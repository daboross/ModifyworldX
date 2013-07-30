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
package net.daboross.modifyworld.bukkit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.daboross.modifyworld.ModifyworldListener;
import net.daboross.modifyworld.PlayerInformer;
import net.daboross.modifyworld.handlers.BlockListener;
import net.daboross.modifyworld.handlers.PlayerListener;
import net.daboross.modifyworld.handlers.VehicleListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.PluginManager;
import net.daboross.modifyworld.handlers.EntityListener;

/**
 *
 * @author t3hk0d3
 */
public class Modifyworld extends JavaPlugin {

    protected final static Class<? extends ModifyworldListener>[] LISTENERS = new Class[]{
        PlayerListener.class,
        BlockListener.class,
        VehicleListener.class,
        EntityListener.class
    };
    protected List<ModifyworldListener> listeners = new ArrayList<ModifyworldListener>();
    protected PlayerInformer informer;
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
        this.informer = new PlayerInformer(config);
        this.registerListeners();
        this.saveConfig();
        ModifyworldPermissionRegister.registerAllPermissions(this);
        this.getLogger().info("Modifyworld enabled!");
    }

    @Override
    public void onDisable() {
        this.listeners.clear();
        this.config = null;
        this.getLogger().info("Modifyworld successfully disabled!");
    }

    protected void initializeConfiguration(FileConfiguration config) {
        // Flags
        config.set("item-restrictions", false);
        config.set("inform-players", false);
        config.set("whitelist", false);
        config.set("use-material-names", true);
        config.set("drop-restricted-item", false);
        config.set("item-use-check", false);

        // Messages
        config.set("messages/message-format", PlayerInformer.DEFAULT_MESSAGE_FORMAT);
        config.set("messages/default-message", PlayerInformer.PERMISSION_DENIED);

        // Predefined messages
        config.set("messages/modifyworld.items.have", PlayerInformer.PROHIBITED_ITEM);
    }

    protected void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        for (Class<? extends ModifyworldListener> listenerClass : LISTENERS) {
            ModifyworldListener listener;
            try {
                Constructor<? extends ModifyworldListener> constructor =
                        listenerClass.getConstructor(Plugin.class, ConfigurationSection.class, PlayerInformer.class);
                listener = constructor.newInstance(this, this.getConfig(), this.informer);
            } catch (Throwable e) {
                this.getLogger().log(Level.WARNING, "Failed to initialize \"{0}\" listener", listenerClass.getName());
                this.getLogger().log(Level.WARNING, "Initialization error: ", e);
                continue;
            }
            pm.registerEvents(listener, this);
            this.listeners.add(listener);
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
            config.load(configFile);
        } catch (FileNotFoundException e) {
            this.getLogger().severe("Configuration file not found - deploying default one");
            InputStream defConfigStream = getResource("config.yml");
            if (defConfigStream != null) {
                try {
                    this.config.load(defConfigStream);
                } catch (Exception de) {
                    this.getLogger().severe("Default config file is broken.");
                }
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to load configuration file: {0}", e.getMessage());
        }
        InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream != null) {
            this.config.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
        }
    }
}
