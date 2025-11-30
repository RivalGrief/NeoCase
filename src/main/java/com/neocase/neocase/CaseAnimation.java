package com.neocase.neocase;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CaseAnimation {
    public abstract void playAnimation(Player player, GameCase gameCase);

    protected void giveReward(Player player, CaseItem item) {
        if (item != null && item.getItem() != null) {
            ItemStack reward = item.getItem().clone();

            // Выдаем предмет игроку
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(reward);
            } else {
                // Если инвентарь полный, бросаем под ноги
                player.getWorld().dropItem(player.getLocation(), reward);
                player.sendMessage("§eПредмет выпал на землю (инвентарь полный)");
            }

            // Сообщение игроку
            String itemName = item.getDisplayName();
            if (itemName == null || itemName.isEmpty()) {
                itemName = "§f" + reward.getType().toString();
            }
            player.sendMessage("§aВы выиграли: " + itemName);
        }
    }
}