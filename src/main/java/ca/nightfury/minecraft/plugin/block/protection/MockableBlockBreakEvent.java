package ca.nightfury.minecraft.plugin.block.protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class MockableBlockBreakEvent
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public MockableBlockBreakEvent(final BlockBreakEvent event)
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

    private final BlockBreakEvent m_event;
}
