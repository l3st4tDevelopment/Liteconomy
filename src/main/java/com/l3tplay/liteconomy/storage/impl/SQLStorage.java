package com.l3tplay.liteconomy.storage.impl;

import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.storage.StorageManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

public class SQLStorage extends StorageManager {

    private final Liteconomy plugin;

    private final String prefix;
    private final String insert;
    private final String select;
    private final String update;
    private final String exists;

    private HikariDataSource hikariDataSource;

    public SQLStorage(Liteconomy plugin) {
        super(plugin);
        this.plugin = plugin;

        this.prefix = plugin.getConfig().getString("storage.mysql.prefix");
        this.insert = "INSERT INTO " + this.prefix + "balance VALUES(?,?,?) ON DUPLICATE KEY UPDATE NAME=?";
        this.select = "SELECT MONEY FROM " + this.prefix + "balance WHERE UUID=?";
        this.update = "UPDATE " + this.prefix + "balance SET MONEY=? WHERE UUID=?";
        this.exists = "SELECT EXISTS(SELECT * from " + this.prefix + "balance WHERE UUID=?)";
    }

    @Override
    protected BigDecimal loadPlayerData(OfflinePlayer player, BigDecimal defaultValue) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement(this.insert);
             PreparedStatement select = connection.prepareStatement(this.select)) {

            insert.setString(1, player.getUniqueId().toString());
            insert.setString(2, player.getName());
            insert.setBigDecimal(3, defaultValue);
            insert.setString(4, player.getName());
            insert.execute();

            select.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("MONEY");
            }
            resultSet.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return new BigDecimal(0);
    }

    @Override
    protected void savePlayerData(OfflinePlayer player, BigDecimal balance) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement update = connection.prepareStatement(this.update)) {
            update.setBigDecimal(1, balance);
            update.setString(2, player.getUniqueId().toString());
            update.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean hasAccount(OfflinePlayer player) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement exists = connection.prepareStatement(this.exists)) {
            exists.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = exists.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            resultSet.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected Map<UUID, BigDecimal> sortPlayers() {
        Map<UUID, BigDecimal> sortedMap = new HashMap<>();
        try (Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT UUID,MONEY FROM " + this.prefix + "balance");
            while (resultSet.next()) {
                sortedMap.put(UUID.fromString(resultSet.getString("UUID")), resultSet.getBigDecimal("MONEY").setScale(2, RoundingMode.HALF_EVEN));
            }
            resultSet.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return sortedMap;
    }

    @Override
    public void init() {
        setupHikari();
        createTable();
    }

    private void setupHikari() {
        String host = plugin.getConfig().getString("storage.mysql.host");
        int port = plugin.getConfig().getInt("storage.mysql.port");
        String username = plugin.getConfig().getString("storage.mysql.username");
        String password = plugin.getConfig().getString("storage.mysql.password");
        String database = plugin.getConfig().getString("storage.mysql.database");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariDataSource = new HikariDataSource(config);
    }

    private void createTable() {
        try (Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.prefix + "balance(UUID varchar(36) UNIQUE, NAME varchar(16), MONEY decimal(18,2))");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }
}
