package ca.nightfury.minecraft.plugin.block.tombstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TombstoneEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public TombstoneEventListener(final JavaPlugin plugin, final PluginLogger logger)
    {
        m_scheduler = Bukkit.getScheduler();
        m_plugin = plugin;
        m_logger = logger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(final PlayerDeathEvent event)
    {
        final Player player = event.getEntity();
        final String playerName = player.getDisplayName();
        final Location location = player.getLocation();

        m_logger.info(
                String.format(
                        "%s died in %s at %d/%d/%d",
                        playerName,
                        location.getWorld().getName(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ()));

        final Collection<ItemStack> droppedItemStacks = event.getDrops();
        if (droppedItemStacks.isEmpty())
        {
            m_logger.info(String.format("%s died with no items", playerName));
            return;
        }

        final Collection<ItemStack> lootableItemStacks = new ArrayList<>(droppedItemStacks);

        droppedItemStacks.clear();

        m_scheduler.scheduleSyncDelayedTask(m_plugin, () ->
        {
            final Block block = getLowestNonSolidBlock(location);
            if (block != null)
            {
                final Collection<ItemStack> excessDroppedItems =
                        createAndFillChest(playerName, block, lootableItemStacks);

                lootableItemStacks.clear();

                if (!excessDroppedItems.isEmpty())
                {
                    final Block excessBlock = block.getRelative(0, 1, 0);
                    final Collection<ItemStack> remainingItems =
                            createAndFillChest(playerName, excessBlock, excessDroppedItems);

                    lootableItemStacks.addAll(remainingItems);
                }
            }
            else
            {
                m_logger.info(String.format("Suitable block not found for %s's chest.", playerName));
            }

            for (final ItemStack lootableItemStack : lootableItemStacks)
            {
                location.getWorld().dropItemNaturally(location, lootableItemStack);
            }
        }, CREATE_AND_FILL_CHEST_TICK_DELAY);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    protected boolean isAirLavaOrWater(final Material material)
    {
        switch (material)
        {
            case AIR:
            case CAVE_AIR:
            case LAVA:
            case VOID_AIR:
            case WATER:
                return true;

            default:
                return false;
        }
    }

    protected Block getLowestNonSolidBlock(final Location location)
    {
        // Find the lowest non-air, non-water, non-lava block
        final World world = location.getWorld();
        final int maximumHeight = world.getMaxHeight();
        final int xCoordinate = location.getBlockX();
        final int zCoordinate = location.getBlockZ();

        for (int yCoordinate = Math.min(maximumHeight, location.getBlockY()); yCoordinate > 0; yCoordinate--)
        {
            final Block relativeBlock = world.getBlockAt(xCoordinate, yCoordinate, zCoordinate);
            final Material relativeBlockType = relativeBlock.getType();

            if (!isAirLavaOrWater(relativeBlockType))
            {
                return relativeBlock.getRelative(0, 1, 0);
            }
        }

        return null;
    }

    protected Collection<ItemStack> createAndFillChest(
            final String playerName,
            final Block block,
            final Collection<ItemStack> itemStacks)
    {
        final World world = block.getWorld();
        final int maximumHeight = world.getMaxHeight();
        final int yCoordinate = block.getY();
        final Material blockType = block.getType();

        if ((yCoordinate < maximumHeight) && isAirLavaOrWater(blockType))
        {
            block.setType(Material.CHEST);

            final BlockState blockState = block.getState();
            if (blockState instanceof Chest)
            {
                final Chest chest = (Chest) blockState;
                final Inventory chestInventory = chest.getBlockInventory();
                final ItemStack[] arrayOfItemStacks = itemStacks.toArray(new ItemStack[itemStacks.size()]);
                final Map<?, ItemStack> excessItemStacks = chestInventory.addItem(arrayOfItemStacks);

                m_logger.info(
                        String.format(
                                "Chest created for %s in %s at %d/%d/%d",
                                playerName,
                                world.getName(),
                                block.getX(),
                                block.getY(),
                                block.getZ()));

                return excessItemStacks.values();
            }
        }

        m_logger.info(
                String.format(
                        "Could not create chest for %s in %s at %d/%d/%d",
                        playerName,
                        world.getName(),
                        block.getX(),
                        block.getY(),
                        block.getZ()));

        return itemStacks;
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

    private final static int CREATE_AND_FILL_CHEST_TICK_DELAY = 10;
    private final BukkitScheduler m_scheduler;
    private final JavaPlugin m_plugin;
    private final PluginLogger m_logger;
}