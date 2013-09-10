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
package net.daboross.modifyworldx.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.daboross.modifyworldx.PermissionsHelper;
import net.daboross.modifyworldx.handlers.BlockListener;
import net.daboross.modifyworldx.handlers.PlayerListener;
import net.daboross.modifyworldx.handlers.VehicleListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.plugin.PluginManager;
import net.daboross.modifyworldx.handlers.EntityListener;
import org.bukkit.event.Listener;

/**
 *
 * @author t3hk0d3
 */
public class Modifyworld extends JavaPlugin {

    protected final static Class<? extends PermissionsHelper>[] LISTENERS = new Class[]{
        PlayerListener.class,
        BlockListener.class,
        VehicleListener.class,
        EntityListener.class
    };
    protected File configFile;
    protected FileConfiguration config;

    @Override
    public void onLoad() {
        configFile = new File(this.getDataFolder(), "config.yml");
    }

    @Override
    public void onEnable() {
        this.config = this.getConfig();
        if (!config.isConfigurationSection("messages")) {
            this.getLogger().severe("Deploying default config");
            this.initializeConfiguration(config);
        }
        this.registerListeners();
        this.saveConfig();
        ModifyworldPermissionRegister.registerAllPermissions(this);
    }

    @Override
    public void onDisable() {
        this.config = null;
    }

    protected void initializeConfiguration(FileConfiguration config) {
        // Flags
        config.set("item-restrictions", false);
        config.set("inform-players", false);
        config.set("whitelist", false);
        config.set("use-material-names", true);
        config.set("drop-restricted-item", false);
        config.set("item-use-check", false);
    }

    protected void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        this.registerListeners(pm, new BlockListener(), new EntityListener(), new PlayerListener(this, config), new VehicleListener());
    }

    protected void registerListeners(PluginManager pm, Listener... listeners) {
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    @Override
    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    @Override
    public void saveConfig() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to save configuration file: {0}", e.getMessage());
        }
    }

    @Override
    public void reloadConfig() {
        this.config = new YamlConfiguration();
        config.options().pathSeparator('/');
        try {
            if (configFile.exists()) {
                config.load(configFile);
                InputStream defConfigStream = getResource("config.yml");
                if (defConfigStream != null) {
                    this.config.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
                }
            } else {
                this.getLogger().info("Deploying default configuration");
                InputStream defConfigStream = getResource("config.yml");
                if (defConfigStream != null) {
                    try {
                        this.config.load(defConfigStream);
                    } catch (Exception dex) {
                        this.getLogger().log(Level.SEVERE, "Failed to load default configuration", dex);
                    }
                }
            }
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, "Failed to load configuration", ex);
        }
    }
}
