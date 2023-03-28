package group.aelysium.rustyconnector.plugin.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import group.aelysium.rustyconnector.core.lib.config.MigrationDirections;
import group.aelysium.rustyconnector.core.lib.exception.NoOutputException;
import group.aelysium.rustyconnector.core.lib.lang_messaging.Lang;
import group.aelysium.rustyconnector.plugin.velocity.commands.CommandRusty;
import group.aelysium.rustyconnector.plugin.velocity.commands.CommandTPA;
import group.aelysium.rustyconnector.plugin.velocity.lib.config.DefaultConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.config.FamilyConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.config.LoggerConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.config.WhitelistConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.events.OnPlayerChangeServer;
import group.aelysium.rustyconnector.plugin.velocity.lib.events.OnPlayerChooseInitialServer;
import group.aelysium.rustyconnector.plugin.velocity.lib.events.OnPlayerDisconnect;
import group.aelysium.rustyconnector.plugin.velocity.lib.events.OnPlayerKicked;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang_messaging.VelocityLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.module.Proxy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;

public class Engine {
    public static boolean start() {
        VelocityRustyConnector plugin = VelocityRustyConnector.getInstance();

        MigrationDirections.init();

        if(!initConfigs(plugin)) return false;
        if(!initCommands(plugin)) return false;
        if(!initEvents(plugin)) return false;

        VelocityLang.WORDMARK_RUSTY_CONNECTOR.send(plugin.logger());

        DefaultConfig defaultConfig = DefaultConfig.getConfig();
        if(defaultConfig.isBootCommands_enabled()) {
            plugin.logger().log("Issuing boot commands...");
            defaultConfig.getBootCommands_commands().forEach(command -> {
                plugin.logger().log(">>> "+command);
                plugin.getVirtualServer().dispatchCommand(command);
            });
        }

        WhitelistConfig.empty();
        DefaultConfig.empty();
        FamilyConfig.empty();

        return true;
    }
    public static void stop() {
        try {
            VelocityRustyConnector plugin = VelocityRustyConnector.getInstance();

            WhitelistConfig.empty();
            DefaultConfig.empty();
            FamilyConfig.empty();
            LoggerConfig.empty();

            if(plugin.getVirtualServer() != null) {
                plugin.getVirtualServer().killHeartbeats();
                plugin.getVirtualServer().killRedis();
                plugin.unsetVirtualServer();
            }

            plugin.getVelocityServer().getCommandManager().unregister("rc");

            plugin.getVelocityServer().getEventManager().unregisterListener(plugin, new OnPlayerChooseInitialServer());
            plugin.getVelocityServer().getEventManager().unregisterListener(plugin, new OnPlayerChangeServer());
            plugin.getVelocityServer().getEventManager().unregisterListener(plugin, new OnPlayerKicked());
            plugin.getVelocityServer().getEventManager().unregisterListener(plugin, new OnPlayerDisconnect());
        } catch (Exception ignore) {}
    }

    private static boolean initConfigs(VelocityRustyConnector plugin) {
        try {
            DefaultConfig defaultConfig = DefaultConfig.newConfig(new File(plugin.getDataFolder(), "config.yml"), "velocity_config_template.yml");
            if(!defaultConfig.generate()) {
                throw new IllegalStateException("Unable to load or create config.yml!");
            }
            defaultConfig.register();

            LoggerConfig loggerConfig = LoggerConfig.newConfig(new File(plugin.getDataFolder(), "logger.yml"), "velocity_logger_template.yml");
            if(!loggerConfig.generate()) {
                throw new IllegalStateException("Unable to load or create logger.yml!");
            }
            loggerConfig.register();
            PluginLogger.init(loggerConfig);

            plugin.setVirtualServer(Proxy.init(defaultConfig));

            return true;
        } catch (NoOutputException ignore) {
            return false;
        } catch (Exception e) {
            Lang.BOXED_MESSAGE_COLORED.send(plugin.logger(), Component.text(e.getMessage()), NamedTextColor.RED);
            return false;
        }
    }
    private static boolean initCommands(VelocityRustyConnector plugin) {
        CommandManager commandManager = plugin.getVelocityServer().getCommandManager();
        try {
            commandManager.register(
                    commandManager.metaBuilder("rustyconnector")
                            .aliases("rusty", "rc")
                            .aliases("/rustyconnector","/rusty","/rc") // Add slash variants so that they can be used in console as well
                            .build(),
                    CommandRusty.create()
                    );

            commandManager.unregister("server");

            commandManager.register(
                    commandManager.metaBuilder("tpa")
                            .build(),
                    CommandTPA.create()
            );

            return true;
        } catch (Exception e) {
            VelocityLang.BOXED_MESSAGE_COLORED.send(plugin.logger(), Component.text(e.getMessage()), NamedTextColor.RED);
            return false;
        }
    }

    private static boolean initEvents(VelocityRustyConnector plugin) {
        EventManager manager = plugin.getVelocityServer().getEventManager();
        try {
            manager.register(plugin, new OnPlayerChooseInitialServer());
            manager.register(plugin, new OnPlayerChangeServer());
            manager.register(plugin, new OnPlayerKicked());
            manager.register(plugin, new OnPlayerDisconnect());

            return true;
        } catch (Exception e) {
            VelocityLang.BOXED_MESSAGE_COLORED.send(plugin.logger(), Component.text(e.getMessage()), NamedTextColor.RED);
            return false;
        }
    }
}
