package com.neocase.neocase;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ItemBuilder {
    public static ItemStack createCaseItem(GameCase gameCase) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6" + gameCase.getName());
        meta.setLore(Arrays.asList(
                "§7Нажми ПКМ чтобы открыть",
                "§eСодержит уникальные предметы!"
        ));

        item.setItemMeta(meta);
        return item;
    }
}