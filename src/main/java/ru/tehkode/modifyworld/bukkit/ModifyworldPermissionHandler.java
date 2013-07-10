/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package ru.tehkode.modifyworld.bukkit;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
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
public class ModifyworldPermissionHandler {

    private final boolean useMaterialNames;

    public ModifyworldPermissionHandler(boolean useMaterialNames) {
        this.useMaterialNames = useMaterialNames;
    }

    public void register() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        Permission modifyworldStar = new Permission("modifyworld.*");
        registerBlocks(pm).addParent(modifyworldStar, true);
    }

    private Permission registerBlocks(PluginManager pm) {
        Map<String, Boolean> blocksPlace = new HashMap<String, Boolean>();
        Map<String, Boolean> blocksDestroy = new HashMap<String, Boolean>();
        Map<String, Boolean> blocksInteract = new HashMap<String, Boolean>();
        for (Material m : Material.values()) {
            blocksPlace.put("modifyworld.blocks.place." + getMaterialPermission(m), true);
            blocksDestroy.put("modifyworld.blocks.destroy." + getMaterialPermission(m), true);
            blocksInteract.put("modifyworld.blocks.interact." + getMaterialPermission(m), true);
        }
        Permission blocksPlacePermission = new Permission("modifyworld.blocks.place.*", blocksPlace);
        Permission blocksRemovePermission = new Permission("modifyworld.blocks.remove.*", blocksPlace);
        Permission blocksInteractPermission = new Permission("modifyworld.blocks.interact.*", blocksPlace);
        Permission blocksStarPermission = new Permission("modifyworld.blocks.*");
        blocksPlacePermission.addParent(blocksStarPermission, true);
        blocksRemovePermission.addParent(blocksStarPermission, true);
        blocksInteractPermission.addParent(blocksStarPermission, true);
        return blocksStarPermission;
    }

    private String getInventoryTypePermission(InventoryType type) {
        return formatEnumString(type.name());
    }

    private String getMaterialPermission(Material type) {
        return this.useMaterialNames ? formatEnumString(type.name()) : Integer.toString(type.getId());
    }

    private String getMaterialPermission(Material type, byte metadata) {
        return getMaterialPermission(type) + (metadata > 0 ? ":" + metadata : "");
    }

    private String getBlockPermission(Block block) {
        return getMaterialPermission(block.getType(), block.getData());
    }

    public String getItemPermission(ItemStack item) {
        return getMaterialPermission(item.getType(), item.getData().getData());
    }

    private String formatEnumString(String enumName) {
        return enumName.toLowerCase().replace("_", "");
    }

    private String getEntityName(Entity entity) {
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
}
