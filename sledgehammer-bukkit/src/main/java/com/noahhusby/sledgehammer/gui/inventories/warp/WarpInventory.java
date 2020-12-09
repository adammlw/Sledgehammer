/*
 * Copyright (c) 2020 Noah Husby
 * sledgehammer - WarpInventory.java
 *
 * Sledgehammer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sledgehammer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Sledgehammer.  If not, see <https://github.com/noahhusby/Sledgehammer/blob/master/LICENSE/>.
 */

package com.noahhusby.sledgehammer.gui.inventories.warp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.noahhusby.sledgehammer.Constants;
import com.noahhusby.sledgehammer.SledgehammerUtil;
import com.noahhusby.sledgehammer.data.warp.Warp;
import com.noahhusby.sledgehammer.gui.inventories.general.GUIChild;
import com.noahhusby.sledgehammer.gui.inventories.general.GUIHelper;
import com.noahhusby.sledgehammer.gui.inventories.general.GUIRegistry;
import com.noahhusby.sledgehammer.gui.inventories.warp.config.WarpConfigController;
import com.noahhusby.sledgehammer.network.S2P.S2PWarpPacket;
import com.noahhusby.sledgehammer.network.S2P.S2PWebMapPacket;
import com.noahhusby.sledgehammer.network.SledgehammerNetworkManager;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Skull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WarpInventory extends GUIChild {
    private final int page;
    private final List<Warp> warps;

    private Inventory inventory;

    public WarpInventory(int page, List<Warp> warps) {
        this.page = page;
        this.warps = warps;
    }

    @Override
    public void init() {
        this.inventory = getInventory();
        int total_pages = (int) Math.ceil(warps.size() / 27.0);

        for(int x = 0; x < 54; x++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);

            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(ChatColor.RESET+"");
            meta.setDisplayName(null);
            glass.setItemMeta(meta);

            inventory.setItem(x, glass);
        }

        inventory.setItem(4, SledgehammerUtil.getSkull(Constants.globeHead, ChatColor.GREEN + "" + ChatColor.BOLD + "All Warps"));
        //inventory.setItem(4, SledgehammerUtil.getSkull(Constants.monitorHead, ChatColor.GRAY + "" + ChatColor.BOLD + "All Warps"));
        //inventory.setItem(48, SledgehammerUtil.getSkull(Constants.lampHead, ChatColor.GOLD + "" + ChatColor.BOLD + "Pinned Warps"));
        inventory.setItem(40, GUIHelper.generateCompass());
        inventory.setItem(49, GUIHelper.generateExit());

        ItemStack sort = new ItemStack(Material.HOPPER, 1);
        ItemMeta sortMeta = sort.getItemMeta();
        sortMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Sort");
        sort.setItemMeta(sortMeta);
        inventory.setItem(45, sort);

        inventory.setItem(46, SledgehammerUtil.getSkull(Constants.lampHead, ChatColor.GOLD + "" + ChatColor.BOLD
            + "Show pinned warps"));

        if(((WarpInventoryController) getController()).getPayload().isEditAccess()) {
            ItemStack anvil = new ItemStack(Material.ANVIL, 1);
            ItemMeta meta = anvil.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Configure Warps");
            anvil.setItemMeta(meta);
            inventory.setItem(47, anvil);
        }

        boolean paged = false;
        if(page != 0) {
            ItemStack head = SledgehammerUtil.getSkull(Constants.arrowLeftHead, ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page");
            inventory.setItem(51, head);
            paged = true;
        }

        if(warps.size() > (page + 1) * Constants.warpsPerPage) {
            ItemStack head = SledgehammerUtil.getSkull(Constants.arrowRightHead, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page");
            inventory.setItem(53, head);
            paged = true;
        }

        if(paged) {
            inventory.setItem(52, SledgehammerUtil.NumberHeads.getHead((page + 1), ChatColor.GREEN +
                    "" + ChatColor.BOLD + "Page " + (page + 1)));
        }

        int min = page * 27;
        int max = min + 27;

        if(max > warps.size()) {
            max = min + (warps.size() - (page * 27));
        }

        int current = 9;
        for(int x = min; x < max; x++) {
            Warp warp = warps.get(x);

            String headId = warp.getHeadID();
            if(headId.equals("")) headId = Constants.cyanWoolHead;
            ItemStack item = SledgehammerUtil.getSkull(headId, ((warp.getPinnedMode() == Warp.PinnedMode.GLOBAL
            || warp.getPinnedMode() == Warp.PinnedMode.LOCAL) ? ChatColor.GOLD : ChatColor.BLUE)
                    + "" + ChatColor.BOLD + warp.getName());

            ItemMeta meta = item.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + "------------------");
            lore.add(ChatColor.DARK_GRAY + "Server: " + warp.getServer());
            lore.add(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "Click to warp.");
            lore.add(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + "------------------");
            lore.add(ChatColor.GRAY + "ID: " + warp.getId());
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(current, item);
            current++;
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        WarpInventoryController controller = (WarpInventoryController) getController();

        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        if(e.getSlot() == 45) {
            controller.close();
            GUIRegistry.register(new WarpSortInventoryController(getPlayer(), controller.getPayload()));
            return;
        }

        if(e.getSlot() == 46) {
            GUIRegistry.register(new PinnedWarpInventoryController(getPlayer(), controller.getPayload()));
            return;
        }

        if(e.getSlot() == 47 && controller.getPayload().isEditAccess()) {
            controller.close();
            GUIRegistry.register(new WarpConfigController(getPlayer()));
            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Previous Page")) {
            controller.openChild(controller.getChildByPage(page-1));
            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next Page")) {
            controller.openChild(controller.getChildByPage(page+1));
            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Close")) {
            controller.close();
            return;
        }

        if(e.getSlot() == 40) {
            GUIRegistry.register(new GroupListWarpInventoryController(getController(), controller.getPayload()));
            return;
        }

        if(e.getSlot() > 8 && e.getSlot() < 36) {
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            int id = -1;
            List<String> lore = meta.getLore();
            for(String s : lore) {
                if(s.contains("ID:"))
                    id = new Long(ChatColor.stripColor(s).replaceAll("[^\\d.]", "")).intValue();
            }

            SledgehammerNetworkManager.getInstance().sendPacket(new S2PWarpPacket(player, id));

            controller.close();
            return;
        }

        /**
        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Open Web Map")) {
            SledgehammerNetworkManager.getInstance().sendPacket(new S2PWebMapPacket(player));
            controller.close();
            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Pinned Warps")) {
            controller.switchToPinned();
            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Server Warps")) {
            controller.switchToServerList();
            return;
        }
         */
    }

    public int getPage() {
        return page;
    }
}