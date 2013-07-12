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
    protected ConfigurationSection config;
    protected boolean informPlayers = false;
    protected boolean checkMetadata = false;
    protected boolean checkItemUse = false;
    protected boolean enableWhitelist = false;

    public ModifyworldListener(Plugin plugin, ConfigurationSection config, PlayerInformer informer) {
        this.informer = informer;
        this.config = config;

        this.registerEvents(plugin);

        this.informPlayers = config.getBoolean("informPlayers", informPlayers);
        this.checkMetadata = config.getBoolean("check-metadata", checkMetadata);
        this.checkItemUse = config.getBoolean("item-use-check", checkItemUse);
        this.enableWhitelist = config.getBoolean("whitelist", enableWhitelist);
    }

    protected boolean permissionDenied(Player player, String basePermission, Object... arguments) {
        String permission = assemblePermission(basePermission, arguments);
        boolean isDenied = !player.hasPermission(permission);

        if (isDenied) {
            this.informer.informPlayer(player, permission, arguments);
        }

        return isDenied;
    }

    protected boolean _permissionDenied(Player player, String permission, Object... arguments) {
        return !player.hasPermission(assemblePermission(permission, arguments));
    }

    protected String assemblePermission(String permission, Object... arguments) {
        StringBuilder builder = new StringBuilder(permission);

        if (arguments != null) {
            for (Object obj : arguments) {
                if (obj == null) {
                    continue;
                }

                builder.append('.');
                builder.append(ModifyworldPermissionRegister.getObjectPermission(obj));
            }
        }

        return builder.toString();
    }

    private void registerEvents(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
