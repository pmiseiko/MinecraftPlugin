package ca.nightfury.minecraft.plugin.block.protection;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DatabaseManager2 implements Database, Flushable, Closeable
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public DatabaseManager2(final File dataFolder, final PluginLogger logger) throws SQLException
    {
        final File dbFile = new File(dataFolder, MAPDB_DATABASE_FILENAME);
        m_database =
                DBMaker.fileDB(dbFile).closeOnJvmShutdown().concurrencyDisable().fileMmapEnableIfSupported().make();
        m_ownership = m_database.hashMap(
                "BlockOwnership",
                BlockIdentitySerializer.SINGLETON,
                PlayerIdentitySerializer.SINGLETON).createOrOpen();

        m_logger = logger;
    }

    public void integrityCheck(final Server server)
    {
        final Map<BlockIdentity, PlayerIdentity> blockOwners = getBlockOwners();
        for (final Entry<BlockIdentity, PlayerIdentity> entry : blockOwners.entrySet())
        {
            final BlockIdentity blockIdentity = entry.getKey();
            entry.getValue();

            final UUID worldUUID = blockIdentity.getWorldUUID();
            final int xCoordinate = blockIdentity.getXCoordinate();
            final int yCoordinate = blockIdentity.getYCoordinate();
            final int zCoordinate = blockIdentity.getZCoordinate();

            final World world = server.getWorld(worldUUID);
            final Environment worldEnvironment = world.getEnvironment();
            final String worldName = world.getName();
            final Block worldBlock = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);
            final Material worldBlockType = worldBlock.getType();
            final String worldBlockTypeName = worldBlockType.name();
            final String databaseBlockTypeName = getBlockType(blockIdentity);

            if (!Objects.equals(worldEnvironment, Environment.NORMAL))
            {
                m_logger.warning(
                        String.format(
                                "World protection disabled for block %s in %s at %d/%d/%d",
                                worldBlockType,
                                worldName,
                                xCoordinate,
                                yCoordinate,
                                zCoordinate));
                deleteBlockOwner(blockIdentity);
            }
            else if (!ProtectedMaterials.isProtectedMaterial(worldBlockType))
            {
                m_logger.warning(
                        String.format(
                                "Invalid block %s protected in %s at %d/%d/%d",
                                worldBlockType,
                                worldName,
                                xCoordinate,
                                yCoordinate,
                                zCoordinate));
                deleteBlockOwner(blockIdentity);
            }
            /*
            else if (!Objects.equals(worldBlockTypeName, databaseBlockTypeName))
            {
                m_logger.warning(
                        String.format(
                                "Database inconsistent in %s at %d/%d/%d world block %s != database block %s",
                                worldName,
                                xCoordinate,
                                yCoordinate,
                                zCoordinate,
                                worldBlockType,
                                databaseBlockTypeName));
                deleteBlockOwner(blockIdentity);
            }
            */
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Database Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean createBlockOwner(final Block block, final PlayerIdentity playerIdentity)
    {
        m_ownership.put(new BlockIdentity(block), playerIdentity);
        return true;
    }

    @Override
    public boolean deleteBlockOwner(final BlockIdentity blockIdentity)
    {
        m_ownership.remove(blockIdentity);
        return true;
    }

    @Override
    public boolean isBlockOwned(final BlockIdentity blockIdentity)
    {
        return m_ownership.containsKey(blockIdentity);
    }

    @Override
    public boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        return Objects.equals(playerIdentity, m_ownership.get(blockIdentity));
    }

    @Override
    public Map<BlockIdentity, PlayerIdentity> getBlockOwners()
    {
        return m_ownership;
    }

    @Override
    public String getBlockType(final BlockIdentity blockIdentity)
    {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flushable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void flush() throws IOException
    {
        m_database.commit();
        m_logger.info("Block protection database flushed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Closeable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void close() throws IOException
    {
        m_database.close();
        m_logger.info("Block protection database closed");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static String MAPDB_DATABASE_FILENAME = "mapdb_block_manager.db";
    private final DB m_database;
    private final Map<BlockIdentity, PlayerIdentity> m_ownership;
    private final PluginLogger m_logger;
}
