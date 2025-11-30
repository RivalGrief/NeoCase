package com.neocase.neocase;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;

public class CaseInventory {
    public static Inventory createCaseMenu() {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Кейсы");

        // Теперь getCases() работает!
        Collection<GameCase> cases = NeoCase.getInstance().getCaseManager().getCases();

        for (GameCase gameCase : cases) {
            ItemStack caseItem = ItemBuilder.createCaseItem(gameCase);
            inv.addItem(caseItem);
        }

        return inv;
    }
}