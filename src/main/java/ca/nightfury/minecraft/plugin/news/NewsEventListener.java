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

        PrettyMessages.sendMessage(player, "Blocks outside of surface world no longer protected.");
        PrettyMessages.sendMessage(
                player,
                "Signs with \"community\" or \"public\" now permit other players");
        PrettyMessages.sendMessage(
                player,
                "permission to interact with near by protected objects.");
    }
}
