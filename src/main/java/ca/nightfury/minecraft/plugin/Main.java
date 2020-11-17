package ca.nightfury.minecraft.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ca.nightfury.minecraft.plugin.block.protection.BlockManager;
import ca.nightfury.minecraft.plugin.block.protection.DatabaseCache;
import ca.nightfury.minecraft.plugin.block.protection.DatabaseManager;
import ca.nightfury.minecraft.plugin.block.protection.ProtectionEventListener;
import ca.nightfury.minecraft.plugin.block.rewards.RewardEventListener;
import ca.nightfury.minecraft.plugin.block.tombstone.TombstoneEventListener;
import ca.nightfury.minecraft.plugin.entity.squids.SquidEventListener;
import ca.nightfury.minecraft.plugin.inventory.autoreplace.AutoReplaceEventListener;

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

        try
        {
            final DatabaseManager databaseManager = new DatabaseManager(dataFolder, m_logger);
            final Server server = getServer();

            databaseManager.integrityCheck(server);

            m_closeables.add(databaseManager);
            m_flushables.add(databaseManager);

            final DatabaseCache databaseCache = new DatabaseCache(databaseManager, m_logger);
            final BlockManager blockManager = new BlockManager(databaseCache, m_logger);
            final ProtectionEventListener eventListener = new ProtectionEventListener(this, blockManager, m_logger);

            eventListener.integrityCheck(server);

            m_listeners.add(eventListener);
        }
        catch (final SQLException exception)
        {
            m_logger.log(Level.SEVERE, "Block protection initialization error", exception);
        }

        m_listeners.add(new AutoReplaceEventListener(m_logger));
        m_listeners.add(new RewardEventListener(m_logger));
        // m_listeners.add(new TombstoneEventListener(this, m_logger));
        m_listeners.add(new SquidEventListener());

        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        for (final Listener listener : m_listeners)
        {
            pluginManager.registerEvents(listener, this);
            m_logger.info(String.format("Registered event listener: %s", listener));
        }
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