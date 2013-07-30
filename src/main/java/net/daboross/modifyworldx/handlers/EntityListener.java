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
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.ConfigurationSection;
import net.daboross.modifyworldx.ModifyworldListener;
import net.daboross.modifyworldx.PlayerInformer;

/**
 *
 * @author t3hk0d3
 */
public class EntityListener extends ModifyworldListener {

    public EntityListener(Plugin plugin, ConfigurationSection config, PlayerInformer informer) {
        super(plugin, config, informer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) event;
            if (edbe.getDamager() instanceof Player) { // Prevent from damaging by player
                Player player = (Player) edbe.getDamager();
                if (isPermissionDeniedMessage(player, "modifyworld.damage.deal", event.getEntity())) {
                    event.setCancelled(true);
                }
            }
            if (edbe.getEntity() instanceof Player) {
                Player player = (Player) edbe.getEntity();
                if (edbe.getDamager() != null && player.isOnline()) { // Prevent from taking damage by entity
                    if (isPermissionDenied(player, "modifyworld.damage.take", edbe.getDamager())) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getEntity() instanceof Player) { // player are being damaged by enviroment
            Player player = (Player) event.getEntity();
            if (isPermissionDenied(player, "modifyworld.damage.take", event.getCause())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTame(EntityTameEvent event) {
        if ((event.getOwner() instanceof Player)) {
            Player player = (Player) event.getOwner();
            if (isPermissionDeniedMessage(player, "modifyworld.tame", event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (isPermissionDenied(player, "modifyworld.mobtarget", event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }
}
