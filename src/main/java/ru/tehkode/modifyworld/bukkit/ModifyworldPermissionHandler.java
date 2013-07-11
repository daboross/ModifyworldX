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

    public void registerAllPermissions() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        registerBlocks(pm);
        registerChat(pm);
        Map<String, Boolean> firstLevelNodes = getFirstLevelNodes();
        Permission modifyworldStar = new Permission("modifyworld.*", firstLevelNodes);
        pm.addPermission(modifyworldStar);
    }

    private Map<String, Boolean> getFirstLevelNodes() {
        Map<String, Boolean> firstLevelNodes = new HashMap<String, Boolean>(15);
        firstLevelNodes.put("modifyworld.login", true);
        firstLevelNodes.put("modifyworld.chat", true);
        firstLevelNodes.put("modifyworld.sneak", true);
        firstLevelNodes.put("modifyworld.sprint", true);
        firstLevelNodes.put("modifyworld.chat.*", true);
        firstLevelNodes.put("modifyworld.usebeds", true);
        firstLevelNodes.put("modifyworld.bucket.*", true);
        firstLevelNodes.put("modifyworld.digestion", true);
        firstLevelNodes.put("modifyworld.items.*", true);
        firstLevelNodes.put("modifyworld.damage.*", true);
        firstLevelNodes.put("modifyworld.mobtarget.*", true);
        firstLevelNodes.put("modifyworld.blocks.*", true);
        firstLevelNodes.put("modifyworld.interact.*", true);
        firstLevelNodes.put("modifyworld.tame.*", true);
        firstLevelNodes.put("modifyworld.vehicle.*", true);
        return firstLevelNodes;
    }

    private void registerChat(PluginManager pm) {
        Map<String, Boolean> chatNodes = new HashMap<String, Boolean>(1);
        chatNodes.put("modifyworld.chat.private", true);
        Permission chatNodeStar = new Permission("modifyworld.chat.*", chatNodes);
        pm.addPermission(chatNodeStar);
    }

    private void registerBlocks(PluginManager pm) {
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
        Permission blocksDestroyermission = new Permission("modifyworld.blocks.desktroy.*", blocksDestroy);
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
