package ca.nightfury.minecraft.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ca.nightfury.minecraft.plugin.block.protection.ProtectionDatabase;
import ca.nightfury.minecraft.plugin.block.protection.ProtectionDatabaseImpl;
import ca.nightfury.minecraft.plugin.block.protection.ProtectionEventListener;
import ca.nightfury.minecraft.plugin.block.protection.ProtectionManager;
import ca.nightfury.minecraft.plugin.block.protection.ProtectionManagerImpl;
import ca.nightfury.minecraft.plugin.block.rewards.RewardDatabase;
import ca.nightfury.minecraft.plugin.block.rewards.RewardDatabaseImpl;
import ca.nightfury.minecraft.plugin.block.rewards.RewardEventListener;
import ca.nightfury.minecraft.plugin.entity.squids.SquidEventListener;
import ca.nightfury.minecraft.plugin.inventory.autoreplace.AutoReplaceEventListener;
import ca.nightfury.minecraft.plugin.news.NewsEventListener;
import ca.nightfury.minecraft.plugin.player.hearth.HearthCommandHandler;
import ca.nightfury.minecraft.plugin.player.hearth.HearthDatabase;
import ca.nightfury.minecraft.plugin.player.hearth.HearthDatabaseImpl;
import ca.nightfury.minecraft.plugin.player.tombstone.TombstoneEventListener;

public class Main extends JavaPlugin
{
    ///////////////////////////////////////////////////////////////////////////
    // JavaPlugin Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onEnable()
    {
        super.onEnable();

        final File dataFolder = getDataFolder();
        if (!dataFolder.exists())
        {
            dataFolder.mkdir();
        }

        final ProtectionDatabase protectionDatabase = new ProtectionDatabaseImpl(dataFolder, m_logger);
        final ProtectionManager protectionManager = new ProtectionManagerImpl(protectionDatabase, m_logger);

        m_closeables.add(protectionDatabase);
        m_flushables.add(protectionDatabase);

        final Server server = getServer();

        protectionDatabase.integrityCheck(server);
        protectionManager.integrityCheck(server);

        final RewardDatabase rewardDatabase = new RewardDatabaseImpl(m_logger);

        m_closeables.add(rewardDatabase);
        m_flushables.add(rewardDatabase);

        m_listeners.add(new AutoReplaceEventListener(m_logger));
        m_listeners.add(new NewsEventListener());
        m_listeners.add(new ProtectionEventListener(this, protectionManager, m_logger));
        m_listeners.add(new RewardEventListener(rewardDatabase, m_logger));
        m_listeners.add(new SquidEventListener());
        m_listeners.add(new TombstoneEventListener(m_logger));

        final PluginManager pluginManager = server.getPluginManager();
        for (final Listener listener : m_listeners)
        {
            pluginManager.registerEvents(listener, this);
            m_logger.info(String.format("Registered event listener: %s", listener));
        }

        final HearthDatabase hearthDatabase = new HearthDatabaseImpl(dataFolder, m_logger);

        m_closeables.add(hearthDatabase);
        m_flushables.add(hearthDatabase);

        final CommandExecutor hearthCommandHandler = new HearthCommandHandler(hearthDatabase, server, m_logger);
        final PluginCommand pluginCommand = getCommand("hearth");

        pluginCommand.setExecutor(hearthCommandHandler);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        for (final Listener listener : m_listeners)
        {
            HandlerList.unregisterAll(listener);
            m_logger.info(String.format("Unregistered event listener: %s", listener));
        }

        for (final Flushable flushable : m_flushables)
        {
            try
            {
                flushable.flush();
            }
            catch (final IOException exception)
            {
                m_logger.log(
                        Level.SEVERE,
                        String.format("Flush failure for %s", flushable.getClass().getSimpleName()),
                        exception);
            }
        }

        for (final Closeable closeable : m_closeables)
        {
            try
            {
                closeable.close();
            }
            catch (final IOException exception)
            {
                m_logger.log(
                        Level.SEVERE,
                        String.format("Close failure for %s", closeable.getClass().getSimpleName()),
                        exception);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final List<Closeable> m_closeables = new LinkedList<>();
    private final List<Flushable> m_flushables = new LinkedList<>();
    private final List<Listener> m_listeners = new LinkedList<>();
    private final PluginLogger m_logger = new PluginLogger(this);
}