package com.l3tplay.liteconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.inventories.BaltopInventory;
import com.l3tplay.liteconomy.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("baltop|balancetop")
@RequiredArgsConstructor
public class BaltopCommand extends BaseCommand {

    private final Liteconomy plugin;

    @Default
    public void onBaltop(Player player) {
        player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.loadingBaltop")));
        plugin.newChain().asyncFirst(() -> {
            Map<OfflinePlayer, BigDecimal> topPlayers = new LinkedHashMap<>();

            for (Map.Entry<UUID, BigDecimal> entry : plugin.getStorageManager().getBaltop().entrySet()) {
                topPlayers.put(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
            }

            return topPlayers;
        }).syncLast((topPlayers) ->
                BaltopInventory.getInventory(plugin, new BaltopInventory(plugin, topPlayers)).open(player)).execute();
    }
}
