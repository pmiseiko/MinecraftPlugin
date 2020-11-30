package ca.nightfury.minecraft.plugin.block.rewards;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.BlockIdentitySerializer;

public class RewardDatabaseImpl implements RewardDatabase
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public RewardDatabaseImpl(final File dataFolder, final PluginLogger logger)
    {
        final File dbFile = new File(dataFolder, DATABASE_FILE_NAME);
        final Maker dbMaker = DBMaker.fileDB(dbFile);

        dbMaker.closeOnJvmShutdown();
        dbMaker.concurrencyDisable();
        dbMaker.fileMmapEnableIfSupported();

        m_database = dbMaker.make();
        m_rewardedBlocks = m_database.hashSet("RewardedBlocks", BlockIdentitySerializer.SINGLETON).createOrOpen();
        m_logger = logger;
        m_logger.info(String.format("Rewarded Block(s): %d", m_rewardedBlocks.size()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // RewardDatabase Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean rewardBlock(final BlockIdentity blockIdentity)
    {
        final boolean rewardBlock = m_rewardedBlocks.contains(blockIdentity);
        if (!rewardBlock)
        {
            m_rewardedBlocks.add(blockIdentity);
        }

        return rewardBlock;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flushable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void flush() throws IOException
    {
        m_database.commit();
        m_logger.info("Rewarded blocks database flushed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Closeable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void close() throws IOException
    {
        m_database.close();
        m_logger.info("Rewarded blocks database closed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static String DATABASE_FILE_NAME = "rewarded_blocks.db";
    private final DB m_database;
    private final Set<BlockIdentity> m_rewardedBlocks;
    private final PluginLogger m_logger;
}
