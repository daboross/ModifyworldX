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
import org.bukkit.event.entity.*;
import static net.daboross.modifyworldx.PermissionsHelper.assemblePermission;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;
            if (evt.getDamager() instanceof Player) { // Prevent from damaging by player
                Player player = (Player) evt.getDamager();
                if (!player.hasPermission(assemblePermission("damage.deal", event.getEntity()))) {
                    event.setCancelled(true);
                }
            }
            if (evt.getEntity() instanceof Player) {
                Player player = (Player) evt.getEntity();
                if (evt.getDamager() != null && player.isOnline()) { // Prevent from taking damage by entity
                    if (!player.hasPermission(assemblePermission("damage.take", evt.getDamager()))) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getEntity() instanceof Player) { // player are being damaged by enviroment
            Player player = (Player) event.getEntity();
            if (!player.hasPermission(assemblePermission("damage.take", event.getCause()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTame(EntityTameEvent event) {
        if ((event.getOwner() instanceof Player)) {
            Player player = (Player) event.getOwner();
            if (!player.hasPermission(assemblePermission("tame", event.getEntity()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (!player.hasPermission(assemblePermission("mobtarget", event.getEntity()))) {
                event.setCancelled(true);
            }
        }
    }
}
