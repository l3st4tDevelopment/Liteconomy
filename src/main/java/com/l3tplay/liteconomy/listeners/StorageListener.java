package com.l3tplay.liteconomy.listeners;

import com.l3tplay.liteconomy.Liteconomy;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class StorageListener implements Listener {

    private final Liteconomy plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getStorageManager().loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getStorageManager().savePlayerAsync(event.getPlayer());
    }
}
