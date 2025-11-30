package com.neocase.neocase;

import org.bukkit.inventory.ItemStack;

public class CaseItem {
    private ItemStack item;
    private double chance;
    private String displayName;
    private boolean broadcast;

    public CaseItem(ItemStack item, double chance, String displayName, boolean broadcast) {
        this.item = item;
        this.chance = chance;
        this.displayName = displayName;
        this.broadcast = broadcast;
    }

    // Геттеры
    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    // Сеттеры
    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
}