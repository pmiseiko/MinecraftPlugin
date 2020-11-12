package ca.nightfury.minecraft.plugin.block.protection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.PluginLogger;

import ca.nightfury.minecraft.plugin.services.PrettyMessages;

public class ProtectionEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public ProtectionEventListener(final BlockManager blockManager, final PluginLogger logger)
    {
        m_blockManager = Objects.requireNonNull(blockManager);
        m_logger = Objects.requireNonNull(logger);
    }

    public void integrityCheck(final Server server)
    {
        final Map<BlockIdentity, PlayerIdentity> blockOwners = m_blockManager.getBlockOwners();
        final Set<BlockIdentity> blocksChecked = new HashSet<>();

        for (final Entry<BlockIdentity, PlayerIdentity> entry : blockOwners.entrySet())
        {
            final BlockIdentity blockIdentity = entry.getKey();
            if (blocksChecked.contains(blockIdentity))
            {
                continue;
            }

            final PlayerIdentity playerIdentity = entry.getValue();

            final UUID worldUUID = blockIdentity.getWorldUUID();
            final int xCoordinate = blockIdentity.getXCoordinate();
            final int yCoordinate = blockIdentity.getYCoordinate();
            final int zCoordinate = blockIdentity.getZCoordinate();

            final World world = server.getWorld(worldUUID);
            final Block block = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);

            final Set<Block> protectableBlocks = getAttachedProtectableBlocks(block, Integer.MAX_VALUE);
            if (protectableBlocks.size() < BLOCK_COUNT_BEFORE_ACTIVATION)
            {
                for (final Block protectableBlock : protectableBlocks)
                {
                    m_logger.info(
                            String.format(
                                    "Protection removed from block %s in %s at %d/%d/%d",
                                    protectableBlock.getType(),
                                    protectableBlock.getWorld().getName(),
                                    protectableBlock.getX(),
                                    protectableBlock.getY(),
                                    protectableBlock.getZ()));

                    m_blockManager.unregisterBlockOwner(protectableBlock);

                    final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);

                    blocksChecked.add(protectableBlockIdentity);
                }
            }
            else
            {
                for (final Block protectableBlock : protectableBlocks)
                {
                    if (m_blockManager.isBlockOwned(protectableBlock))
                    {
                        if (!m_blockManager.isBlockOwnedByPlayer(protectableBlock, playerIdentity))
                        {
                            m_logger.info(
                                    String.format(
                                            "Ownership collision with attached block %s in %s at %d/%d/%d",
                                            protectableBlock.getType(),
                                            protectableBlock.getWorld().getName(),
                                            protectableBlock.getX(),
                                            protectableBlock.getY(),
                                            protectableBlock.getZ()));
                        }
                    }
                    else
                    {
                        m_blockManager.registerBlockOwner(protectableBlock, playerIdentity);
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

                    final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);

                    blocksChecked.add(protectableBlockIdentity);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // BlockEvent Listener(s).
    ///////////////////////////////////////////////////////////////////////////
    // No direct Player associated with the event.
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplodeEvent(final BlockExplodeEvent event)
    {
        for (final Block block : event.blockList())
        {
            if (m_blockManager.isBlockOwned(block))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockFromToEvent(final BlockFromToEvent event)
    {
        final Block block = event.getToBlock();
        if (m_blockManager.isBlockOwned(block))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPistonRetractEvent(final BlockPistonRetractEvent event)
    {
        for (final Block block : event.getBlocks())
        {
            if (m_blockManager.isBlockOwned(block))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPistonExtendEvent(final BlockPistonExtendEvent event)
    {
        for (final Block block : event.getBlocks())
        {
            if (m_blockManager.isBlockOwned(block))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // BlockEvent Listener(s).
    ///////////////////////////////////////////////////////////////////////////
    // Player associated with the event.
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();

        // Blocks cannot be broken unless they are not owned or owned by the player.
        if (m_blockManager.isBlockOwned(block))
        {
            if (m_blockManager.isBlockOwnedByPlayer(block, player))
            {
                m_blockManager.unregisterBlockOwner(block, player);
            }
            else
            {
                PrettyMessages.sendMessage(player, "You do not have permission to break that object.");
                event.setCancelled(true);
                return;
            }
        }

        // Example:
        // <-----door----->
        // <-----door----->
        //
        // Note: Only a bisected door block contains a second half.
        //
        final BlockData blockData = block.getBlockData();
        if (blockData instanceof Door)
        {
            final Door bisectedBlockData = (Door) blockData;
            final Half whichBlock = bisectedBlockData.getHalf();
            final Block otherBlock;

            switch (whichBlock)
            {
                case BOTTOM:
                    otherBlock = block.getRelative(BlockFace.UP);
                    break;
                case TOP:
                    otherBlock = block.getRelative(BlockFace.DOWN);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            m_blockManager.unregisterBlockOwner(otherBlock, player);
            return;
        }

        // Example:
        // <--north-west--> <----switch----> <--north-east-->
        // <----switch----> <-----dirt-----> <----switch---->
        // <--south-west--> <----switch----> <--south-east-->
        //
        for (final BlockFace currentBlockDirection : ATTACHED_NEIGHBOURS)
        {
            final Block attachedBlock = block.getRelative(currentBlockDirection);
            final BlockData attachedBlockData = attachedBlock.getBlockData();

            // Note: Other objects could break when their parent block breaks
            // such as a torch, but the torch does not have a BlockData class.
            if (attachedBlockData instanceof Switch)
            {
                // Example:
                // <----switch----> <-----down----->
                // <-----dirt-----> <-----self----->
                // <----switch----> <------up------>
                //
                final AttachedFace attachedFace = ((FaceAttachable) attachedBlockData).getAttachedFace();
                final BlockFace attachedBlockDirection;

                switch (attachedFace)
                {
                    case CEILING:
                        attachedBlockDirection = BlockFace.DOWN;
                        break;
                    case FLOOR:
                        attachedBlockDirection = BlockFace.UP;
                        break;
                    case WALL:
                        attachedBlockDirection = ((Directional) attachedBlockData).getFacing();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

                if (Objects.equals(currentBlockDirection, attachedBlockDirection))
                {
                    m_blockManager.unregisterBlockOwner(attachedBlock, player);
                }
            }
            else if (Objects.equals(currentBlockDirection, BlockFace.UP))
            {
                // Example:
                // <pressure plate>
                // <-----dirt----->
                //
                if (attachedBlockData instanceof Powerable)
                {
                    m_blockManager.unregisterBlockOwner(attachedBlock, player);

                    // Example:
                    // <-----door----->
                    // <-----door----->
                    // <-----dirt----->
                    //
                    // Note: Only a bisected door block contains a second half.
                    //
                    if (attachedBlockData instanceof Door)
                    {
                        final Block topBlock = attachedBlock.getRelative(BlockFace.UP);

                        m_blockManager.unregisterBlockOwner(topBlock, player);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(final BlockIgniteEvent event)
    {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();

        if (m_blockManager.isBlockOwned(block))
        {
            if ((player == null) || !m_blockManager.isBlockOwnedByPlayer(block, player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockMultiPlaceEvent(final BlockMultiPlaceEvent event)
    {
        final List<BlockState> blockStates = event.getReplacedBlockStates();
        for (final BlockState blockState : blockStates)
        {
            final Block block = blockState.getBlock();
            final BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(
                    block,
                    blockState,
                    event.getBlockAgainst(),
                    event.getItemInHand(),
                    event.getPlayer(),
                    event.canBuild(),
                    event.getHand());

            onBlockPlaceEvent(blockPlaceEvent);

            if (blockPlaceEvent.isCancelled())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(final BlockPlaceEvent event)
    {
        final Block block = event.getBlockPlaced();
        final Material blockType = block.getType();

        if (!ProtectedMaterials.isProtectedMaterial(blockType))
        {
            return;
        }

        // Protectable blocks must not be placed beside protected blocks owned by other
        // players.
        final Player player = event.getPlayer();
        final List<Block> ownedBlocks = getProtectedBlocks(block, 1);

        for (final Block ownedBlock : ownedBlocks)
        {
            if (!m_blockManager.isBlockOwnedByPlayer(ownedBlock, player))
            {
                PrettyMessages.sendMessage(
                        player,
                        String.format(
                                "You do not have permission to place %s in %s at %d/%d/%d.",
                                ownedBlock.getType(),
                                ownedBlock.getWorld().getName(),
                                ownedBlock.getX(),
                                ownedBlock.getY(),
                                ownedBlock.getZ()));
                event.setCancelled(true);
                return;
            }
        }

        final Set<Block> protectableBlocks = getAttachedProtectableBlocks(block, BLOCK_COUNT_BEFORE_ACTIVATION * 2);
        if (protectableBlocks.size() >= BLOCK_COUNT_BEFORE_ACTIVATION)
        {
            int registeredBlocks = 0;
            for (final Block protectableBlock : protectableBlocks)
            {
                if (m_blockManager.isBlockOwned(protectableBlock))
                {
                    if (!m_blockManager.isBlockOwnedByPlayer(protectableBlock, player))
                    {
                        m_blockManager.unregisterBlockOwner(protectableBlock, player);
                        m_blockManager.registerBlockOwner(protectableBlock, player);
                        registeredBlocks++;
                    }
                }
                else
                {
                    m_blockManager.registerBlockOwner(protectableBlock, player);
                    registeredBlocks++;
                }
            }

            if (registeredBlocks > 0)
            {
                PrettyMessages.sendMessage(player, String.format("Protected %d block(s).", registeredBlocks));
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // EntityEvent Listener(s).
    ///////////////////////////////////////////////////////////////////////////
    // No direct Player associated with the event.
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityBreakDoorEvent(final EntityBreakDoorEvent event)
    {
        final Block block = event.getBlock();
        if (m_blockManager.isBlockOwned(block))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(final EntityChangeBlockEvent event)
    {
        final Block block = event.getBlock();
        if (m_blockManager.isBlockOwned(block))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityEnterBlockEvent(final EntityEnterBlockEvent event)
    {
        final Block block = event.getBlock();
        if (m_blockManager.isBlockOwned(block))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(final EntityExplodeEvent event)
    {
        for (final Block block : event.blockList())
        {
            if (m_blockManager.isBlockOwned(block))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PlayerEvent Listener(s).
    ///////////////////////////////////////////////////////////////////////////
    // Player associated with the event.
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(final PlayerInteractEvent event)
    {
        final Action action = event.getAction();
        switch (action)
        {
            case LEFT_CLICK_BLOCK:
            {
                final Player player = event.getPlayer();
                final Material mainHandItemType = player.getInventory().getItemInMainHand().getType();

                if (Objects.equals(mainHandItemType, MAIN_HAND_BLOCK_DEBUG_MATERIAL))
                {
                    final Block clickedBlock = event.getClickedBlock();
                    final Material clickedBlockType = clickedBlock.getType();

                    if (!ProtectedMaterials.isProtectedMaterial(clickedBlockType))
                    {
                        PrettyMessages.sendMessage(player, "That block is not protectable.");
                        break;
                    }

                    if (!m_blockManager.isBlockOwned(clickedBlock))
                    {
                        PrettyMessages.sendMessage(player, "That block is not protected.");
                        break;
                    }

                    if (!m_blockManager.isBlockOwnedByPlayer(clickedBlock, player))
                    {
                        PrettyMessages.sendMessage(player, "That block is protected.");
                        break;
                    }

                    PrettyMessages.sendMessage(player, "That block is protected for you.");
                    break;
                }

                break;
            }

            case RIGHT_CLICK_BLOCK:
            {
                final Block clickedBlock = event.getClickedBlock();
                final Material clickedBlockType = clickedBlock.getType();

                if (!ProtectedMaterials.isProtectedInteractiveMaterial(clickedBlockType))
                {
                    break;
                }

                if (!m_blockManager.isBlockOwned(clickedBlock))
                {
                    break;
                }

                final Player player = event.getPlayer();
                if (!m_blockManager.isBlockOwnedByPlayer(clickedBlock, player))
                {
                    PrettyMessages.sendMessage(player, "You do not have permission to interact with that object.");
                    event.setCancelled(true);
                    break;
                }

                break;
            }

            default:
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // WorldEvent Listener(s).
    ///////////////////////////////////////////////////////////////////////////
    // Player associated with the event.
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onStructureGrowEvent(final StructureGrowEvent event)
    {
        final Player player = event.getPlayer();
        final List<BlockState> blockStates = event.getBlocks();

        for (final BlockState blockState : blockStates)
        {
            final Block block = blockState.getBlock();
            if (m_blockManager.isBlockOwned(block))
            {
                if ((player == null) || !m_blockManager.isBlockOwnedByPlayer(block, player))
                {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    protected Set<Block> getAttachedProtectableBlocks(final Block originBlock, final int limit)
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

    protected List<Block> getAttachedProtectableBlocks(final Block originBlock)
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

    protected List<Block> getProtectedBlocks(final Block originBlock, final int distance)
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
                    if (m_blockManager.isBlockOwned(block))
                    {
                        ownedBlocks.add(block);
                    }
                }
            }
        }

        return Collections.unmodifiableList(ownedBlocks);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static int BLOCK_COUNT_BEFORE_ACTIVATION = 128;
    private final static Material MAIN_HAND_BLOCK_DEBUG_MATERIAL = Material.STICK;
    private final static List<BlockFace> ATTACHED_NEIGHBOURS = Arrays.asList(
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN);
    private final BlockManager m_blockManager;
    private final PluginLogger m_logger;
}