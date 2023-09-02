package group.aelysium.rustyconnector.plugin.velocity.central;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import group.aelysium.rustyconnector.core.central.PluginLifecycle;
import group.aelysium.rustyconnector.core.lib.config.MigrationDirections;
import group.aelysium.rustyconnector.core.lib.exception.DuplicateLifecycleException;
import group.aelysium.rustyconnector.core.lib.exception.NoOutputException;
import group.aelysium.rustyconnector.core.lib.lang_messaging.Lang;
import group.aelysium.rustyconnector.plugin.velocity.PluginLogger;
import group.aelysium.rustyconnector.plugin.velocity.config.*;
import group.aelysium.rustyconnector.plugin.velocity.events.*;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang_messaging.VelocityLang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;

public class VelocityLifecycle extends PluginLifecycle {
    public boolean start() throws DuplicateLifecycleException {
        VelocityAPI api = VelocityAPI.get();
        PluginLogger logger = api.logger();

        if(this.isRunning()) throw new DuplicateLifecycleException("RustyConnector-Velocity is already running! You can't start it a second time!");

        MigrationDirections.init();

        if(!loadConfigs()) return false;
        if(!loadCommands()) return false;
        if(!loadEvents()) return false;

        VelocityLang.WORDMARK_RUSTY_CONNECTOR.send(logger, api.version());

        WhitelistConfig.empty();
        DefaultConfig.empty();
        ScalarFamilyConfig.empty();

        this.isRunning = true;
        return true;
    }
    public void stop() {
        try {
            VelocityAPI api = VelocityAPI.get();

            WhitelistConfig.empty();
            DefaultConfig.empty();
            ScalarFamilyConfig.empty();
            LoggerConfig.empty();

            try {
                api.core().kill();
            } catch (Exception ignore) {}

            api.velocityServer().getCommandManager().unregister("rc");
            api.velocityServer().getCommandManager().unregister("tpa");

            this.isRunning = false;

            api.velocityServer().getEventManager().unregisterListener(api.accessPlugin(), new OnPlayerChooseInitialServer());
            api.velocityServer().getEventManager().unregisterListener(api.accessPlugin(), new OnPlayerChangeServer());
            api.velocityServer().getEventManager().unregisterListener(api.accessPlugin(), new OnPlayerKicked());
            api.velocityServer().getEventManager().unregisterListener(api.accessPlugin(), new OnPlayerDisconnect());
        } catch (Exception ignore) {}
    }
    protected boolean loadCommands() {
        VelocityAPI api = VelocityAPI.get();
        PluginLogger logger = api.logger();

        CommandManager commandManager = api.velocityServer().getCommandManager();
        try {
            commandManager.register(
                    commandManager.metaBuilder("rc")
                            .aliases("/rc", "//") // Add slash variants so that they can be used in console as well
                            .build(),
                    CommandRusty.create()
                    );

            commandManager.unregister("server");
          
            // Commands for specific services can be found in the constructors for those services
  
            return true;
        } catch (Exception e) {
            VelocityLang.BOXED_MESSAGE_COLORED.send(logger, Component.text(e.getMessage()), NamedTextColor.RED);
            return false;
        }
    }

    protected boolean loadEvents() {
        VelocityAPI api = VelocityAPI.get();
        PluginLogger logger = api.logger();

        EventManager manager = api.velocityServer().getEventManager();
        try {
            manager.register(api.accessPlugin(), new OnPlayerChooseInitialServer());
            manager.register(api.accessPlugin(), new OnPlayerChangeServer());
            manager.register(api.accessPlugin(), new OnPlayerKicked());
            manager.register(api.accessPlugin(), new OnPlayerDisconnect());

            return true;
        } catch (Exception e) {
            VelocityLang.BOXED_MESSAGE_COLORED.send(logger, Component.text(e.getMessage()), NamedTextColor.RED);
            return false;
        }
    }
}
