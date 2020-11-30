package ca.nightfury.minecraft.plugin.block.rewards;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginLogger;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;

public class RewardEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public RewardEventListener(final RewardDatabase database, final PluginLogger logger)
    {
        m_database = database;
        m_logger = logger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        final BlockIdentity blockIdentity = new BlockIdentity(block);

        if (!m_database.rewardBlock(blockIdentity))
        {
            final Location location = block.getLocation();
            final World world = block.getWorld();
            final Biome biome = block.getBiome();
            final Player player = event.getPlayer();
            final PlayerInventory playerInventory = player.getInventory();
            final ItemStack playerItemInMainHand = playerInventory.getItemInMainHand();
            final Material rewardingMaterial = block.getType();
            final Material breakingTool = playerItemInMainHand.getType();
            final List<RewardMaterials> rewardMaterials =
                    RewardMaterials.getRewardMaterials(world, biome, location, rewardingMaterial, breakingTool);

            for (final RewardMaterials rewardMaterial : rewardMaterials)
            {
                if (rewardMaterial.discovered())
                {
                    final ItemStack item = rewardMaterial.getItemStack();

                    world.dropItem(location, item);

                    final Material itemType = item.getType();
                    final int itemAmount = item.getAmount();
                    final String playerName = player.getName();
                    final UUID playerUUID = player.getUniqueId();
                    final String playerID = playerUUID.toString();

                    m_logger.log(
                            Level.INFO,
                            String.format(
                                    "%s[%s] was rewarded with %s %d",
                                    playerName,
                                    playerID,
                                    itemType,
                                    itemAmount));
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
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final RewardDatabase m_database;
    private final PluginLogger m_logger;
}