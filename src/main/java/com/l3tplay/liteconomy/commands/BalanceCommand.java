package com.l3tplay.liteconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@CommandAlias("money|bal|balance")
@RequiredArgsConstructor
public class BalanceCommand extends BaseCommand {

    private final Liteconomy plugin;

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onBalance(Player player, @Optional OfflinePlayer other) {
        if (other != null && other.hasPlayedBefore()) {
            BigDecimal money = plugin.getStorageManager().getBalance(other).join();
            player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.player.other"))
                    .replace("%money", money.toString())
                    .replace("%player", other.getName()));
            return;
        }

        BigDecimal money = plugin.getStorageManager().getBalance(player).join();
        player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.player.self")).replace("%money", money.toString()));
    }
}
