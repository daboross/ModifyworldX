/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.tehkode.modifyworld.bukkit;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.modifyworld.EntityCategory;

/**
 *
 * @author daboross
 */
public class ModifyworldPermissionRegister {

    public static void registerAllPermissions() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        registerBlocks(pm);
        registerBucket(pm);
        Map<String, Boolean> firstLevelNodes = getFirstLevelNodes();
        Permission modifyworldStar = new Permission("modifyworld.*", firstLevelNodes);
        pm.addPermission(modifyworldStar);
    }

    private static Map<String, Boolean> getFirstLevelNodes() {
        Map<String, Boolean> firstLevelNodes = new HashMap<String, Boolean>(15);
        firstLevelNodes.put("modifyworld.sneak", true);
        firstLevelNodes.put("modifyworld.sprint", true);
        firstLevelNodes.put("modifyworld.usebeds", true);
        firstLevelNodes.put("modifyworld.bucket.*", true);
        firstLevelNodes.put("modifyworld.digestion", true);
        firstLevelNodes.put("modifyworld.blocks.*", true);
        firstLevelNodes.put("modifyworld.tame.*", true);
        firstLevelNodes.put("modifyworld.vehicle.*", true);
        return firstLevelNodes;
    }

    private static void registerBucket(PluginManager pm) {
        Map<String, Boolean> emptyNodes = new HashMap<String, Boolean>(2);
        emptyNodes.put("modifyworld.bucket.empty.water", true);
        emptyNodes.put("modifyworld.bucket.empty.lava", true);
        Permission bucketEmptyStarNode = new Permission("modifyworld.bucket.empty.*", emptyNodes);
        pm.addPermission(bucketEmptyStarNode);
        Map<String, Boolean> fillNodes = new HashMap<String, Boolean>(2);
        fillNodes.put("modifyworld.bucket.fill.water", true);
        fillNodes.put("modifyworld.bucket.fill.lava", true);
        Permission bucketFillStarNode = new Permission("modifyworld.bucket.fill.*", fillNodes);
        pm.addPermission(bucketFillStarNode);
        Map<String, Boolean> bucketNodes = new HashMap<String, Boolean>(2);
        bucketNodes.put("modifyworld.bucket.empty.*", true);
        bucketNodes.put("modifyworld.bucket.fill.*", true);
        Permission bucketStarNode = new Permission("modifyworld.bucket.*", bucketNodes);
        pm.addPermission(bucketStarNode);
    }

    private static void registerBlocks(PluginManager pm) {
        Material[] materialValues = Material.values();
        Map<String, Boolean> blocksDestroy = new HashMap<String, Boolean>(materialValues.length);
        Map<String, Boolean> blocksPlace = new HashMap<String, Boolean>(materialValues.length);
        Map<String, Boolean> blocksInteract = new HashMap<String, Boolean>(materialValues.length);
        for (Material m : materialValues) {
            String materialPermission = getMaterialPermission(m);
            blocksPlace.put("modifyworld.blocks.place." + materialPermission, true);
            blocksDestroy.put("modifyworld.blocks.destroy." + materialPermission, true);
            blocksInteract.put("modifyworld.blocks.interact." + materialPermission, true);
        }
        Permission blocksDestroyermission = new Permission("modifyworld.blocks.deskroy.*", blocksDestroy);
        pm.addPermission(blocksDestroyermission);
        Permission blocksPlacePermission = new Permission("modifyworld.blocks.place.*", blocksPlace);
        pm.addPermission(blocksPlacePermission);
        Permission blocksInteractPermission = new Permission("modifyworld.blocks.interact.*", blocksPlace);
        pm.addPermission(blocksInteractPermission);
        Permission blocksStarPermission = new Permission("modifyworld.blocks.*");
        pm.addPermission(blocksStarPermission);
        blocksPlacePermission.addParent(blocksStarPermission, true);
        blocksDestroyermission.addParent(blocksStarPermission, true);
        blocksInteractPermission.addParent(blocksStarPermission, true);
    }

    private static String getInventoryTypePermission(InventoryType type) {
        return formatEnumString(type.name());
    }

    private static String getMaterialPermission(Material type) {
        return Integer.toString(type.getId());
    }

    private static String getMaterialPermission(Material type, byte metadata) {
        return getMaterialPermission(type) + (metadata > 0 ? ":" + metadata : "");
    }

    private static String getBlockPermission(Block block) {
        return getMaterialPermission(block.getType(), block.getData());
    }

    public static String getItemPermission(ItemStack item) {
        return getMaterialPermission(item.getType(), item.getData().getData());
    }

    private static String formatEnumString(String enumName) {
        return enumName.toLowerCase().replace("_", "");
    }

    private static String getEntityName(Entity entity) {
        if (entity instanceof ComplexEntityPart) {
            return getEntityName(((ComplexEntityPart) entity).getParent());
        }
        String entityName = formatEnumString(entity.getType().toString());
        if (entity instanceof Item) {
            entityName = getItemPermission(((Item) entity).getItemStack());
        }
        if (entity instanceof Player) {
            return "player." + ((Player) entity).getName();
        } else if (entity instanceof Tameable) {
            Tameable animal = (Tameable) entity;
            return "animal." + entityName + (animal.isTamed() ? "." + animal.getOwner().getName() : "");
        }
        EntityCategory category = EntityCategory.fromEntity(entity);
        if (category == null) {
            return entityName; // category unknown (ender crystal)
        }
        return category.getNameDot() + entityName;
    }

    public static String getObjectPermission(Object obj) {
        if (obj instanceof Entity) {
            return (getEntityName((Entity) obj));
        } else if (obj instanceof EntityType) {
            return formatEnumString(((EntityType) obj).name());
        } else if (obj instanceof BlockState) {
            return (getBlockPermission(((BlockState) obj).getBlock()));
        } else if (obj instanceof ItemStack) {
            return (getItemPermission((ItemStack) obj));
        } else if (obj instanceof Material) {
            return (getMaterialPermission((Material) obj));
        } else if (obj instanceof Block) {
            return (getBlockPermission((Block) obj));
        } else if (obj instanceof InventoryType) {
            return getInventoryTypePermission((InventoryType) obj);
        }

        return (obj.toString());
    }
}
