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
package net.daboross.modifyworld;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import net.daboross.modifyworld.bukkit.ModifyworldPermissionRegister;

/**
 *
 * @author t3hk0d3
 */
public abstract class ModifyworldListener implements Listener {

    protected final PlayerInformer informer;
    protected final Plugin plugin;

    public ModifyworldListener(Plugin plugin, ConfigurationSection config, PlayerInformer informer) {
        this.plugin = plugin;
        this.informer = informer;
    }

    protected boolean isPermissionDeniedMessage(Player player, String permission) {
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission) {
        return !player.hasPermission(permission);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, Entity obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, Entity obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, Entity obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, BlockState obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, BlockState obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, BlockState obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, Material obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, Material obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, Material obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, Block obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, Block obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, Block obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, ItemStack obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, ItemStack obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, ItemStack obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, Enum obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, Enum obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, Enum obj) {
        return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, String obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission, String obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, String obj) {
        return permission + "." + obj.toLowerCase();
    }
}
