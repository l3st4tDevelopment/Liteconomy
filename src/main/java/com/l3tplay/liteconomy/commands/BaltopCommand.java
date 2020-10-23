package com.l3tplay.liteconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.inventories.BaltopInventory;
import com.l3tplay.liteconomy.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("baltop|balancetop")
@RequiredArgsConstructor
public class BaltopCommand extends BaseCommand {

    private final Liteconomy plugin;

    @Default
    public void onBaltop(Player player) {
        BaltopInventory.getInventory(plugin, new BaltopInventory(plugin)).open(player);
    }
}
