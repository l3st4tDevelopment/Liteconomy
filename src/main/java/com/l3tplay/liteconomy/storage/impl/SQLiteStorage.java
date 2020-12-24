package com.l3tplay.liteconomy.storage.impl;

import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.storage.StorageManager;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SQLiteStorage extends StorageManager {

    private final Liteconomy plugin;
    private Connection connection;
    private final String connectionUrl;

    private static final String insert = "INSERT INTO PlayerBalance VALUES(?,?,?) ON CONFLICT(UUID) DO UPDATE SET NAME=?";
    private static final String select = "SELECT MONEY FROM PlayerBalance WHERE UUID=?";
    private static final String save = "UPDATE PlayerBalance SET MONEY=? WHERE UUID=?";
    private static final String exists = "SELECT EXISTS(SELECT * from PlayerBalance WHERE UUID=?)";

    public SQLiteStorage(Liteconomy plugin) {
        super(plugin);
        this.plugin = plugin;
        this.connectionUrl = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "liteconomy.db";
    }

    @Override
    public void init() {
        connect(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlayerBalance(UUID varchar(36) UNIQUE, NAME varchar(16), MONEY decimal(18,2))");
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void connect(Consumer<Connection> callback) {
        if (this.connection == null) {
            try {
                this.connection = DriverManager.getConnection(this.connectionUrl);
            } catch (SQLException e) {
                plugin.getLogger().severe("An error occurred retrieving the SQLite database connection: " + e.getMessage());
            }
        }

        try {
            callback.accept(connection);
        }catch (Exception e) {
            plugin.getLogger().severe("An error occurred while executing a SQLite query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected BigDecimal loadPlayerData(OfflinePlayer player, BigDecimal defaultValue) {
        AtomicReference<BigDecimal> returnValue = new AtomicReference<>(new BigDecimal(0));

        connect(connection -> {
            try (PreparedStatement insert = connection.prepareStatement(this.insert);
            PreparedStatement select = connection.prepareStatement(this.select)) {

                insert.setString(1, player.getUniqueId().toString());
                insert.setString(2, player.getName());
                insert.setBigDecimal(3, defaultValue);
                insert.setString(4, player.getName());
                insert.execute();

                select.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = select.executeQuery();
                if (resultSet.next()) {
                    returnValue.set(resultSet.getBigDecimal("MONEY"));
                }
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return returnValue.get();
    }

    @Override
    protected void savePlayerData(OfflinePlayer player, BigDecimal balance) {
        connect(connection -> {
            try (PreparedStatement update = connection.prepareStatement(this.save)) {
                update.setBigDecimal(1, balance);
                update.setString(2, player.getUniqueId().toString());
                update.execute();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected boolean hasAccount(OfflinePlayer player) {
        AtomicBoolean hasAccount = new AtomicBoolean();
        connect(connection -> {
            try (PreparedStatement exists = connection.prepareStatement(this.exists)) {
                exists.setString(1, player.getUniqueId().toString());

                ResultSet resultSet = exists.executeQuery();
                if (resultSet.next()) {
                    hasAccount.set(true);
                }
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return hasAccount.get();
    }

    @Override
    protected Map<UUID, BigDecimal> sortPlayers() {
        Map<UUID, BigDecimal> sortedMap = new HashMap<>();
        connect(connection -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT UUID,MONEY FROM PlayerBalance ORDER BY MONEY DESC LIMIT " + plugin.getConfig().getInt("settings.baltopLimit"));
                while (resultSet.next()) {
                    sortedMap.put(UUID.fromString(resultSet.getString("UUID")), resultSet.getBigDecimal("MONEY").setScale(2, RoundingMode.HALF_EVEN));
                }
                resultSet.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return sortedMap;
    }

    @Override
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            this.plugin.getLogger().severe("An error occurred closing the SQLite database connection: " + ex.getMessage());
        }
    }
}
