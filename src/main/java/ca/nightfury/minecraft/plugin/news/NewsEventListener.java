package ca.nightfury.minecraft.plugin.news;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ca.nightfury.minecraft.plugin.services.PrettyMessages;

public class NewsEventListener implements Listener
{
    ///////////////////////////////////////////////////////////////////////////
    // Listener Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        PrettyMessages.sendMessage(player, "1) Blocks outside the surface world are no longer protected.");
        PrettyMessages.sendMessage(
                player,
                "2) Signs with \"community\" or \"public\" share adjacent blocks with other players");
        PrettyMessages.sendMessage(player, "3) Planks is now a protectable block.");
        PrettyMessages.sendMessage(player, "4) \"/hearth\" command lets you teleport back to your bed once per day");
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
