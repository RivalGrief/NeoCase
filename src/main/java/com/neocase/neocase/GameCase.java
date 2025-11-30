package com.neocase.neocase;

import java.util.ArrayList;
import java.util.List;

public class GameCase {
    private final String id;
    private final String name;
    private List<CaseItem> items = new ArrayList<>();
    private String animationType = "roulette";

    public GameCase(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public List<CaseItem> getItems() { return items; }
    public String getAnimationType() { return animationType; }

    // Сеттеры
    public void setItems(List<CaseItem> items) { this.items = items; }
    public void setAnimationType(String animationType) { this.animationType = animationType; }

    public CaseItem getRandomItem() {
        if (items == null || items.isEmpty()) return null;

        double totalWeight = items.stream().mapToDouble(CaseItem::getChance).sum();
        double random = Math.random() * totalWeight;
        double current = 0;

        for (CaseItem item : items) {
            current += item.getChance();
            if (random <= current) {
                return item;
            }
        }

        return items.get(0);
    }
}