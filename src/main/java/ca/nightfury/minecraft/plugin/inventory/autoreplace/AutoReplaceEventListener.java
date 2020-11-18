package ca.nightfury.minecraft.plugin.inventory.autoreplace;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginLogger;

public class AutoReplaceEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public AutoReplaceEventListener(final PluginLogger logger)
    {
        m_logger = logger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlaceEvent(final BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final ItemStack mainHandItem = event.getItemInHand();
        final int mainHandItemAmount = mainHandItem.getAmount();

        if (mainHandItemAmount == 1)
        {
            final String playerName = player.getDisplayName();
            final Material mainHandItemType = mainHandItem.getType();

            m_logger.info(String.format("%s exhausted their main hand item %s", playerName, mainHandItemType));

            for (int itemIndex = 0; itemIndex < inventory.getSize(); itemIndex++)
            {
                final ItemStack inventoryItem = inventory.getItem(itemIndex);
                if ((inventoryItem == null) || Objects.equals(mainHandItem, inventoryItem))
                {
                    continue;
                }

                final Material itemType = inventoryItem.getType();
                final int inventoryItemAmount = inventoryItem.getAmount();

                if (Objects.equals(mainHandItemType, itemType))
                {
                    m_logger.info(
                            String.format(
                                    "Auto replaced %s[%d] with %s[%d] from inventory for %s",
                                    mainHandItemType,
                                    mainHandItemAmount,
                                    itemType,
                                    inventoryItemAmount,
                                    playerName));

                    inventory.setItemInMainHand(inventoryItem);
                    inventory.setItem(itemIndex, null);
                    return;
                }
            }

            m_logger.info(
                    String.format(
                            "%s had no replacement for %s[%d]",
                            playerName,
                            mainHandItemType,
                            mainHandItemAmount));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemBreakEvent(final PlayerItemBreakEvent event)
    {
        final ItemStack brokenItem = event.getBrokenItem();
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final ItemStack mainHandItem = inventory.getItemInMainHand();

        if (Objects.equals(brokenItem, mainHandItem))
        {
            final String playerName = player.getDisplayName();
            final Material mainHandItemType = mainHandItem.getType();

            m_logger.info(String.format("%s broke their main hand item %s", playerName, mainHandItemType));

            for (int itemIndex = 0; itemIndex < inventory.getSize(); itemIndex++)
            {
                final ItemStack inventoryItem = inventory.getItem(itemIndex);
                if ((inventoryItem == null) || Objects.equals(brokenItem, inventoryItem))
                {
                    continue;
                }

                final Material itemType = inventoryItem.getType();
                if (Objects.equals(mainHandItemType, itemType))
                {
                    m_logger.info(
                            String.format(
                                    "Auto replaced %s with %s from inventory for %s",
                                    mainHandItemType,
                                    itemType,
                                    playerName));

                    inventory.setItemInMainHand(inventoryItem);
                    inventory.setItem(itemIndex, null);
                    return;
                }
            }

            m_logger.info(String.format("%s had no replacement for %s", playerName, mainHandItemType));
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

    private final PluginLogger m_logger;
}