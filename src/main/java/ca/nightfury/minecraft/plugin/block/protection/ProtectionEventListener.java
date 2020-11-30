package ca.nightfury.minecraft.plugin.block.protection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentity;
import ca.nightfury.minecraft.plugin.services.PrettyMessages;

public class ProtectionEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public ProtectionEventListener(final JavaPlugin plugin, final ProtectionManager manager, final PluginLogger logger)
    {
        m_scheduler = Bukkit.getScheduler();
        m_plugin = plugin;
        m_manager = Objects.requireNonNull(manager);
        m_logger = Objects.requireNonNull(logger);
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
            final BlockIdentity blockIdentity = new BlockIdentity(block);
            if (m_manager.isBlockOwned(blockIdentity))
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
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPistonRetractEvent(final BlockPistonRetractEvent event)
    {
        for (final Block block : event.getBlocks())
        {
            final BlockIdentity blockIdentity = new BlockIdentity(block);
            if (m_manager.isBlockOwned(blockIdentity))
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
            final BlockIdentity blockIdentity = new BlockIdentity(block);
            if (m_manager.isBlockOwned(blockIdentity))
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
        final BlockIdentity blockIdentity = new BlockIdentity(block);
        final Material blockType = block.getType();
        final Player player = event.getPlayer();
        final PlayerIdentity playerIdentity = new PlayerIdentity(player);
        final World world = block.getWorld();

        // Blocks cannot be broken unless they are not owned or owned by the player.
        if (m_manager.isBlockOwned(blockIdentity))
        {
            if (m_manager.isBlockOwner(blockIdentity, playerIdentity))
            {
                m_manager.deleteBlockOwner(blockIdentity);
                m_manager.deleteBlockType(blockIdentity);

                m_logger.info(
                        String.format(
                                "%s[%s] unregistered %s in %s at %d/%d/%d",
                                player.getName(),
                                player.getUniqueId(),
                                blockType,
                                world.getName(),
                                block.getX(),
                                block.getY(),
                                block.getZ()));
            }
            else
            {
                PrettyMessages.sendMessage(player, "You do not have permission to break that object.");
                event.setCancelled(true);
                return;
            }
        }

        m_scheduler.scheduleSyncDelayedTask(m_plugin, () ->
        {
            final List<Block> neighbourBlocks = new LinkedList<>();

            neighbourBlocks.add(block);
            neighbourBlocks.add(block.getRelative(BlockFace.NORTH));
            neighbourBlocks.add(block.getRelative(BlockFace.EAST));
            neighbourBlocks.add(block.getRelative(BlockFace.SOUTH));
            neighbourBlocks.add(block.getRelative(BlockFace.WEST));
            neighbourBlocks.add(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP));
            neighbourBlocks.add(block.getRelative(BlockFace.UP));
            neighbourBlocks.add(block.getRelative(BlockFace.DOWN));

            for (final Block neighbourBlock : neighbourBlocks)
            {
                final BlockIdentity neighbourBlockIdentity = new BlockIdentity(neighbourBlock);
                if (m_manager.isBlockOwned(neighbourBlockIdentity))
                {
                    final Material neighbourBlockActualType = neighbourBlock.getType();
                    final Material neighbourBlockExpectType = m_manager.getBlockType(neighbourBlockIdentity);

                    if (!Objects.equals(neighbourBlockActualType, neighbourBlockExpectType))
                    {
                        final World neighbourBlockWorld = block.getWorld();

                        m_manager.deleteBlockOwner(neighbourBlockIdentity);
                        m_manager.deleteBlockType(neighbourBlockIdentity);

                        m_logger.info(
                                String.format(
                                        "%s[%s] unregistered %s in %s at %d/%d/%d because of %s in %s at %d/%d/%d",
                                        player.getName(),
                                        player.getUniqueId(),
                                        neighbourBlockExpectType,
                                        neighbourBlockWorld.getName(),
                                        neighbourBlock.getX(),
                                        neighbourBlock.getY(),
                                        neighbourBlock.getZ(),
                                        blockType,
                                        world.getName(),
                                        block.getX(),
                                        block.getY(),
                                        block.getZ()));
                    }
                }
            }
        }, NEIGHBOUR_BLOCK_CHECK_DELAY);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(final BlockIgniteEvent event)
    {
        final Block block = event.getBlock();
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            final Player player = event.getPlayer();
            if (player == null)
            {
                event.setCancelled(true);
            }
            else
            {
                final PlayerIdentity playerIdentity = new PlayerIdentity(player);
                if (!m_manager.isBlockOwner(blockIdentity, playerIdentity))
                {
                    event.setCancelled(true);
                }
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
        final World world = block.getWorld();
        final Environment worldEnvironment = world.getEnvironment();

        if (!Objects.equals(worldEnvironment, Environment.NORMAL))
        {
            return;
        }

        final Material blockType = block.getType();
        if (!ProtectedMaterials.isProtectedMaterial(blockType))
        {
            return;
        }

        // Protectable blocks must not be placed beside protected blocks owned by other
        // players.
        final Player player = event.getPlayer();
        final PlayerIdentity playerIdentity = new PlayerIdentity(player);
        final List<Block> ownedBlocks = m_manager.getProtectedBlocks(block, 1);

        for (final Block ownedBlock : ownedBlocks)
        {
            final BlockIdentity ownedBlockIdentity = new BlockIdentity(ownedBlock);
            if (!m_manager.isBlockOwner(ownedBlockIdentity, playerIdentity))
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

        final int minimumAttachedProtectableBlocksBeforeProtectionActivation =
                m_manager.getMinimumAttachedProtectableBlocksBeforeProtectionActivation();
        final Set<Block> protectableBlocks = m_manager.getAttachedProtectableBlocks(
                block,
                minimumAttachedProtectableBlocksBeforeProtectionActivation);
        final Set<Block> newlyProtectedBlocks = new HashSet<>();

        if (protectableBlocks.size() >= minimumAttachedProtectableBlocksBeforeProtectionActivation)
        {
            for (final Block protectableBlock : protectableBlocks)
            {
                final BlockIdentity protectableBlockIdentity = new BlockIdentity(protectableBlock);
                final Material protectableBlockType = protectableBlock.getType();

                if (m_manager.isBlockOwned(protectableBlockIdentity))
                {
                    if (!m_manager.isBlockOwner(protectableBlockIdentity, playerIdentity))
                    {
                        m_manager.deleteBlockOwner(protectableBlockIdentity);
                        m_manager.deleteBlockType(protectableBlockIdentity);
                        m_manager.createBlockOwner(protectableBlockIdentity, playerIdentity);
                        m_manager.setBlockType(protectableBlockIdentity, protectableBlockType);

                        m_logger.info(
                                String.format(
                                        "%s[%s] overwrote registration of %s in %s at %d/%d/%d",
                                        player.getName(),
                                        player.getUniqueId(),
                                        block.getType(),
                                        world.getName(),
                                        block.getX(),
                                        block.getY(),
                                        block.getZ()));

                        newlyProtectedBlocks.add(protectableBlock);
                    }
                }
                else
                {
                    m_manager.createBlockOwner(protectableBlockIdentity, playerIdentity);
                    m_manager.setBlockType(protectableBlockIdentity, protectableBlockType);

                    m_logger.info(
                            String.format(
                                    "%s[%s] registered %s in %s at %d/%d/%d",
                                    player.getName(),
                                    player.getUniqueId(),
                                    block.getType(),
                                    world.getName(),
                                    block.getX(),
                                    block.getY(),
                                    block.getZ()));

                    newlyProtectedBlocks.add(protectableBlock);
                }
            }

            if (!newlyProtectedBlocks.isEmpty())
            {
                final UUID playerUUID = player.getUniqueId();
                if (m_playerPendingProtectedBlocks.containsKey(playerUUID))
                {
                    m_playerPendingProtectedBlocks.get(playerUUID).addAll(newlyProtectedBlocks);
                }
                else
                {
                    m_playerPendingProtectedBlocks.put(playerUUID, newlyProtectedBlocks);
                }

                if (m_playerPendingProtectedBlockMessage.containsKey(playerUUID))
                {
                    final int previousTaskID = m_playerPendingProtectedBlockMessage.get(playerUUID);
                    m_scheduler.cancelTask(previousTaskID);
                }

                final int taskID = m_scheduler.scheduleSyncDelayedTask(m_plugin, () ->
                {
                    final Set<Block> pendingProtectedBlocks = m_playerPendingProtectedBlocks.remove(playerUUID);
                    if ((pendingProtectedBlocks != null) && !pendingProtectedBlocks.isEmpty())
                    {
                        int count = 0;
                        for (final Block protectedBlock : pendingProtectedBlocks)
                        {
                            final BlockIdentity protectedBlockIdentity = new BlockIdentity(protectedBlock);
                            if (m_manager.isBlockOwner(protectedBlockIdentity, playerIdentity))
                            {
                                count++;
                            }
                        }

                        PrettyMessages.sendMessage(player, String.format("Protected %d block(s).", count));
                    }

                    m_playerPendingProtectedBlockMessage.remove(playerUUID);
                }, PLAYER_PROTECTED_BLOCK_MESSAGE_DELAY);

                m_playerPendingProtectedBlockMessage.put(playerUUID, taskID);
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
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(final EntityChangeBlockEvent event)
    {
        final Block block = event.getBlock();
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityEnterBlockEvent(final EntityEnterBlockEvent event)
    {
        final Block block = event.getBlock();
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(final EntityExplodeEvent event)
    {
        for (final Block block : event.blockList())
        {
            final BlockIdentity blockIdentity = new BlockIdentity(block);
            if (m_manager.isBlockOwned(blockIdentity))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityInteractEvent(final EntityInteractEvent event)
    {
        final Block block = event.getBlock();
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (m_manager.isBlockOwned(blockIdentity))
        {
            event.setCancelled(true);
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

                    final BlockIdentity clickedBlockIdentity = new BlockIdentity(clickedBlock);
                    if (!m_manager.isBlockOwned(clickedBlockIdentity))
                    {
                        PrettyMessages.sendMessage(player, "That block is not protected.");
                        break;
                    }

                    final PlayerIdentity playerIdentity = new PlayerIdentity(player);
                    if (!m_manager.isBlockOwner(clickedBlockIdentity, playerIdentity))
                    {
                        PrettyMessages.sendMessage(player, "That block is protected.");
                        break;
                    }

                    PrettyMessages.sendMessage(player, "That block is protected for you.");
                    break;
                }

                break;
            }

            case PHYSICAL:
            case RIGHT_CLICK_BLOCK:
            {
                final Block clickedBlock = event.getClickedBlock();
                final Material clickedBlockType = clickedBlock.getType();

                if (!ProtectedMaterials.isProtectedInteractiveMaterial(clickedBlockType))
                {
                    break;
                }

                final BlockIdentity clickedBlockIdentity = new BlockIdentity(clickedBlock);
                if (!m_manager.isBlockOwned(clickedBlockIdentity))
                {
                    break;
                }

                final Player player = event.getPlayer();
                final PlayerIdentity playerIdentity = new PlayerIdentity(player);

                if (!m_manager.isBlockOwner(clickedBlockIdentity, playerIdentity))
                {
                    final List<Block> protectedBlocks = m_manager.getProtectedBlocks(clickedBlock, 1);
                    for (final Block protectedBlock : protectedBlocks)
                    {
                        final BlockState protectedBlockData = protectedBlock.getState();
                        if (protectedBlockData instanceof Sign)
                        {
                            final Sign sign = (Sign) protectedBlockData;
                            final String header = sign.getLine(0).trim().toLowerCase();

                            if ("community".equals(header) || "public".equals(header))
                            {
                                final String playerName = player.getDisplayName();
                                final World world = clickedBlock.getWorld();
                                final String worldName = world.getName();

                                m_logger.info(
                                        String.format(
                                                "%s interacted with public protected block %s in %s at %d/%d/%d",
                                                playerName,
                                                clickedBlockType,
                                                worldName,
                                                clickedBlock.getX(),
                                                clickedBlock.getY(),
                                                clickedBlock.getZ()));

                                return;
                            }
                        }
                    }

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
        final List<BlockState> blockStates = event.getBlocks();
        final Player player = event.getPlayer();
        final PlayerIdentity playerIdentity = new PlayerIdentity(player);

        for (final BlockState blockState : blockStates)
        {
            final Block block = blockState.getBlock();
            final BlockIdentity blockIdentity = new BlockIdentity(block);

            if (m_manager.isBlockOwned(blockIdentity) &&
                    ((player == null) || !m_manager.isBlockOwner(blockIdentity, playerIdentity)))
            {
                event.setCancelled(true);
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
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static Material MAIN_HAND_BLOCK_DEBUG_MATERIAL = Material.STICK;
    private final static int PLAYER_PROTECTED_BLOCK_MESSAGE_DELAY = 100;
    private final static int NEIGHBOUR_BLOCK_CHECK_DELAY = 10;
    private final Map<UUID, Integer> m_playerPendingProtectedBlockMessage = new HashMap<>();
    private final Map<UUID, Set<Block>> m_playerPendingProtectedBlocks = new HashMap<>();
    private final BukkitScheduler m_scheduler;
    private final JavaPlugin m_plugin;
    private final ProtectionManager m_manager;
    private final PluginLogger m_logger;
}