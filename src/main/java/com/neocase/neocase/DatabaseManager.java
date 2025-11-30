package com.neocase.neocase;

import com.neocase.neocase.NeoCase;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private final NeoCase plugin;
    private Connection connection;

    public DatabaseManager(NeoCase plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            FileConfiguration config = plugin.getConfig();
            String host = config.getString("database.host");
            int port = config.getInt("database.port");
            String database = config.getString("database.database");
            String username = config.getString("database.username");
            String password = config.getString("database.password");
            String prefix = config.getString("database.table-prefix");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);

            createTables();
            plugin.getLogger().info("Успешное подключение к MySQL!");

        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения к MySQL: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS " + getTable("player_keys") + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "case_id VARCHAR(64) NOT NULL," +
                "key_count INT NOT NULL DEFAULT 0," +
                "UNIQUE KEY unique_player_case (player_uuid, case_id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTable);
        }
    }

    public void addKeys(UUID playerId, String caseId, int amount) {
        String query = "INSERT INTO " + getTable("player_keys") +
                " (player_uuid, case_id, key_count) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE key_count = key_count + ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerId.toString());
            stmt.setString(2, caseId);
            stmt.setInt(3, amount);
            stmt.setInt(4, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка добавления ключей: " + e.getMessage());
        }
    }

    public void removeKey(UUID playerId, String caseId, int amount) {
        String query = "UPDATE " + getTable("player_keys") +
                " SET key_count = key_count - ? WHERE player_uuid = ? AND case_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, amount);
            stmt.setString(2, playerId.toString());
            stmt.setString(3, caseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка удаления ключа: " + e.getMessage());
        }
    }

    public Map<String, Integer> getPlayerKeys(UUID playerId) {
        Map<String, Integer> keys = new HashMap<>();
        String query = "SELECT case_id, key_count FROM " + getTable("player_keys") +
                " WHERE player_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                keys.put(rs.getString("case_id"), rs.getInt("key_count"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка загрузки ключей: " + e.getMessage());
        }

        return keys;
    }

    private String getTable(String table) {
        return plugin.getConfig().getString("database.table-prefix") + table;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка отключения от MySQL: " + e.getMessage());
            }
        }
    }
}