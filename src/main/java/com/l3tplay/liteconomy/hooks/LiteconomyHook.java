package com.l3tplay.liteconomy.hooks;

import com.l3tplay.liteconomy.Liteconomy;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class LiteconomyHook implements Economy {

    private final Liteconomy plugin;

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getConfig().getString("settings.currency.plural");
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("settings.currency.singular");
    }

    @Override
    public boolean hasAccount(String s) {
        return hasAccount(Bukkit.getOfflinePlayer(s));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return plugin.getStorageManager().isAccountCreated(offlinePlayer);
    }

    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
        return plugin.getStorageManager().isAccountCreated(offlinePlayer);
    }

    @Override
    public double getBalance(String player) {
        return getBalance(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue();
    }

    @Override
    public double getBalance(String player, String world) {
        return getBalance(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue();
    }

    @Override
    public boolean has(String player, double value) {
        return has(Bukkit.getOfflinePlayer(player), value);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double value) {
        return plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue() >= value;
}

    @Override
    public boolean has(String player, String world, double value) {
        return has(Bukkit.getOfflinePlayer(player), world, value);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String world, double value) {
        return plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue() >= value;
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, double value) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(player), value);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double value) {
        boolean success = plugin.getStorageManager().removeBalance(offlinePlayer, value);
        double balance = plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue();

        if (success) {
            return new EconomyResponse(value, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(value, balance, EconomyResponse.ResponseType.FAILURE, "Invalid player or not enough funds.");
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double value) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(player), world, value);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double value) {
        return withdrawPlayer(offlinePlayer, value);
    }

    @Override
    public EconomyResponse depositPlayer(String player, double value) {
        return depositPlayer(Bukkit.getOfflinePlayer(player), value);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double value) {
        boolean success = plugin.getStorageManager().addBalance(offlinePlayer, value);
        double balance = plugin.getStorageManager().getBalance(offlinePlayer).join().doubleValue();

        if (success) {
            return new EconomyResponse(value, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(value, balance, EconomyResponse.ResponseType.FAILURE, "Invalid player or negative funds.");
    }

    @Override
    public EconomyResponse depositPlayer(String player, String world, double value) {
        return depositPlayer(Bukkit.getOfflinePlayer(player), world, value);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double value) {
        return depositPlayer(offlinePlayer, value);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String player) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) {
            return false;
        }

        plugin.getStorageManager().createAccount(offlinePlayer);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
