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

        PrettyMessages.sendMessage(player, "1) Blocks outside of surface world no longer protected.");
        PrettyMessages.sendMessage(player, "2) Signs with \"community\" or \"public\" now permit other players");
        PrettyMessages.sendMessage(player, "2) permission to interact with near by protected objects.");
        PrettyMessages.sendMessage(player, "3) Fixed a bug that caused main hand items to vanish.");
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
