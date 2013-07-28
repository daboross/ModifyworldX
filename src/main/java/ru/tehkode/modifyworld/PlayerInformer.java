package ru.tehkode.modifyworld;

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
import org.bukkit.block.BlockState;

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

    public void informPlayer(Player player, String permission) {
        if (!enabled) {
            return;
        }
        String message = getMessage(permission).replace("$permission", permission);
        if (message != null && !message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(messageFormat, message)));
        }
    }

    public void informPlayer(Player player, String permission, Block block) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(block));
    }

    public void informPlayer(Player player, String permission, BlockState blockState) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(blockState));
    }

    public void informPlayer(Player player, String permission, Item item) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(item));
    }

    public void informPlayer(Player player, String permission, ItemStack itemStack) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(itemStack));
    }

    public void informPlayer(Player player, String permission, ComplexEntityPart complexEntityPart) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(complexEntityPart));
    }

    public void informPlayer(Player player, String permission, Entity entity) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(entity));
    }

    public void informPlayer(Player player, String permission, Enum enumeration) {
        if (!enabled) {
            return;
        }
        informPlayer(player, permission, getDescription(enumeration));
    }

    public void informPlayer(Player player, String permission, String string) {
        if (!enabled) {
            return;
        }
        String message = getMessage(permission).replace("$permission", permission).replace("$1", string);
        if (!message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(messageFormat, message)));
        }
    }

    private String getDescription(Block block) {
        return getDescription(block.getType());
    }

    private String getDescription(BlockState block) {
        return getDescription(block.getBlock().getType());
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

    private String getDescription(Enum enumuration) {
        return enumuration.name().toLowerCase().replace('_', ' ');
    }
}
