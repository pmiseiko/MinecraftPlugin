package ca.nightfury.minecraft.plugin.player.hearth;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import ca.nightfury.minecraft.plugin.database.PlayerIdentity;
import ca.nightfury.minecraft.plugin.services.PrettyMessages;

public class HearthCommandHandler implements CommandExecutor
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public HearthCommandHandler(final HearthDatabase database, final Server server, final PluginLogger logger)
    {
        m_database = database;
        m_server = server;
        m_logger = logger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CommandExecutor Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        final String commandName = command.getName();
        if (HEARTH_COMMAND_NAME.equalsIgnoreCase(commandName))
        {
            final String playerName = sender.getName();
            final Player player = m_server.getPlayer(playerName);
            final PlayerIdentity playerIdentity = new PlayerIdentity(player);

            if (m_database.isHearthDisabled(playerIdentity))
            {
                m_logger.info(String.format("%s tried to hearth but their hearth is on cooldown", playerName));

                PrettyMessages.sendMessage(player, "Your hearth is on cooldown.");
            }
            else
            {
                final Location bedSpawnLocation = player.getBedSpawnLocation();
                if (bedSpawnLocation != null)
                {
                    final Location currentLocation = player.getLocation();
                    final World currentWorld = currentLocation.getWorld();
                    final World bedSpawnWorld = bedSpawnLocation.getWorld();

                    if (player.teleport(bedSpawnLocation))
                    {
                        m_database.disableHearth(playerIdentity);
                        m_logger.info(
                                String.format(
                                        "%s hearthed from %s at %d/%d/%d to %s at %d/%d/%d",
                                        playerName,
                                        currentWorld.getName(),
                                        currentLocation.getBlockX(),
                                        currentLocation.getBlockY(),
                                        currentLocation.getBlockZ(),
                                        bedSpawnWorld.getName(),
                                        bedSpawnLocation.getBlockX(),
                                        bedSpawnLocation.getBlockY(),
                                        bedSpawnLocation.getBlockZ()));

                        PrettyMessages.sendMessage(player, "Your hearth is now on cooldown.");
                    }
                    else
                    {
                        m_logger.info(
                                String.format(
                                        "%s could not hearth from %s at %d/%d/%d to %s at %d/%d/%d",
                                        playerName,
                                        currentWorld.getName(),
                                        currentLocation.getBlockX(),
                                        currentLocation.getBlockY(),
                                        currentLocation.getBlockZ(),
                                        bedSpawnWorld.getName(),
                                        bedSpawnLocation.getBlockX(),
                                        bedSpawnLocation.getBlockY(),
                                        bedSpawnLocation.getBlockZ()));

                        PrettyMessages.sendMessage(player, "Something preventing you from using your hearth.");
                    }
                }
                else
                {
                    m_logger.info(String.format("%s tried to hearth without a bed spawn location", playerName));

                    PrettyMessages.sendMessage(player, "You must have a bed spawn location to hearth.");
                }
            }

            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static String HEARTH_COMMAND_NAME = "hearth";
    private final HearthDatabase m_database;
    private final Server m_server;
    private final PluginLogger m_logger;
}
