package ca.nightfury.minecraft.plugin.player.hearth;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DB.HashSetMaker;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;

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

        final HashSetMaker<PlayerIdentity> hearthDisabledMaker =
                m_database.hashSet("HearthDisabled", PlayerIdentitySerializer.SINGLETON);

        hearthDisabledMaker.expireAfterCreate(1, TimeUnit.DAYS);
        hearthDisabledMaker.expireAfterCreate();

        m_hearthDisabled = hearthDisabledMaker.createOrOpen();
        m_logger = logger;
        m_logger.info(String.format("Disabled Hearth(s): %d", m_hearthDisabled.size()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // RewardDatabase Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void disableHearth(final PlayerIdentity playerIdentity)
    {
        m_hearthDisabled.add(playerIdentity);
    }

    @Override
    public boolean isHearthDisabled(final PlayerIdentity playerIdentity)
    {
        return m_hearthDisabled.contains(playerIdentity);
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
    private final DB m_database;
    private final Set<PlayerIdentity> m_hearthDisabled;
    private final PluginLogger m_logger;
}
