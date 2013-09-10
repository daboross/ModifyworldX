/*
 * Modifyworld - PermissionsEx ruleset plugin for Bukkit
 * Copyright (C) 2011 t3hk0d3 http://www.tehkode.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.daboross.modifyworldx.handlers;

import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.Plugin;
import static net.daboross.modifyworldx.PermissionsHelper.assemblePermission;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    protected boolean checkInventory = false;

    public PlayerListener(Plugin plugin, ConfigurationSection config) {
        this.checkInventory = config.getBoolean("item-restrictions", this.checkInventory);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBedEnter(PlayerBedEnterEvent evt) {
        if (!evt.getPlayer().hasPermission("usebeds")) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        String name;
        Material bucket = event.getBucket();
        if (bucket == Material.WATER_BUCKET) {
            name = "water";
        } else if (bucket == Material.LAVA_BUCKET) {
            name = "lava";
        } else if (bucket == Material.MILK_BUCKET) {
            name = "milk";
        } else {
            name = bucket.name().toLowerCase().replace("_bucket", "");
        }
        if (!event.getPlayer().hasPermission(assemblePermission("bucket.empty", name))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketFill(PlayerBucketFillEvent evt) {
        Material result = evt.getItemStack().getType();
        String name;
        if (result == Material.WATER_BUCKET) {
            name = "water";
        } else if (result == Material.LAVA_BUCKET) {
            name = "lava";
        } else if (result == Material.MILK_BUCKET) {
            name = "milk";
        } else {
            name = result.name().toLowerCase();
        }
        if (!evt.getPlayer().hasPermission(assemblePermission("bucket.fill", name))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        // No inform to avoid spam
        if (!evt.getPlayer().hasPermission(assemblePermission("items.pickup", evt.getItem().getItemStack()))) {
            evt.setCancelled(true);
        }

        this.checkPlayerInventory(evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        if (!evt.getPlayer().hasPermission(assemblePermission("items.drop", evt.getItemDrop().getItemStack()))) {
            evt.setCancelled(true);
        }
        this.checkPlayerInventory(evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player || // do not track inter-inventory stuff
                event.getRawSlot() >= event.getView().getTopInventory().getSize() || // top inventory only
                event.getSlotType() == InventoryType.SlotType.OUTSIDE || // do not track drop
                event.getSlot() == -999) { // temporary fix for bukkit bug (BUKKIT-2768)
            return;
        }
        ItemStack take = event.getCurrentItem();
        String action;
        ItemStack item;

        if (take == null) {
            action = "put";
            item = event.getCursor();
        } else {
            action = "take";
            item = take;
        }
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(assemblePermission("items." + action, item))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        if (evt.getPlayer().hasPermission(assemblePermission("interact", evt.getRightClicked()))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Action action = evt.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) { // item restriction check
            this.checkPlayerInventory(evt.getPlayer());
        }
        Player player = evt.getPlayer();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) { //RIGHT_CLICK_AIR is cancelled by default.
            switch (player.getItemInHand().getType()) {
                case POTION: //Only check splash potions.
                    if ((player.getItemInHand().getDurability() & 0x4000) != 0x4000) {
                        break;
                    }
                case EGG:
                case SNOW_BALL:
                case EXP_BOTTLE:
                    if (!player.hasPermission(assemblePermission("items.throw", player.getItemInHand()))) {
                        evt.setUseItemInHand(Result.DENY);
                        //Denying a potion works fine, but the client needs to be updated because it already reduced the item.
                        if (player.getItemInHand().getType() == Material.POTION) {
                            evt.getPlayer().updateInventory();
                        }
                    }
                    return; // no need to check further
                case MONSTER_EGG: // don't add MONSTER_EGGS here
                    if (!player.hasPermission(assemblePermission("spawn", ((SpawnEgg) player.getItemInHand().getData()).getSpawnedType()))) {
                        evt.setUseItemInHand(Result.DENY);
                    }
                    return; // no need to check further
            }
        }
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL) {
            if (!evt.isCancelled() && !player.hasPermission(assemblePermission("blocks.interact", evt.getClickedBlock()))) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemEnchant(EnchantItemEvent evt) {
        if (!evt.getEnchanter().hasPermission(assemblePermission("items.enchant", evt.getItem()))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(assemblePermission("items.craft", event.getRecipe().getResult()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player) && ((Player) event.getEntity()).hasPermission("digestion")) {
            event.setCancelled(true);
        }
    }

    protected void checkPlayerInventory(Player player) {
        if (!checkInventory) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && !player.hasPermission(assemblePermission("items.have", stack))) {
                Bukkit.getLogger().log(Level.INFO, "Removed {0} from {1}", new Object[]{stack.getType().name().toLowerCase(), player});
                inventory.remove(stack);
            }
        }
    }
}
