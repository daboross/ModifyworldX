package ru.tehkode.modifyworld;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;

public class PlayerInformer {

    public final static String PERMISSION_DENIED = "Sorry, you don't have enough permissions";
    public final static String PROHIBITED_ITEM = "Prohibited item \"%s\" has been removed from your inventory.";
    public final static String DEFAULT_MESSAGE_FORMAT = "&f[&2Modifyworld&f]&4 %s";
    // Default message format
    protected String messageFormat = DEFAULT_MESSAGE_FORMAT;
    protected Map<String, String> messages = new HashMap<String, String>();
    // Flags
    protected boolean enabled = false;
    protected boolean individualMessages = false;
    protected String defaultMessage = PERMISSION_DENIED;

    public PlayerInformer(ConfigurationSection config) {
        this.enabled = config.getBoolean("inform-players", enabled);

        this.loadConfig(config.getConfigurationSection("messages"));
    }

    private void loadConfig(ConfigurationSection config) {

        this.defaultMessage = config.getString("default-message", this.defaultMessage);

        this.messageFormat = config.getString("message-format", this.messageFormat);

        this.individualMessages = config.getBoolean("individual-messages", this.individualMessages);

        for (String permission : config.getKeys(true)) {
            if (!config.isString(permission)) {
                continue;
            }

            setMessage(permission, config.getString(permission.replace("/", ".")));
        }
    }

    public void setMessage(String permission, String message) {
        messages.put(permission, message);
    }

    public String getMessage(String permission) {
        if (messages.containsKey(permission)) {
            return messages.get(permission);
        }
        String currentPermission = permission;
        int index;
        while ((index = currentPermission.lastIndexOf(".")) != -1) {
            currentPermission = currentPermission.substring(0, index);
            if (messages.containsKey(currentPermission)) {
                String message = messages.get(currentPermission);
                messages.put(permission, message);
                return message;
            }
        }
        return this.defaultMessage;
    }

    public void informPlayer(Player player, String permission, Object obj) {
        if (!enabled) {
            return;
        }
        String message = getMessage(permission).replace("$permission", permission).replace("$1", describeObject(obj));
        if (message != null && !message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(messageFormat, message)));
        }
    }

    public void informPlayer(Player player, String permission) {
        if (!enabled) {
            return;
        }
        String message = getMessage(permission).replace("$permission", permission);
        if (message != null && !message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(messageFormat, message)));
        }
    }

    protected String describeObject(Object obj) {
        if (obj instanceof ComplexEntityPart) { // Complex entities
            return getDescription((ComplexEntityPart) obj);
        } else if (obj instanceof Item) { // Dropped items
            return getDescription((Item) obj);
        } else if (obj instanceof ItemStack) { // Items
            return getDescription((ItemStack) obj);
        } else if (obj instanceof Entity) { // Entities
            return getDescription((Entity) obj);
        } else if (obj instanceof Block) { // Blocks
            return getDescription((Block) obj);
        } else if (obj instanceof Material) { // Just material
            return getDescription((Material) obj);
        } else {
            return String.valueOf(obj);
        }
    }

    private String getDescription(Block block) {
        return getDescription(block.getType());
    }

    private String getDescription(Item item) {
        return getDescription(item.getItemStack());
    }

    private String getDescription(ItemStack itemStack) {
        return getDescription(itemStack.getType());
    }

    private String getDescription(ComplexEntityPart complexEntityPart) {
        return getDescription(complexEntityPart.getParent());
    }

    private String getDescription(Entity entity) {
        return entity.getType().toString().toLowerCase().replace('_', ' ');
    }

    private String getDescription(Material material) {
        return material.name().toLowerCase().replace('_', ' ');
    }
}
