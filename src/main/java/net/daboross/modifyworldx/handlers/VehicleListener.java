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
package net.daboross.modifyworldx.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import static net.daboross.modifyworldx.PermissionsHelper.assemblePermission;
import org.bukkit.event.Listener;

public class VehicleListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player
                && !((Player) event.getAttacker()).hasPermission(assemblePermission("vehicle.destroy", event.getVehicle()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player
                && !((Player) event.getEntered()).hasPermission(assemblePermission("vehicle.enter", event.getVehicle()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player
                && !((Player) event.getEntity()).hasPermission(assemblePermission("vehicle.collide", event.getVehicle()))) {
            event.setCancelled(true);
            event.setCollisionCancelled(true);
            event.setPickupCancelled(true);

        }

    }
}