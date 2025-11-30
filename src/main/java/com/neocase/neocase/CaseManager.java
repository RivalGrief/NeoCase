package com.neocase.neocase;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.*;

public class CaseManager {
    private final NeoCase plugin;
    private final Map<String, GameCase> cases = new HashMap<>();

    public CaseManager(NeoCase plugin) {
        this.plugin = plugin;
    }

    public void loadCases() {
        cases.clear();

        File casesFolder = new File(plugin.getDataFolder(), "cases");
        if (!casesFolder.exists()) {
            casesFolder.mkdirs();
            createDefaultCases();
        }

        File[] caseFiles = casesFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (caseFiles == null || caseFiles.length == 0) {
            plugin.getLogger().warning("В папке cases нет файлов кейсов!");
            createDefaultCases();
            return;
        }

        for (File file : caseFiles) {
            loadCaseFromFile(file);
        }

        plugin.getLogger().info("Загружено " + cases.size() + " кейсов!");
    }

    private void createDefaultCases() {
        plugin.getLogger().info("Создаем стандартные кейсы...");

        File casesFolder = new File(plugin.getDataFolder(), "cases");
        if (!casesFolder.exists()) {
            casesFolder.mkdirs();
        }

        // Создаем default.yml
        File defaultCase = new File(casesFolder, "default.yml");
        if (!defaultCase.exists()) {
            try {
                defaultCase.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(defaultCase);
                config.set("name", "&6Обычный Кейс");
                config.set("animation", "roulette");

                List<Map<String, Object>> items = new ArrayList<>();

                Map<String, Object> diamond = new HashMap<>();
                diamond.put("material", "DIAMOND");
                diamond.put("amount", 1);
                diamond.put("chance", 5.0);
                diamond.put("display-name", "&bАлмаз");
                diamond.put("broadcast", true);
                items.add(diamond);

                Map<String, Object> gold = new HashMap<>();
                gold.put("material", "GOLD_INGOT");
                gold.put("amount", 5);
                gold.put("chance", 15.0);
                gold.put("display-name", "&6Золотые слитки");
                gold.put("broadcast", false);
                items.add(gold);

                Map<String, Object> sword = new HashMap<>();
                sword.put("material", "IRON_SWORD");
                sword.put("amount", 1);
                sword.put("chance", 30.0);
                sword.put("display-name", "&7Железный меч");
                sword.put("broadcast", false);
                items.add(sword);

                config.set("items", items);
                config.save(defaultCase);

            } catch (Exception e) {
                plugin.getLogger().severe("Ошибка создания default.yml: " + e.getMessage());
            }
        }

        // Перезагружаем кейсы после создания
        loadCases();
    }

    private void loadCaseFromFile(File file) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String caseId = file.getName().replace(".yml", "");

            String name = config.getString("name", "Кейс " + caseId);
            String animation = config.getString("animation", "roulette");

            // Загружаем предметы кейса
            List<CaseItem> caseItems = new ArrayList<>();
            List<Map<?, ?>> itemsList = config.getMapList("items");

            for (Map<?, ?> itemMap : itemsList) {
                try {
                    String materialStr = (String) itemMap.get("material");

                    // БЕЗОПАСНОЕ ПОЛУЧЕНИЕ ЗНАЧЕНИЙ С ПРОВЕРКАМИ
                    Object amountObj = itemMap.get("amount");
                    int amount = (amountObj instanceof Integer) ? (Integer) amountObj : 1;

                    Object chanceObj = itemMap.get("chance");
                    double chance = (chanceObj instanceof Number) ? ((Number) chanceObj).doubleValue() : 10.0;

                    String displayName = (String) itemMap.get("display-name");
                    if (displayName == null) {
                        displayName = "";
                    }

                    Object broadcastObj = itemMap.get("broadcast");
                    boolean broadcast = (broadcastObj instanceof Boolean) ? (Boolean) broadcastObj : false;

                    // Создает ItemStack
                    ItemStack item = new ItemStack(org.bukkit.Material.valueOf(materialStr), amount);

                    CaseItem caseItem = new CaseItem(item, chance, displayName, broadcast);
                    caseItems.add(caseItem);

                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка загрузки предмета в кейсе " + caseId + ": " + e.getMessage());
                }
            }

            GameCase gameCase = new GameCase(caseId, name);
            gameCase.setItems(caseItems);
            gameCase.setAnimationType(animation);

            cases.put(caseId, gameCase);
            plugin.getLogger().info("Загружен кейс: " + name + " (" + caseItems.size() + " предметов)");

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка загрузки кейса из файла " + file.getName() + ": " + e.getMessage());
        }
    }

    public GameCase getCase(String id) {
        return cases.get(id);
    }

    public Collection<GameCase> getCases() {
        return cases.values();
    }

    public void openCaseForPlayer(Player player, String caseId) {
        GameCase gameCase = getCase(caseId);
        if (gameCase == null) {
            player.sendMessage("§cКейс не найден!");
            return;
        }

        // Проверяем есть ли ключи у игрока
        if (plugin.getConfig().getBoolean("settings.require-keys", true) &&
                !plugin.getKeyManager().hasKey(player, caseId)) {
            player.sendMessage("§cУ вас нет ключей для этого кейса!");
            return;
        }

        // Используем ключ
        if (plugin.getConfig().getBoolean("settings.require-keys", true)) {
            plugin.getKeyManager().takeKey(player, caseId);
        }

        // Выбираем анимацию
        CaseAnimation animation = createAnimation(gameCase.getAnimationType());

        // Запускаем анимацию
        animation.playAnimation(player, gameCase);
    }

    private CaseAnimation createAnimation(String type) {
        if (type == null) {
            return new RouletteAnimation();
        }

        switch (type.toLowerCase()) {
            case "instant":
                return new InstantAnimation();
            case "firework":
                return new FireworkAnimation();
            case "spin":
                // Можно добавить позже
                return new RouletteAnimation();
            case "roulette":
            default:
                return new RouletteAnimation();
        }
    }

    // Метод для открытия кейса без проверки ключей (для админов)
    public void openCaseForPlayerForce(Player player, String caseId) {
        GameCase gameCase = getCase(caseId);
        if (gameCase == null) {
            player.sendMessage("§cКейс не найден!");
            return;
        }

        CaseAnimation animation = createAnimation(gameCase.getAnimationType());
        animation.playAnimation(player, gameCase);
    }

    // Получить все ID кейсов
    public Set<String> getCaseIds() {
        return cases.keySet();
    }

    // Проверить существует ли кейс
    public boolean caseExists(String caseId) {
        return cases.containsKey(caseId);
    }

    // Перезагрузить конкретный кейс
    public void reloadCase(String caseId) {
        File caseFile = new File(plugin.getDataFolder(), "cases/" + caseId + ".yml");
        if (caseFile.exists()) {
            loadCaseFromFile(caseFile);
        }
    }
}