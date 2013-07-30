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
package net.daboross.modifyworldx.bukkit;

import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author daboross
 */
public class ModifyworldPermissionRegister {

    private static final String[] MATERIAL_PERMISSION_BASES = new String[]{
        "modifyworld.blocks.place",
        "modifyworld.blocks.destroy",
        "modifyworld.blocks.interact",
        "modifyworld.items.craft",
        "modifyworld.items.enchant",
        "modifyworld.items.pickup",
        "modifyworld.items.have",
        "modifyworld.items.throw",
        "modifyworld.items.drop",
        "modifyworld.items.put",
        "modifyworld.items.take"
    };
    private static final String[] ENTITY_TYPE_PERMISSION_BASES = new String[]{
        "modifyworld.damage.deal",
        "modifyworld.damage.take",
        "modifyworld.mobtarget",
        "modifyworld.interact",
        "modifyworld.tame" // TODO find a way to get only tamable entites for this.
    };
    private static final String[] VEHICLE_PERMISSION_BASES = new String[]{
        "modifyworld.vehicle.enter",
        "modifyworld.vehicle.destroy",
        "modifyworld.vehicle.collide"
    };
    private static final String[] HANGING_PERMISSION_BASES = new String[]{
        "modifyworld.blocks.place",
        "modifyworld.blocks.destroy",
        "modifyworld.blocks.interact"
    };
    private static final String[] MODIFYWORLD_STAR_CHILDREN = new String[]{
        "modifyworld.usebeds",
        "modifyworld.bucket.*",
        "modifyworld.digestion",
        "modifyworld.blocks.*",
        "modifyworld.tame.*",
        "modifyworld.vehicle.*",
        "modifyworld.items.*",
        "modifyworld.mobtarget.*",
        "modifyworld.tame.*",
        "modifyworld.damage.*"
    };
    private static final String[] ITEMS_STAR_CHILDREN = new String[]{
        "modifyworld.items.craft.*",
        "modifyworld.items.drop.*",
        "modifyworld.items.enchant.*",
        "modifyworld.items.have.*",
        "modifyworld.items.pickup.*",
        "modifyworld.items.throw.*",
        "modifyworld.items.take.*",
        "modifyworld.items.put.*"
    };
    private static final String[] DAMAGE_STAR_CHILDREN = new String[]{
        "modifyworld.damage.deal.*",
        "modifyworld.damage.take.*"
    };
    private static final String[] BLOCKS_STAR_CHILDREN = new String[]{
        "modifyworld.blocks.interact.*",
        "modifyworld.blocks.place.*",
        "modifyworld.blocks.destroy.*"
    };
    private static final EntityType[] VEHICLES = new EntityType[]{
        EntityType.MINECART,
        EntityType.MINECART_CHEST,
        EntityType.MINECART_FURNACE,
        EntityType.MINECART_HOPPER,
        EntityType.MINECART_MOB_SPAWNER,
        EntityType.MINECART_TNT,
        EntityType.BOAT
    };
    private static final EntityType[] HANGING_ENTITIES = new EntityType[]{
        EntityType.PAINTING,
        EntityType.ITEM_FRAME
    };

    public static void registerAllPermissions(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();
        try {
            registerBucket(pm);
            registerItemsStar(pm);
            registerDamageStar(pm);
            registerMaterial(pm);
            registerHangingEntities(pm);
            registerBlockStar(pm);
            registerEntityTypes(pm);
            registerVehicle(pm);
            registerModifyworldStar(pm);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to register permissions with error", ex);
        }
    }

    private static void registerModifyworldStar(PluginManager pm) {
        registerPermission(pm, "modifyworld.*", MODIFYWORLD_STAR_CHILDREN);
    }

    private static void registerItemsStar(PluginManager pm) {
        registerPermission(pm, "modifyworld.items.*", ITEMS_STAR_CHILDREN);
    }

    private static void registerDamageStar(PluginManager pm) {
        registerPermission(pm, "modifyworld.damage.*", DAMAGE_STAR_CHILDREN);
    }

    private static void registerBlockStar(PluginManager pm) {
        registerPermission(pm, "modifyworld.blocks.*", BLOCKS_STAR_CHILDREN);
    }

    private static void registerBucket(PluginManager pm) {
        registerPermission(pm, "modifyworld.bucket.empty.*",
                "modifyworld.bucket.empty.water",
                "modifyworld.bucket.empty.lava",
                "modifyworld.bucket.empty.milk");
        registerPermission(pm, "modifyworld.bucket.fill.*",
                "modifyworld.bucket.fill.water",
                "modifyworld.bucket.fill.lava",
                "modifyworld.bucket.fill.milk");
        registerPermission(pm, "modifyworld.bucket.*",
                "modifyworld.bucket.empty.*",
                "modifyworld.bucket.fill.*");
    }

