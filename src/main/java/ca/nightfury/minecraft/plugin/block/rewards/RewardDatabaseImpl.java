package ca.nightfury.minecraft.plugin.block.rewards;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DB.HashSetMaker;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.BlockIdentitySerializer;

public class RewardDatabaseImpl implements RewardDatabase
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public RewardDatabaseImpl(final PluginLogger logger)
    {
        final Maker dbMaker = DBMaker.memoryDB();

        dbMaker.concurrencyDisable();

        m_database = dbMaker.make();

        final HashSetMaker<BlockIdentity> rewardedBlocksMaker =
                m_database.hashSet("RewardedBlocks", BlockIdentitySerializer.SINGLETON);

        rewardedBlocksMaker.expireAfterCreate(1, TimeUnit.DAYS);

        m_rewardedBlocks = rewardedBlocksMaker.createOrOpen();
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

    private final DB m_database;
    private final Set<BlockIdentity> m_rewardedBlocks;
    private final PluginLogger m_logger;
}
