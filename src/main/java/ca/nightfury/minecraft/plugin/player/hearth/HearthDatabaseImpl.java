package ca.nightfury.minecraft.plugin.player.hearth;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.mapdb.Serializer;

import ca.nightfury.minecraft.plugin.database.PlayerIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentitySerializer;

public class HearthDatabaseImpl implements HearthDatabase
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public HearthDatabaseImpl(final File dataFolder, final PluginLogger logger)
    {
        final File dbFile = new File(dataFolder, DATABASE_FILE_NAME);
        final Maker dbMaker = DBMaker.fileDB(dbFile);

        dbMaker.concurrencyDisable();
        dbMaker.fileMmapEnableIfSupported();

        m_database = dbMaker.make();
        m_hearthCooldownTimeout = m_database.hashMap(
                "HearthCooldownTimeout",
                PlayerIdentitySerializer.SINGLETON,
                Serializer.LONG).createOrOpen();
        m_logger = logger;
        m_logger.info(String.format("Disabled Hearth(s): %d", m_hearthCooldownTimeout.size()));
        m_logger.info(String.format("Hearth Cooldown Timeout: %d", HEARTH_COOLDOWN_TIMEOUT));
    }

    ///////////////////////////////////////////////////////////////////////////
    // RewardDatabase Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void disableHearth(final PlayerIdentity playerIdentity)
    {
        m_hearthCooldownTimeout.put(playerIdentity, System.currentTimeMillis() + HEARTH_COOLDOWN_TIMEOUT);
    }

    @Override
    public boolean isHearthDisabled(final PlayerIdentity playerIdentity)
    {
        if (m_hearthCooldownTimeout.containsKey(playerIdentity))
        {
            final long timeout = m_hearthCooldownTimeout.get(playerIdentity);
            return timeout > System.currentTimeMillis();
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flushable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void flush() throws IOException
    {
        m_database.commit();
        m_logger.info("Hearth database flushed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Closeable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void close() throws IOException
    {
        m_database.close();
        m_logger.info("Hearth database closed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static String DATABASE_FILE_NAME = "hearth.db";
    private final static long HEARTH_COOLDOWN_TIMEOUT = TimeUnit.DAYS.toMillis(1);
    private final DB m_database;
    private final Map<PlayerIdentity, Long> m_hearthCooldownTimeout;
    private final PluginLogger m_logger;
}
