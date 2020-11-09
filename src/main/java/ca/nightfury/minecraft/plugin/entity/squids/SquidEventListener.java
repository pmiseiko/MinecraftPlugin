package ca.nightfury.minecraft.plugin.entity.squids;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SquidEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Listener Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(final CreatureSpawnEvent event)
    {
        final LivingEntity entity = event.getEntity();
        final EntityType entityType = entity.getType();

        switch (entityType)
        {
            case SQUID:
            {
                final CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

                if (reason.equals(CreatureSpawnEvent.SpawnReason.NATURAL))
                {
                    final Location entityLocation = entity.getLocation();
                    final Block entityBlock = entityLocation.getBlock();
                    final Biome biome = entityBlock.getBiome();

                    switch (biome)
                    {
                        case OCEAN:
                        case COLD_OCEAN:
                        case DEEP_COLD_OCEAN:
                        case DEEP_FROZEN_OCEAN:
                        case DEEP_LUKEWARM_OCEAN:
                        case DEEP_OCEAN:
                        case DEEP_WARM_OCEAN:
                        case FROZEN_OCEAN:
                        case LUKEWARM_OCEAN:
                        case WARM_OCEAN:
                            break;

                        default:
                            event.setCancelled(true);
                            return;
                    }

                    for (int relativeXCoordinate = -2; relativeXCoordinate < 3; relativeXCoordinate++)
                    {
                        for (int relativeYCoordinate = -2; relativeYCoordinate < 3; relativeYCoordinate++)
                        {
                            for (int relativeZCoordinate = -2; relativeZCoordinate < 3; relativeZCoordinate++)
                            {
                                final Block relativeBlock = entityBlock.getRelative(
                                        relativeXCoordinate,
                                        relativeYCoordinate,
                                        relativeZCoordinate);
                                final Material relativeBlockType = relativeBlock.getType();

                                switch (relativeBlockType)
                                {
                                    case WATER:
                                        break;

                                    default:
                                        event.setCancelled(true);
                                        return;
                                }
                            }
                        }
                    }
                }
            }

            default:
                break;
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
}