    private static void registerVehicle(PluginManager pm) {
        registerPermission(pm, "modifyworld.vehicle.*",
                "modifyworld.vehicle.enter.*",
                "modifyworld.vehicle.destroy.*",
                "modifyworld.vehicle.collide.*");
        for (String permissionString : VEHICLE_PERMISSION_BASES) {
            Permission permission = getPermission(pm, permissionString + ".*");
            for (EntityType vehicle : VEHICLES) {
                permission.getChildren().put(permissionString + "." + getPermission(vehicle), Boolean.TRUE);
            }
            recalculatePermission(pm, permission);
        }
    }

    private static void registerMaterial(PluginManager pm) {
        Material[] materialValues = Material.values();
        Permission[] permissions = new Permission[MATERIAL_PERMISSION_BASES.length];
        for (int i = 0; i < permissions.length; i++) {
            permissions[i] = getPermission(pm, MATERIAL_PERMISSION_BASES[i] + ".*");
        }
        for (Material material : materialValues) {
            String materialPermission = getPermission(material);
            for (int i = 0; i < permissions.length; i++) {
                permissions[i].getChildren().put(MATERIAL_PERMISSION_BASES[i] + "." + materialPermission, Boolean.TRUE);
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            recalculatePermission(pm, permissions[i]);
        }
    }

    private static void registerEntityTypes(PluginManager pm) {
        EntityType[] values = EntityType.values();
        Permission[] permissions = new Permission[ENTITY_TYPE_PERMISSION_BASES.length];
        for (int i = 0; i < permissions.length; i++) {
            permissions[i] = getPermission(pm, ENTITY_TYPE_PERMISSION_BASES[i] + ".*");
        }
        for (EntityType entityType : values) {
            String permission = getPermission(entityType);
            for (int i = 0; i < permissions.length; i++) {
                permissions[i].getChildren().put(ENTITY_TYPE_PERMISSION_BASES[i] + "." + permission, Boolean.TRUE);
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            recalculatePermission(pm, permissions[i]);
        }
    }

    private static void registerHangingEntities(PluginManager pm) {
        Permission[] permissions = new Permission[HANGING_PERMISSION_BASES.length];
        for (int i = 0; i < permissions.length; i++) {
            permissions[i] = getPermission(pm, HANGING_PERMISSION_BASES[i] + ".*");
        }
        for (EntityType entityType : HANGING_ENTITIES) {
            String permission = getPermission(entityType);
            for (int i = 0; i < permissions.length; i++) {
                permissions[i].getChildren().put(HANGING_PERMISSION_BASES[i] + "." + permission, Boolean.TRUE);
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            recalculatePermission(pm, permissions[i]);
        }
    }

    private static void registerPermission(PluginManager pm, String name, String... children) {
        Permission permission = getPermission(pm, name);
        Map<String, Boolean> childrenMap = permission.getChildren();
        for (String child : children) {
            childrenMap.put(child, Boolean.TRUE);
        }
        recalculatePermission(pm, permission);
    }

    private static Permission getPermission(PluginManager pm, String name) {
        Permission permission = pm.getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
        }
        return permission;
    }

    private static void recalculatePermission(PluginManager pm, Permission permission) {
        Permission old = pm.getPermission(permission.getName());
        if (old == null) {
            pm.addPermission(permission);
            permission.recalculatePermissibles();
        } else if (old == permission) {
            permission.recalculatePermissibles();
        } else {
            throw new IllegalArgumentException("Permission already registered " + permission.getName());
        }
    }

    public static String getPermission(Entity entity) {
        if (entity instanceof ComplexEntityPart) {
            return getPermission((ComplexEntityPart) entity);
        }
        if (entity instanceof Item) {
            return getPermission(((Item) entity).getItemStack());
//		} else if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
//			return getPermission(entity.getType()) + "." + ((Tameable) entity).getOwner().getName();
//		} else if (entity instanceof Player) {
//			return "player." + ((Player) entity).getName();
        } else {
            return getPermission(entity.getType());
        }

    }

    public static String getPermission(ComplexEntityPart complexEntityPart) {
        return getPermission(complexEntityPart.getParent());
    }

    public static String getPermission(BlockState blockState) {
        return getPermission(blockState.getBlock());
    }

    public static String getPermission(ItemStack itemStack) {
        return getPermission(itemStack.getType());
    }

    public static String getPermission(Material material) {
        return Integer.toString(material.getId());
    }

    public static String getPermission(Block block) {
        return getPermission(block.getType());
    }

    public static String getPermission(Enum enumeration) {
        return enumeration.name().toLowerCase().replaceAll("_", "");
    }
}
