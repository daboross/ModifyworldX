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
		registerMaterials(pm);
		registerBucket(pm);
		registerItems(pm);
		Map<String, Boolean> firstLevelNodes = getFirstLevelNodes();
		Permission modifyworldStar = new Permission("modifyworld.*", firstLevelNodes);
		pm.addPermission(modifyworldStar);
	}

	private static Map<String, Boolean> getFirstLevelNodes() {
		Map<String, Boolean> firstLevelNodes = new HashMap<String, Boolean>(15);
		firstLevelNodes.put("modifyworld.usebeds", true);
		firstLevelNodes.put("modifyworld.bucket.*", true);
		firstLevelNodes.put("modifyworld.digestion", true);
		firstLevelNodes.put("modifyworld.blocks.*", true);
		firstLevelNodes.put("modifyworld.tame.*", true);
		firstLevelNodes.put("modifyworld.vehicle.*", true);
		firstLevelNodes.put("modifyworld.items.*", true);
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

	private static void registerItems(PluginManager pm) {
		Map<String, Boolean> craftNodes = new HashMap<String, Boolean>();
		Map<String, Boolean> dropNodes = new HashMap<String, Boolean>();
		Map<String, Boolean> enchantNodes = new HashMap<String, Boolean>();
		Map<String, Boolean> haveNodes = new HashMap<String, Boolean>();
		Map<String, Boolean> pickupNodes = new HashMap<String, Boolean>();
		Map<String, Boolean> throwNodes = new HashMap<String, Boolean>();
		for (Material material : Material.values()) {
			craftNodes.put("modifyworld.items.craft." + getMaterialPermission(material), Boolean.TRUE);
			dropNodes.put("modifyworld.items.drop." + getMaterialPermission(material), Boolean.TRUE);
			enchantNodes.put("modifyworld.items.enchant." + getMaterialPermission(material), Boolean.TRUE);
			haveNodes.put("modifyworld.items.have." + getMaterialPermission(material), Boolean.TRUE);
			pickupNodes.put("modifyworld.items.pickup." + getMaterialPermission(material), Boolean.TRUE);
			throwNodes.put("modifyworld.items.throw." + getMaterialPermission(material), Boolean.TRUE);
			//no hold
		}
		Permission craftStar = new Permission("modifyworld.items..*", craftNodes);
		pm.addPermission(craftStar);
		Permission dropStar = new Permission("modifyworld.items..*", dropNodes);
		pm.addPermission(dropStar);
		Permission enchantStar = new Permission("modifyworld.items..*", enchantNodes);
		pm.addPermission(enchantStar);
		Permission haveStar = new Permission("modifyworld.items..*", haveNodes);
		pm.addPermission(haveStar);
		Permission pickupStar = new Permission("modifyworld.items..*", pickupNodes);
		pm.addPermission(pickupStar);
		Permission throwStar = new Permission("modifyworld.items..*", throwNodes);
		pm.addPermission(throwStar);
		Map<String, Boolean> itemsNodes = new HashMap<String, Boolean>();
		itemsNodes.put("modifyworld.items.craft.*", true);
		itemsNodes.put("modifyworld.items.drop.*", true);
		itemsNodes.put("modifyworld.items.enchant.*", true);
		itemsNodes.put("modifyworld.items.have.*", true);
		itemsNodes.put("modifyworld.items.hold.*", true);
		itemsNodes.put("modifyworld.items.pickup.*", true);
		itemsNodes.put("modifyworld.items.throw.*", true);
		itemsNodes.put("modifyworld.items.take.*", true);
		itemsNodes.put("modifyworld.items.put.*", true);
		Permission itemsStar = new Permission("modifyworld.items.*", itemsNodes);
		pm.addPermission(itemsStar);
	}

	private static void registerMaterials(PluginManager pm) {
		Material[] materialValues = Material.values();
		EntityType[] entityTypeValues = {EntityType.PAINTING, EntityType.ITEM_FRAME};
		Map<String, Boolean> blocksDestroy = new HashMap<String, Boolean>(materialValues.length + entityTypeValues.length);
		Map<String, Boolean> blocksPlace = new HashMap<String, Boolean>(materialValues.length + entityTypeValues.length);
		Map<String, Boolean> blocksInteract = new HashMap<String, Boolean>(materialValues.length + entityTypeValues.length);
		for (Material material : materialValues) {
			String materialPermission = getMaterialPermission(material);
			blocksPlace.put("modifyworld.blocks.place." + materialPermission, true);
			blocksDestroy.put("modifyworld.blocks.destroy." + materialPermission, true);
			blocksInteract.put("modifyworld.blocks.interact." + materialPermission, true);
		}
		for (EntityType entityType : entityTypeValues) {
			String materialPermission = formatEnumString(entityType.name());
			blocksPlace.put("modifyworld.blocks.place." + materialPermission, true);
			blocksDestroy.put("modifyworld.blocks.destroy." + materialPermission, true);
			blocksInteract.put("modifyworld.blocks.interact." + materialPermission, true);
		}
		Permission blocksDestroyermission = new Permission("modifyworld.blocks.destroy.*", blocksDestroy);
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

	private static String getBlockPermission(Block block) {
		return getMaterialPermission(block.getType());
	}

	private static String formatEnumString(String enumName) {
		return enumName.toLowerCase().replace('_', ' ');
	}

	public static String getPermission(Object obj) {
		if (obj instanceof ComplexEntityPart) {
			return getPermission((ComplexEntityPart) obj);
		} else if (obj instanceof Entity) {
			return (getPermission((Entity) obj));
		} else if (obj instanceof EntityType) {
			return getPermission((EntityType) obj);
		} else if (obj instanceof BlockState) {
			return (getPermission(((BlockState) obj).getBlock()));
		} else if (obj instanceof ItemStack) {
			return (getPermission((ItemStack) obj));
		} else if (obj instanceof Material) {
			return (getPermission((Material) obj));
		} else if (obj instanceof Block) {
			return (getPermission((Block) obj));
		} else if (obj instanceof InventoryType) {
			return getPermission((InventoryType) obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static String getPermission(Entity entity) {
		if (entity instanceof Player) {
			return "player";
		}
		String entityName;
		if (entity instanceof Item) {
			entityName = getPermission(((Item) entity).getItemStack().getType());
		} else {
			entityName = formatEnumString(entity.getType().toString());
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

	public static String getPermission(EntityType entityType) {
		return formatEnumString(entityType.name());
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
		return getMaterialPermission(material);
	}

	public static String getPermission(Block block) {
		return getBlockPermission(block);
	}

	public static String getPermission(InventoryType inventoryType) {
		return getInventoryTypePermission(inventoryType);
	}
}
