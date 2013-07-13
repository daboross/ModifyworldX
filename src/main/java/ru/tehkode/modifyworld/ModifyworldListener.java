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
package ru.tehkode.modifyworld;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import ru.tehkode.modifyworld.bukkit.ModifyworldPermissionRegister;

/**
 *
 * @author t3hk0d3
 */
public abstract class ModifyworldListener implements Listener {

    protected PlayerInformer informer;

    public ModifyworldListener(Plugin plugin, ConfigurationSection config, PlayerInformer informer) {
        this.informer = informer;
    }

    protected boolean isPermissionDeniedMessage(Player player, String permission) {
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission);
        }
        return isDenied;
    }

    protected boolean isPermissionDeniedMessage(Player player, String basePermission, Object obj) {
        String permission = assemblePermission(basePermission, obj);
        boolean isDenied = !player.hasPermission(permission);
        if (isDenied) {
            this.informer.informPlayer(player, permission, obj);
        }
        return isDenied;
    }

    protected boolean isPermissionDenied(Player player, String permission) {
        return !player.hasPermission(permission);
    }

    protected boolean isPermissionDenied(Player player, String permission, Object obj) {
        return !player.hasPermission(assemblePermission(permission, obj));
    }

    protected String assemblePermission(String permission, Object obj) {
        if (obj != null) {
            return permission + "." + ModifyworldPermissionRegister.getPermission(obj);
        } else {
            return permission;
        }
    }

    private void registerEvents(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
