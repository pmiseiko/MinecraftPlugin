package ca.nightfury.minecraft.plugin.block.protection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.PluginLogger;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentity;

public class ProtectionManagerImpl implements ProtectionManager
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public ProtectionManagerImpl(final ProtectionDatabase database, final PluginLogger logger)
    {
        m_database = database;
        m_logger = logger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ProtectionManager Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void integrityCheck(final Server server)
    {
        final Set<BlockIdentity> ownedBlocks = m_database.getOwnedBlocks();
        final Set<BlockIdentity> blocksChecked = new HashSet<>();

        for (final BlockIdentity blockIdentity : ownedBlocks)
        {
            if (blocksChecked.contains(blockIdentity))
            {
                continue;
            }

            final PlayerIdentity playerIdentity = m_database.getBlockOwner(blockIdentity);

            final UUID worldUUID = blockIdentity.getWorldUUID();
            final int xCoordinate = blockIdentity.getXCoordinate();
            final int yCoordinate = blockIdentity.getYCoordinate();
            final int zCoordinate = blockIdentity.getZCoordinate();

            final World world = server.getWorld(worldUUID);
            final Block block = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);

            final Set<Block> protectableBlocks = getAttachedProtectableBlocks(block, Integer.MAX_VALUE);
            for (final Block protectableBlock : protectableBlocks)
            {
                final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);
                blocksChecked.add(protectableBlockIdentity);
            }

            if (protectableBlocks.size() < BLOCK_COUNT_BEFORE_ACTIVATION)
            {
                for (final Block protectableBlock : protectableBlocks)
                {
                    final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);
                    if (m_database.isBlockOwned(protectableBlockIdentity))
                    {
                        m_logger.info(
                                String.format(
                                        "Protection removed from block %s in %s at %d/%d/%d",
                                        protectableBlock.getType(),
                                        protectableBlock.getWorld().getName(),
                                        protectableBlock.getX(),
                                        protectableBlock.getY(),
                                        protectableBlock.getZ()));

                        m_database.deleteBlockOwner(protectableBlockIdentity);
                        m_database.deleteBlockType(protectableBlockIdentity);
                    }
                }
            }
            else
            {
                for (final Block protectableBlock : protectableBlocks)
                {
                    final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);
                    if (m_database.isBlockOwned(protectableBlockIdentity))
                    {
                        if (!m_database.isBlockOwner(protectableBlockIdentity, playerIdentity))
                        {
                            m_logger.info(
                                    String.format(
                                            "Ownership collision with block %s in %s at %d/%d/%d",
                                            protectableBlock.getType(),
                                            protectableBlock.getWorld().getName(),
                                            protectableBlock.getX(),
                                            protectableBlock.getY(),
                                            protectableBlock.getZ()));
                        }
                    }
                    else
                    {
                        final Material protectableBlockType = protectableBlock.getType();

                        m_database.createBlockOwner(protectableBlockIdentity, playerIdentity);
                        m_database.setBlockType(protectableBlockIdentity, protectableBlockType);

                        m_logger.info(
                                String.format(
                                        "Protection added for %s at block %s in %s at %d/%d/%d",
                                        playerIdentity.getUUID(),
                                        protectableBlock.getType(),
                                        protectableBlock.getWorld().getName(),
                                        protectableBlock.getX(),
                                        protectableBlock.getY(),
                                        protectableBlock.getZ()));
                    }
                }
            }
        }
    }

    @Override
    public int getMinimumAttachedProtectableBlocksBeforeProtectionActivation()
    {
        return BLOCK_COUNT_BEFORE_ACTIVATION;
    }

    @Override
    public Set<Block> getAttachedProtectableBlocks(final Block originBlock, final int limit)
    {
        final Material originBlockType = originBlock.getType();
        if (ProtectedMaterials.isProtectedMaterial(originBlockType))
        {
            final Set<Block> protectableBlocks = new HashSet<>();
            final HashSet<Block> blocksChecked = new HashSet<>();
            final Queue<Block> blocks = new LinkedList<>();

            blocks.add(originBlock);
            protectableBlocks.add(originBlock);

            while (!blocks.isEmpty() && (protectableBlocks.size() < limit))
            {
                final Block block = blocks.remove();
                if (blocksChecked.contains(block))
                {
                    continue;
                }
                else
                {
                    blocksChecked.add(block);
                }

                final List<Block> neighbourBlocks = getAttachedProtectableBlocks(block);
                for (final Block neighbourBlock : neighbourBlocks)
                {
                    if (!blocksChecked.contains(neighbourBlock))
                    {
                        blocks.add(neighbourBlock);
                        protectableBlocks.add(neighbourBlock);
                    }
                }
            }

            return protectableBlocks;
        }
        else
        {
            return Collections.emptySet();
        }
    }

    @Override
    public List<Block> getProtectedBlocks(final Block originBlock, final int distance)
    {
        final int length = (distance * 2) + 1;
        final int area = length * length;
        final int volume = area * length;
        final List<Block> ownedBlocks = new ArrayList<>(volume);

        for (int xOffset = -distance; xOffset < (distance + 1); xOffset++)
        {
            for (int yOffset = -distance; yOffset < (distance + 1); yOffset++)
            {
                for (int zOffset = -distance; zOffset < (distance + 1); zOffset++)
                {
                    final Block block = originBlock.getRelative(xOffset, yOffset, zOffset);
                    final BlockIdentity blockIdentity = new BlockIdentity(block);

                    if (m_database.isBlockOwned(blockIdentity))
                    {
                        ownedBlocks.add(block);
                    }
                }
            }
        }

        return Collections.unmodifiableList(ownedBlocks);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ProtectionDatabase Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void createBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        m_database.createBlockOwner(blockIdentity, playerIdentity);
    }

    @Override
    public void deleteBlockOwner(final BlockIdentity blockIdentity)
    {
        m_database.deleteBlockOwner(blockIdentity);
    }

    @Override
    public Set<BlockIdentity> getOwnedBlocks()
    {
        return m_database.getOwnedBlocks();
    }

    @Override
    public PlayerIdentity getBlockOwner(final BlockIdentity blockIdentity)
    {
        return m_database.getBlockOwner(blockIdentity);
    }

    @Override
    public boolean isBlockOwned(final BlockIdentity blockIdentity)
    {
        return m_database.isBlockOwned(blockIdentity);
    }

    @Override
    public boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        return m_database.isBlockOwner(blockIdentity, playerIdentity);
    }

    @Override
    public Map<BlockIdentity, Material> getBlockTypes()
    {
        return m_database.getBlockTypes();
    }

    @Override
    public Material getBlockType(final BlockIdentity blockIdentity)
    {
        return m_database.getBlockType(blockIdentity);
    }

    @Override
    public void setBlockType(final BlockIdentity blockIdentity, final Material material)
    {
        m_database.setBlockType(blockIdentity, material);
    }

    @Override
    public void deleteBlockType(final BlockIdentity blockIdentity)
    {
        m_database.deleteBlockType(blockIdentity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flushable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void flush() throws IOException
    {
        m_database.flush();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Closeable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void close() throws IOException
    {
        m_database.close();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private List<Block> getAttachedProtectableBlocks(final Block originBlock)
    {
        final List<Block> protectableBlocks = new ArrayList<>(ATTACHED_NEIGHBOURS.size());
        for (final BlockFace blockFace : ATTACHED_NEIGHBOURS)
        {
            final Block block = originBlock.getRelative(blockFace);
            final Material blockType = block.getType();

            if (ProtectedMaterials.isProtectedMaterial(blockType))
            {
                protectableBlocks.add(block);
            }
        }

        return Collections.unmodifiableList(protectableBlocks);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static List<BlockFace> ATTACHED_NEIGHBOURS = Collections.unmodifiableList(
            Arrays.asList(
                    BlockFace.NORTH,
                    BlockFace.EAST,
                    BlockFace.SOUTH,
                    BlockFace.WEST,
                    BlockFace.UP,
                    BlockFace.DOWN));
    private final static int BLOCK_COUNT_BEFORE_ACTIVATION = 128;
    private final ProtectionDatabase m_database;
    private final PluginLogger m_logger;
}
