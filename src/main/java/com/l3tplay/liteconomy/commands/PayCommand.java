package com.l3tplay.liteconomy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("pay")
@RequiredArgsConstructor
public class PayCommand extends BaseCommand {

    private final Liteconomy plugin;

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onPay(Player player, OfflinePlayer offlinePlayer, double money) {
        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.invalidPlayer")));
            return;
        }

        if (money < 0) {
            player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.noNegative")));
            return;
        }

        plugin.newChain().asyncFirst(() -> plugin.getStorageManager().isAccountCreated(offlinePlayer))
                .syncLast((accountCreated) -> {
                   if (!accountCreated) {
                       player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.invalidPlayer")));
                       return;
                   }

                   if (plugin.getStorageManager().getBalance(player).join().doubleValue() < money) {
                       player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.pay.notEnoughMoney")));
                       return;
                   }

                   plugin.getStorageManager().removeBalance(player, money);
                   plugin.getStorageManager().addBalance(offlinePlayer, money);

                   player.sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.pay.sent"))
                           .replace("%player", offlinePlayer.getName())
                            .replace("%money", money + ""));

                   if (offlinePlayer.isOnline()) {
                        offlinePlayer.getPlayer().sendMessage(ColorUtils.colorString(plugin.getConfig().getString("messages.balance.pay.received"))
                               .replace("%player", player.getName())
                               .replace("%money", money + ""));
                   }
                }).execute();
    }
}
