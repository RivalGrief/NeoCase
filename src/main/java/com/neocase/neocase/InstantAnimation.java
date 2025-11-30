package com.neocase.neocase;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InstantAnimation extends CaseAnimation {
    @Override
    public void playAnimation(Player player, GameCase gameCase) {
        player.sendMessage("§a✨ Мгновенное вскрытие...");

        // Немедленно выдает награду
        CaseItem reward = gameCase.getRandomItem();
        if (reward != null) {
            giveReward(player, reward);

            // Широкое оповещение для редких предметов
            if (reward.isBroadcast()) {
                String itemName = reward.getDisplayName() != null ?
                        reward.getDisplayName() :
                        reward.getItem().getType().toString();

                NeoCase.getInstance().getServer().broadcastMessage(
                        "§6🎉 " + player.getName() + " выиграл(а) " + itemName + " §6из кейса!"
                );
            }
        }
    }
}