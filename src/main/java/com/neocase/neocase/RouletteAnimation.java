package com.neocase.neocase;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RouletteAnimation extends CaseAnimation {
    @Override
    public void playAnimation(Player player, GameCase gameCase) {
        player.sendMessage("§6🎰 Запускаем рулетку...");

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 60; // 3 секунды (20 ticks = 1 секунда)

            @Override
            public void run() {
                if (ticks++ >= maxTicks) {
                    // Завершение анимации
                    CaseItem reward = gameCase.getRandomItem();
                    giveReward(player, reward);

                    // Широкое оповещение
                    if (reward != null && reward.isBroadcast()) {
                        String itemName = reward.getDisplayName() != null ?
                                reward.getDisplayName() :
                                reward.getItem().getType().toString();

                        NeoCase.getInstance().getServer().broadcastMessage(
                                "§6🎉 " + player.getName() + " выиграл(а) " + itemName + " §6из кейса!"
                        );
                    }

                    cancel();
                    return;
                }

                // Анимация процесса (каждую секунду)
                if (ticks % 20 == 0) {
                    int secondsPassed = ticks / 20;
                    int secondsLeft = (maxTicks - ticks) / 20;
                    player.sendMessage("§e⚡ Крутим... " + secondsLeft + "с");
                }
            }
        }.runTaskTimer(NeoCase.getInstance(), 0L, 1L);
    }
}