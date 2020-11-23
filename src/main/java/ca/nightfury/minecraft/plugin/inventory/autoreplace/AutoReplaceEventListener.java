package ca.nightfury.minecraft.plugin.inventory.autoreplace;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
    public void onPlayerItemBreakEvent(final PlayerItemBreakEvent event)
    {
        final ItemStack brokenItem = event.getBrokenItem();
        final Player player = event.getPlayer();
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack mainHandItem = playerInventory.getItemInMainHand();

        if (Objects.equals(brokenItem, mainHandItem))
        {
            final String playerName = player.getDisplayName();
            final int playerHeldItemSlot = playerInventory.getHeldItemSlot();
            final Material mainHandItemType = mainHandItem.getType();

            m_logger.info(String.format("%s broke their main hand item %s", playerName, mainHandItemType));

            for (int inventoryItemIndex = 0; inventoryItemIndex < playerInventory.getSize(); inventoryItemIndex++)
            {
                if (inventoryItemIndex == playerHeldItemSlot)
                {
                    continue;
                }

                final ItemStack inventoryItem = playerInventory.getItem(inventoryItemIndex);
                if (inventoryItem == null)
                {
                    continue;
                }

                final Material inventoryItemType = inventoryItem.getType();
                if (Objects.equals(mainHandItemType, inventoryItemType))
                {
                    m_logger.info(
                            String.format(
                                    "Auto replaced %s with %s from inventory for %s",
                                    mainHandItemType,
                                    inventoryItemType,
                                    playerName));

                    playerInventory.setItemInMainHand(inventoryItem);
                    playerInventory.setItem(inventoryItemIndex, null);
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