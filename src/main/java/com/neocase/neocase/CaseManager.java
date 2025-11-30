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
        // ... код создания дефолтных кейсов ...
    }

    private void loadCaseFromFile(File file) {
        // ... код загрузки кейсов из файлов ...
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
        if (!plugin.getKeyManager().hasKey(player, caseId)) {
            player.sendMessage("§cУ вас нет ключей для этого кейса!");
            return;
        }

        // Используем ключ
        plugin.getKeyManager().takeKey(player, caseId);

        // Выбираем анимацию
        CaseAnimation animation;
        String animationType = gameCase.getAnimationType();

        if ("instant".equals(animationType)) {
            animation = new InstantAnimation();
        } else {
            // По умолчанию рулетка
            animation = new RouletteAnimation();
        }

        // ВОТ ЭТА СТРОКА ДОЛЖНА БЫТЬ В КОНЦЕ МЕТОДА!
        animation.playAnimation(player, gameCase);
    }
}