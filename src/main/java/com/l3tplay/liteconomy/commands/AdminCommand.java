package com.l3tplay.liteconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@CommandAlias("eco|economy|ecoadmin")
@CommandPermission("liteconomy.admin")
@RequiredArgsConstructor
public class AdminCommand extends BaseCommand {

    private final Liteconomy plugin;

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("add")
    @Syntax("<player> <money>")
    @Description("Add money to a player.")
    @CommandCompletion("@players")
    public void onBalanceAdd(CommandSender sender, OfflinePlayer offlinePlayer, double amount) {
        if (!assertPlayer(sender, offlinePlayer)) {
            return;
        }

        if (!assertNegative(sender, amount)) {
            return;
        }

        if (!plugin.getStorageManager().addBalance(offlinePlayer, amount)) {
            sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.error")));
            return;
        }

        sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.added")
                .replace("%player", offlinePlayer.getName())
                .replace("%balance", amount + "")));
    }

    @Subcommand("take")
    @Syntax("<player> <money>")
    @Description("Take money from a player.")
    @CommandCompletion("@players")
    public void onBalanceTake(CommandSender sender, OfflinePlayer offlinePlayer, double amount) {
        if (!assertPlayer(sender, offlinePlayer)) {
            return;
        }

        if (!assertNegative(sender, amount)) {
            return;
        }

        if (!plugin.getStorageManager().removeBalance(offlinePlayer, amount)) {
            sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.error")));
            return;
        }

        sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.taken")
                .replace("%player", offlinePlayer.getName())
                .replace("%balance", amount + "")));
    }

    @Subcommand("set")
    @Syntax("<player> <money>")
    @Description("Set a player's money.")
    @CommandCompletion("@players")
    public void onBalanceSet(CommandSender sender, OfflinePlayer offlinePlayer, double amount) {
        if (!assertPlayer(sender, offlinePlayer)) {
            return;
        }

        if (!assertNegative(sender, amount)) {
            return;
        }

        if (!plugin.getStorageManager().setBalance(offlinePlayer.getPlayer(), new BigDecimal(amount))) {
            sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.error")));
            return;
        }

        sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.set")
                .replace("%player", offlinePlayer.getName())
                .replace("%balance", amount + "")));
    }

    @Subcommand("reset")
    @Syntax("<player>")
    @Description("Reset a player's money.")
    @CommandCompletion("@players")
    public void onBalanceReset(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!assertPlayer(sender, offlinePlayer)) {
            return;
        }

        plugin.getStorageManager().setBalance(offlinePlayer.getPlayer(), new BigDecimal(0));
        sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.set")
                .replace("%player", offlinePlayer.getName())));
    }

    @Subcommand("updatebaltop")
    public void onBaltopUpdate(CommandSender sender) {
        plugin.getStorageManager().updateBaltop();
        sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.admin.baltopUpdated")));
    }

    private boolean assertPlayer(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.invalidPlayer")));
            return false;
        }

        return true;
    }

    private boolean assertNegative(CommandSender sender, double value) {
        if (value < 0) {
            sender.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.noNegative")));
            return false;
        }

        return true;
    }
}
