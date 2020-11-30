package ca.nightfury.minecraft.plugin.block.protection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginLogger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.BlockIdentitySerializer;
import ca.nightfury.minecraft.plugin.database.MaterialSerializer;
import ca.nightfury.minecraft.plugin.database.PlayerIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentitySerializer;

public class ProtectionDatabaseImpl implements ProtectionDatabase
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public ProtectionDatabaseImpl(final File dataFolder, final PluginLogger logger)
    {
        final File dbFile = new File(dataFolder, BLOCK_OWNERSHIP_DATABASE_FILENAME);
        final Maker dbMaker = DBMaker.fileDB(dbFile);

        dbMaker.closeOnJvmShutdown();
        dbMaker.concurrencyDisable();
        dbMaker.fileMmapEnableIfSupported();

        m_database = dbMaker.make();
        m_blockOwnership = m_database.hashMap(
                "BlockOwnership",
                BlockIdentitySerializer.SINGLETON,
                PlayerIdentitySerializer.SINGLETON).createOrOpen();
        m_blockType = m_database.hashMap(
                "BlockType",
                BlockIdentitySerializer.SINGLETON,
                MaterialSerializer.SINGLETON).createOrOpen();
        m_logger = logger;
        m_logger.info(String.format("Registered Block Owner(s): %d", m_blockOwnership.size()));
        m_logger.info(String.format("Registered Block Type(s): %d", m_blockType.size()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Database Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void integrityCheck(final Server server)
    {
        final Set<BlockIdentity> ownedBlocks = getOwnedBlocks();
        for (final BlockIdentity blockIdentity : ownedBlocks)
        {
            final UUID worldUUID = blockIdentity.getWorldUUID();
            final int xCoordinate = blockIdentity.getXCoordinate();
            final int yCoordinate = blockIdentity.getYCoordinate();
            final int zCoordinate = blockIdentity.getZCoordinate();

            final World world = server.getWorld(worldUUID);
            final Environment worldEnvironment = world.getEnvironment();
            final String worldName = world.getName();
            final Block worldBlock = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);
            final Material worldBlockType = worldBlock.getType();
            final Material databaseBlockType = getBlockType(blockIdentity);

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
                deleteBlockType(blockIdentity);
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
                deleteBlockType(blockIdentity);
            }
            else if (!Objects.equals(worldBlockType, databaseBlockType))
            {
                m_logger.warning(
                        String.format(
                                "Database inconsistent in %s at %d/%d/%d world block %s != database block %s",
                                worldName,
                                xCoordinate,
                                yCoordinate,
                                zCoordinate,
                                worldBlockType,
                                databaseBlockType));

                deleteBlockOwner(blockIdentity);
                deleteBlockType(blockIdentity);
            }
        }

        final Map<BlockIdentity, Material> blockTypes = getBlockTypes();
        for (final BlockIdentity blockIdentity : blockTypes.keySet())
        {
            final UUID worldUUID = blockIdentity.getWorldUUID();
            final int xCoordinate = blockIdentity.getXCoordinate();
            final int yCoordinate = blockIdentity.getYCoordinate();
            final int zCoordinate = blockIdentity.getZCoordinate();

            final World world = server.getWorld(worldUUID);
            final String worldName = world.getName();
            final Block worldBlock = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);
            final Material worldBlockType = worldBlock.getType();
            final Material databaseBlockType = getBlockType(blockIdentity);

            if (!isBlockOwned(blockIdentity))
            {
                m_logger.warning(
                        String.format(
                                "Database inconsistent for block type %s in %s at %d/%d/%d != protected",
                                worldName,
                                xCoordinate,
                                yCoordinate,
                                zCoordinate,
                                worldBlockType,
                                databaseBlockType));

                deleteBlockType(blockIdentity);
            }
        }
    }

    @Override
    public void createBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        m_blockOwnership.put(blockIdentity, playerIdentity);
    }

    @Override
    public void deleteBlockOwner(final BlockIdentity blockIdentity)
    {
        m_blockOwnership.remove(blockIdentity);
    }

    @Override
    public Set<BlockIdentity> getOwnedBlocks()
    {
        return new HashSet<>(m_blockOwnership.keySet());
    }

    @Override
    public PlayerIdentity getBlockOwner(final BlockIdentity blockIdentity)
    {
        return m_blockOwnership.get(blockIdentity);
    }

    @Override
    public boolean isBlockOwned(final BlockIdentity blockIdentity)
    {
        return m_blockOwnership.containsKey(blockIdentity);
    }

    @Override
    public boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        return Objects.equals(playerIdentity, m_blockOwnership.get(blockIdentity));
    }

    @Override
    public Map<BlockIdentity, Material> getBlockTypes()
    {
        return new HashMap<>(m_blockType);
    }

    @Override
    public Material getBlockType(final BlockIdentity blockIdentity)
    {
        return m_blockType.get(blockIdentity);
    }

    @Override
    public void setBlockType(final BlockIdentity blockIdentity, final Material material)
    {
        m_blockType.put(blockIdentity, material);
    }

    @Override
    public void deleteBlockType(final BlockIdentity blockIdentity)
    {
        m_blockType.remove(blockIdentity);
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

    private final static String BLOCK_OWNERSHIP_DATABASE_FILENAME = "block_ownership.db";
    private final DB m_database;
    private final Map<BlockIdentity, PlayerIdentity> m_blockOwnership;
    private final Map<BlockIdentity, Material> m_blockType;
    private final PluginLogger m_logger;
}
