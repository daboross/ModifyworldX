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
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.modifyworld.EntityCategory;

/**
 *
 * @author daboross
 */
public class ModifyworldPermissionRegister {

	private static final String[] materialPermissions = new String[]{
		"modifyworld.blocks.place.",
		"modifyworld.blocks.destroy.",
		"modifyworld.blocks.interact.",
		"modifyworld.items.craft.",
		"modifyworld.items.enchant.",
		"modifyworld.items.pickup.",
		"modifyworld.items.have.",
		"modifyworld.items.throw."
	};
	private static final String[] firstLevelPermissions = new String[]{
		"modifyworld.usebeds",
		"modifyworld.bucket.*",
		"modifyworld.digestion",
		"modifyworld.blocks.*",
		"modifyworld.tame.*",
		"modifyworld.vehicle.*",
		"modifyworld.items.*"
	};

	public static void registerAllPermissions() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		registerBlocks(pm);
		registerBucketStar(pm);
		registerItemsStar(pm);
		registerBlocksHanging(pm);
		registerAllMaterials(pm);
		registerModifyworldStar(pm);
	}

	private static void registerModifyworldStar(PluginManager pm) {
		registerPermission(pm, "modifyworld.*", firstLevelPermissions);
	}

	private static void registerBucketStar(PluginManager pm) {
		registerPermission(pm, "modifyworld.bucket.empty.*",
				"modifyworld.bucket.empty.water",
				"modifyworld.bucket.empty.lava");
		registerPermission(pm, "modifyworld.bucket.fill.*",
				"modifyworld.bucket.fill.water",
				"modifyworld.bucket.fill.lava");
		registerPermission(pm, "modifyworld.bucket.*",
				"modifyworld.bucket.empty.*",
				"modifyworld.bucket.fill.*");
	}

	private static void registerItemsStar(PluginManager pm) {
		registerPermission(pm, "modifyworld.items.*",
				"modifyworld.items.craft.*",
				"modifyworld.items.drop.*",
				"modifyworld.items.enchant.*",
				"modifyworld.items.have.*",
				"modifyworld.items.pickup.*",
				"modifyworld.items.throw.*",
				"modifyworld.items.take.*",
				"modifyworld.items.put.*");
	}

	private static void registerAllMaterials(PluginManager pm) {
		Material[] materialValues = Material.values();
		Permission[] permissions = new Permission[materialPermissions.length];
		for (int i = 0; i < permissions.length; i++) {
			permissions[i] = getPermission(pm, materialPermissions[i]);
		}
		for (Material material : materialValues) {
			String materialPermission = getPermission(material);
			for (int i = 0; i < permissions.length; i++) {
				permissions[i].getChildren().put(materialPermissions[i] + materialPermission, Boolean.TRUE);
			}
		}
		for (int i = 0; i < permissions.length; i++) {
			permissions[i].recalculatePermissibles();
			pm.addPermission(permissions[i]);
		}
	}

	private static void registerBlocksHanging(PluginManager pm) {
		EntityType[] hangingEntities = {EntityType.PAINTING, EntityType.ITEM_FRAME};
		Permission blocksPlacePermission = getPermission(pm, "modifyworld.blocks.place.*");
		Permission blocksDestroyPermission = getPermission(pm, "modifyworld.blocks.destroy.*");
		Permission blocksInteractPermission = getPermission(pm, "modifyworld.blocks.interact.*");
		for (EntityType entityType : hangingEntities) {
			String materialPermission = getPermission(entityType);
			blocksPlacePermission.getChildren().put("modifyworld.blocks.place." + materialPermission, Boolean.TRUE);
			blocksDestroyPermission.getChildren().put("modifyworld.blocks.destroy." + materialPermission, Boolean.TRUE);
			blocksInteractPermission.getChildren().put("modifyworld.blocks.interact." + materialPermission, Boolean.TRUE);
		}
		recalculatePermission(pm, blocksDestroyPermission);
		recalculatePermission(pm, blocksPlacePermission);
		recalculatePermission(pm, blocksInteractPermission);
	}

	private static void registerBlocks(PluginManager pm) {
		EntityType[] extraEntityTypes = {EntityType.PAINTING, EntityType.ITEM_FRAME};
		Permission blocksDestroyPermission = getPermission(pm, "modifyworld.blocks.destroy.*");
		Permission blocksPlacePermission = getPermission(pm, "modifyworld.blocks.place.*");
		Permission blocksInteractPermission = getPermission(pm, "modifyworld.blocks.interact.*");
		for (EntityType entityType : extraEntityTypes) {
			String materialPermission = getPermission(entityType);
			blocksPlacePermission.getChildren().put("modifyworld.blocks.place." + materialPermission, Boolean.TRUE);
			blocksDestroyPermission.getChildren().put("modifyworld.blocks.destroy." + materialPermission, Boolean.TRUE);
			blocksInteractPermission.getChildren().put("modifyworld.blocks.interact." + materialPermission, Boolean.TRUE);
		}
		registerPermission(pm, "modifyworld.blocks.*",
				"modifyworld.blocks.interact.*",
				"modifyworld.blocks.place.*",
				"modifyworld.blocks.destroy.*");

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
		permission.recalculatePermissibles();
		pm.addPermission(permission);
	}

	public static String getPermission(Entity entity) {
		if (entity instanceof ComplexEntityPart) {
			return getPermission((ComplexEntityPart) entity);
		}
		if (entity instanceof Player) {
			return "player";
		}
		String entityName;
		if (entity instanceof Item) {
			entityName = getPermission(((Item) entity).getItemStack().getType());
		} else {
			entityName = getPermission(entity.getType());
		}
		if (entity instanceof Tameable) {
			Tameable animal = (Tameable) entity;
			return "animal." + entityName + (animal.isTamed() ? "." + animal.getOwner().getName() : "");
		}
		EntityCategory category = EntityCategory.fromEntity(entity);
		if (category == null) {
			return entityName; // category unknown (ender crystal)
		}
		return category.getName() + "." + entityName;
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
