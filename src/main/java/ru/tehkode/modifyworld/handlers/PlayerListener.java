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
package ru.tehkode.modifyworld.handlers;

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
import ru.tehkode.modifyworld.ModifyworldListener;
import ru.tehkode.modifyworld.PlayerInformer;

/**
 * @author t3hk0d3
 */
public class PlayerListener extends ModifyworldListener {

    protected boolean checkInventory = false;
    protected boolean dropRestrictedItem = false;

    public PlayerListener(Plugin plugin, ConfigurationSection config, PlayerInformer informer) {
        super(plugin, config, informer);

        this.checkInventory = config.getBoolean("item-restrictions", this.checkInventory);
        this.dropRestrictedItem = config.getBoolean("drop-restricted-item", this.dropRestrictedItem);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (isPermissionDeniedMessage(event.getPlayer(), "modifyworld.usebeds")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        String bucketName = event.getBucket().toString().toLowerCase().replace("_bucket", ""); // WATER_BUCKET -> water
        if (isPermissionDeniedMessage(event.getPlayer(), "modifyworld.bucket.empty", bucketName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Material result = event.getItemStack().getType();
        String materialName;
        if (result == Material.WATER_BUCKET) {
            materialName = "water";
        } else if (result == Material.LAVA_BUCKET) {
            materialName = "lava";
        } else if (result == Material.MILK_BUCKET) {
            materialName = "milk";
        } else {
            materialName = result.name().toLowerCase();
        }
        if (isPermissionDeniedMessage(event.getPlayer(), "modifyworld.bucket.fill", materialName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        // No inform to avoid spam
        if (isPermissionDenied(event.getPlayer(), "modifyworld.items.pickup", event.getItem().getItemStack())) {
            event.setCancelled(true);
        }

        this.checkPlayerInventory(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isPermissionDeniedMessage(event.getPlayer(), "modifyworld.items.drop", event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }

        this.checkPlayerInventory(event.getPlayer());
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
        if (isPermissionDeniedMessage(player, "modifyworld.items." + action, item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInventoryEvent(InventoryClickEvent event) {
        ItemStack item = event.getCursor();

        if (item == null || item.getType() == Material.AIR || event.getSlotType() != InventoryType.SlotType.QUICKBAR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        int targetSlot = player.getInventory().getHeldItemSlot();

        if (event.getSlot() == targetSlot && isPermissionDeniedMessage(player, "modifyworld.items.hold", item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.isCancelled() && isPermissionDeniedMessage(event.getPlayer(), "modifyworld.interact", event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) { // item restriction check
            this.checkPlayerInventory(event.getPlayer());
        }
        Player player = event.getPlayer();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) { //RIGHT_CLICK_AIR is cancelled by default.
            switch (player.getItemInHand().getType()) {
                case POTION: //Only check splash potions.
                    if ((player.getItemInHand().getDurability() & 0x4000) != 0x4000) {
                        break;
                    }
                case EGG:
                case SNOW_BALL:
                case EXP_BOTTLE:
                    if (isPermissionDeniedMessage(player, "modifyworld.items.throw", player.getItemInHand())) {
                        event.setUseItemInHand(Result.DENY);
                        //Denying a potion works fine, but the client needs to be updated because it already reduced the item.
                        if (player.getItemInHand().getType() == Material.POTION) {
                            event.getPlayer().updateInventory();
                        }
                    }
                    return; // no need to check further
                case MONSTER_EGG: // don't add MONSTER_EGGS here
                    if (isPermissionDeniedMessage(player, "modifyworld.spawn", ((SpawnEgg) player.getItemInHand().getData()).getSpawnedType())) {
                        event.setUseItemInHand(Result.DENY);
                    }
                    return; // no need to check further
            }
        }
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL) {
            if (!event.isCancelled() && isPermissionDeniedMessage(player, "modifyworld.blocks.interact", event.getClickedBlock())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemEnchant(EnchantItemEvent event) {
        if (isPermissionDeniedMessage(event.getEnchanter(), "modifyworld.items.enchant", event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (isPermissionDeniedMessage(player, "modifyworld.items.craft", event.getRecipe().getResult())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player)
                && isPermissionDenied((Player) event.getEntity(), "modifyworld.digestion")) {
            event.setCancelled(true);
        }
    }

    protected void checkPlayerInventory(Player player) {
        if (!checkInventory) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && isPermissionDeniedMessage(player, "modifyworld.items.have", stack)) {
                inventory.remove(stack);
                if (this.dropRestrictedItem) {
                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
        }
    }
}
