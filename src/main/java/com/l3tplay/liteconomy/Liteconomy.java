package com.l3tplay.liteconomy;

import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.l3tplay.liteconomy.commands.AdminCommand;
import com.l3tplay.liteconomy.commands.BalanceCommand;
import com.l3tplay.liteconomy.commands.BaltopCommand;
import com.l3tplay.liteconomy.commands.PayCommand;
import com.l3tplay.liteconomy.hooks.LiteconomyHook;
import com.l3tplay.liteconomy.listeners.StorageListener;
import com.l3tplay.liteconomy.storage.StorageManager;
import com.l3tplay.liteconomy.storage.impl.FileStorage;
import com.l3tplay.liteconomy.storage.impl.SQLStorage;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Liteconomy extends JavaPlugin {

    private TaskChainFactory taskChainFactory;

    @Getter private StorageManager storageManager;
    @Getter private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.taskChainFactory = BukkitTaskChainFactory.create(this);

        switch (getConfig().getString("storage.type").toLowerCase()) {
            case "mysql": {
                this.storageManager = new SQLStorage(this);
                break;
            }
            default: {
                this.storageManager = new FileStorage(this);
                break;
            }
        }

        storageManager.init();
        storageManager.updateBaltop();

        this.inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new AdminCommand(this));
        commandManager.registerCommand(new BalanceCommand(this));
        commandManager.registerCommand(new BaltopCommand(this));
        commandManager.registerCommand(new PayCommand(this));
        commandManager.enableUnstableAPI("help");

        Bukkit.getScheduler().runTaskTimer(this, () -> storageManager.updateBaltop(), 0L, getConfig().getInt("settings.schedulers.baltop") * 20);

        Bukkit.getScheduler().runTaskTimer(this, () -> storageManager.autosave(), 0L, getConfig().getInt("settings.schedulers.autosave") * 20);

        Bukkit.getServicesManager().register(Economy.class, new LiteconomyHook(this), this, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(new StorageListener(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(storageManager::savePlayer);
        storageManager.close();
    }

    public <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
}
