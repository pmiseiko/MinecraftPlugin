package ca.nightfury.minecraft.plugin.block.tombstone;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;

public class TombstoneEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public TombstoneEventListener(final PluginLogger logger)
    {
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

        final int droppedExp = event.getDroppedExp();
        if (droppedExp > 0)
        {
            m_logger.info(String.format("%s dropped %d XP", playerName, droppedExp));
        }

        final Collection<ItemStack> droppedItemStacks = event.getDrops();
        for (final ItemStack itemStack : droppedItemStacks)
        {
            final Material itemStackType = itemStack.getType();
            final int itemStackAmount = itemStack.getAmount();
            final Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();

            if (enchantments.isEmpty())
            {
                m_logger.info(String.format("%s dropped %s[%d]", playerName, itemStackType, itemStackAmount));
            }
            else
            {
                final StringBuilder enchantmentsStringBuilder = new StringBuilder();
                for (final Enchantment enchantment : enchantments.keySet())
                {
                    final NamespacedKey namespacedKey = enchantment.getKey();
                    final String enchantmentName = namespacedKey.getKey();

                    enchantmentsStringBuilder.append(" [enchantment:");
                    enchantmentsStringBuilder.append(enchantmentName);
                    enchantmentsStringBuilder.append("]");
                }

                final String enchantmentsString = enchantmentsStringBuilder.toString();

                m_logger.info(
                        String.format(
                                "%s dropped %s[%d]%s",
                                playerName,
                                itemStackType,
                                itemStackAmount,
                                enchantmentsString));
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

    private final PluginLogger m_logger;
}