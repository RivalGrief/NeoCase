package com.neocase.neocase;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyManager {
    private final NeoCase plugin;
    private final Map<UUID, Map<String, Integer>> playerKeys = new HashMap<>();

    public KeyManager(NeoCase plugin) {
        this.plugin = plugin;
    }

    public void giveKey(Player player, String caseId, int amount) {
        UUID playerId = player.getUniqueId();
        playerKeys.putIfAbsent(playerId, new HashMap<>());

        Map<String, Integer> keys = playerKeys.get(playerId);
        keys.put(caseId, keys.getOrDefault(caseId, 0) + amount);

        // Сохраняем в БД если включена
        if (plugin.getDatabaseManager() != null) {
            plugin.getDatabaseManager().addKeys(playerId, caseId, amount);
        }

        // Сообщение игроку
        String caseName = plugin.getCaseManager().getCase(caseId).getName();
        player.sendMessage("§aВы получили §e" + amount + " §aключей для кейса §6" + caseName + "§a!");
    }

    public boolean hasKey(Player player, String caseId) {
        return getKeys(player, caseId) > 0;
    }

    public int getKeys(Player player, String caseId) {
        Map<String, Integer> keys = playerKeys.get(player.getUniqueId());
        return keys != null ? keys.getOrDefault(caseId, 0) : 0;
    }

    public void takeKey(Player player, String caseId) {
        UUID playerId = player.getUniqueId();
        if (playerKeys.containsKey(playerId)) {
            Map<String, Integer> keys = playerKeys.get(playerId);
            int current = keys.getOrDefault(caseId, 0);
            if (current > 0) {
                keys.put(caseId, current - 1);

                // Обновляем БД
                if (plugin.getDatabaseManager() != null) {
                    plugin.getDatabaseManager().removeKey(playerId, caseId, 1);
                }
            }
        }
    }

    public void loadPlayerKeys(UUID playerId, Map<String, Integer> keys) {
        playerKeys.put(playerId, keys);
    }
}