package ca.nightfury.minecraft.plugin.block.protection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.block.Block;
import org.bukkit.plugin.PluginLogger;

public class DatabaseCache implements Database
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public DatabaseCache(final Database database, final PluginLogger logger) throws SQLException
    {
        m_database = database;
        m_databaseCache = database.getBlockOwners();
        m_logger = logger;
        m_logger.info(String.format("Database cache loaded %d block(s)", m_databaseCache.size()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Database Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean createBlockOwner(final Block block, final PlayerIdentity playerIdentity)
    {
        if (m_database.createBlockOwner(block, playerIdentity))
        {
            m_databaseCache.put(new BlockIdentity(block), playerIdentity);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteBlockOwner(final BlockIdentity blockIdentity)
    {
        if (m_database.deleteBlockOwner(blockIdentity))
        {
            m_databaseCache.remove(blockIdentity);
            return true;
        }

        return false;
    }

    @Override
    public boolean isBlockOwned(final BlockIdentity blockIdentity)
    {
        return m_databaseCache.containsKey(blockIdentity);
    }

    @Override
    public boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        return Objects.equals(playerIdentity, m_databaseCache.get(blockIdentity));
    }

    @Override
    public Map<BlockIdentity, PlayerIdentity> getBlockOwners()
    {
        return new HashMap<>(m_databaseCache);
    }

    @Override
    public String getBlockType(final BlockIdentity blockIdentity)
    {
        return m_database.getBlockType(blockIdentity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final Database m_database;
    private final Map<BlockIdentity, PlayerIdentity> m_databaseCache;
    private final PluginLogger m_logger;
}
