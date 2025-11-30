package com.neocase.neocase;

import com.neocase.neocase.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NeoCase extends JavaPlugin {

    private static NeoCase instance;
    private CaseManager caseManager;
    private KeyManager keyManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        // Сохраняем конфиг
        saveDefaultConfig();

        // Инициализация менеджеров
        this.caseManager = new CaseManager(this);
        this.keyManager = new KeyManager(this);

        // Загрузка кейсов
        caseManager.loadCases();

        // Инициализация БД если включена
        if (getConfig().getBoolean("database.enabled")) {
            this.databaseManager = new DatabaseManager(this);
            databaseManager.connect();
        }

        // Регистрация команды
        getCommand("neocase").setExecutor(new CaseCommand(this));

        getLogger().info("NeoCase успешно запущен!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("NeoCase отключен!");
    }

    public static NeoCase getInstance() { return instance; }
    public CaseManager getCaseManager() { return caseManager; }
    public KeyManager getKeyManager() { return keyManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
}