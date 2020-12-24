package com.l3tplay.liteconomy.inventories;

import com.l3tplay.liteconomy.Liteconomy;
import com.l3tplay.liteconomy.utils.ColorUtils;
import com.l3tplay.liteconomy.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class BaltopInventory implements InventoryProvider {

    private final Liteconomy plugin;
    private final Map<OfflinePlayer, BigDecimal> topPlayers;

    public static SmartInventory getInventory(Liteconomy plugin, BaltopInventory inventory) {
        return SmartInventory.builder()
                .id("baltopInventory")
                .manager(plugin.getInventoryManager())
                .provider(inventory)
                .size(5, 9)
                .title(ColorUtils.colorString(plugin.getConfig().getString("baltopMenu.name"))).build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        List<ClickableItem> items = new ArrayList<>();
        for (Map.Entry<OfflinePlayer, BigDecimal> top : topPlayers.entrySet()) {
            List<String> lore = ColorUtils.colorList(plugin.getConfig().getStringList("baltopMenu.playerItem.lore"));
            lore.replaceAll(text -> {
                text = text.replace("%place", items.size() + 1 + "");
                text = text.replace("%balance", top.getValue().toString());

                return text;
            });

            items.add(ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD)
                    .setName(ColorUtils.colorString(plugin.getConfig().getString("baltopMenu.playerItem.name"))
                            .replace("%player", top.getKey().getName()))
                    .setOwningPlayer(top.getKey())
                    .setLore(lore).build()));
        }

        pagination.setItems(items.toArray(new ClickableItem[0]));
        pagination.setItemsPerPage(27);

        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL,1, 1)
                .blacklist(1, 8)
                .blacklist(2, 0)
                .blacklist(2, 8)
                .blacklist(3, 0)
                .blacklist(3, 8);

        pagination.addToIterator(iterator);

        if (!pagination.isLast()) {
            contents.set(4, 5, ClickableItem.of(new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("baltopMenu.nextPage.material")))
                                        .setName(ColorUtils.colorString(plugin.getConfig().getString("baltopMenu.nextPage.name")))
                                        .setLore(ColorUtils.colorList(plugin.getConfig().getStringList("baltopMenu.nextPage.lore"))).build(), event -> {
                getInventory(plugin, this).open(player, pagination.next().getPage());
            }));
        }

        if (!pagination.isFirst()) {
            contents.set(4, 3, ClickableItem.of(new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("baltopMenu.previousPage.material")))
                    .setName(ColorUtils.colorString(plugin.getConfig().getString("baltopMenu.previousPage.name")))
                    .setLore(ColorUtils.colorList(plugin.getConfig().getStringList("baltopMenu.previousPage.lore"))).build(), event -> {
                getInventory(plugin, this).open(player, pagination.previous().getPage());
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {}
}
