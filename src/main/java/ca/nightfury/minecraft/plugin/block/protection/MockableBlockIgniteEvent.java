package ca.nightfury.minecraft.plugin.block.protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockIgniteEvent;

public class MockableBlockIgniteEvent
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public MockableBlockIgniteEvent(final BlockIgniteEvent event)
    {
        m_event = event;
    }

    public Block getBlock()
    {
        return m_event.getBlock();
    }

    public Player getPlayer()
    {
        return m_event.getPlayer();
    }

    public void setCancelled(final boolean cancel)
    {
        m_event.setCancelled(cancel);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final BlockIgniteEvent m_event;
}
