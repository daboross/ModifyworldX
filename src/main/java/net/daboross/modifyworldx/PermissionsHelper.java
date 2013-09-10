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
package net.daboross.modifyworldx;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import net.daboross.modifyworldx.bukkit.ModifyworldPermissionRegister;

public abstract class PermissionsHelper implements Listener {

    public static String assemblePermission(String base, Entity obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, BlockState obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, Material obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, Block obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, ItemStack obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, Enum obj) {
        return "modifyworld." + base + "." + ModifyworldPermissionRegister.getPermission(obj);
    }

    public static String assemblePermission(String base, String obj) {
        return "modifyworld." + base + "." + obj.toLowerCase();
    }
}
