package com.l3tplay.liteconomy.storage.impl;

import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.storage.StorageManager;
import com.l3tplay.liteconomy.utils.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class FileStorage extends StorageManager {

    private final Liteconomy plugin;
    private final ConfigFile dataFile;

    public FileStorage(Liteconomy plugin) {
        super(plugin);
        this.plugin = plugin;
        this.dataFile = new ConfigFile(plugin.getDataFolder(), "data.yml");
    }

    @Override
    protected BigDecimal loadPlayerData(OfflinePlayer player, BigDecimal defaultValue) {
        FileConfiguration config = dataFile.getConfig();
        if (!hasAccount(player)) {
            savePlayerData(player, defaultValue);
        }

        return new BigDecimal(config.getString(player.getUniqueId().toString()));
    }

    @Override
    protected void savePlayerData(OfflinePlayer player, BigDecimal balance) {
        FileConfiguration config = dataFile.getConfig();

        config.set(player.getUniqueId().toString(), balance.toString());
        dataFile.save();
    }

    @Override
    protected boolean hasAccount(OfflinePlayer player) {
        return dataFile.getConfig().getString(player.getUniqueId().toString()) != null;
    }

    @Override
    protected Map<UUID, BigDecimal> sortPlayers() {
        Map<UUID, BigDecimal> map = new HashMap<>();
        for (String key : dataFile.getConfig().getConfigurationSection("").getKeys(false)) {
            map.put(UUID.fromString(key), new BigDecimal(dataFile.getConfig().getString(key)).setScale(2, RoundingMode.HALF_EVEN));
        }

        return map;
    }

    @Override
    public void init() {
        dataFile.createConfig();
    }

    @Override
    public void close() {}

}
