package ca.nightfury.minecraft.plugin.block.protection;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockIdentity
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public BlockIdentity(final Block block)
    {
        m_worldUUID = block.getWorld().getUID();
        m_xCoordinate = block.getX();
        m_yCoordinate = block.getY();
        m_zCoordinate = block.getZ();
    }

    public BlockIdentity(final Location location)
    {
        m_worldUUID = location.getWorld().getUID();
        m_xCoordinate = location.getBlockX();
        m_yCoordinate = location.getBlockY();
        m_zCoordinate = location.getBlockZ();
    }

    public BlockIdentity(final UUID worldUUID, final int xCoordinate, final int yCoordinate, final int zCoordinate)
    {
        m_worldUUID = worldUUID;
        m_xCoordinate = xCoordinate;
        m_yCoordinate = yCoordinate;
        m_zCoordinate = zCoordinate;
    }

    public UUID getWorldUUID()
    {
        return m_worldUUID;
    }

    public int getXCoordinate()
    {
        return m_xCoordinate;
    }

    public int getYCoordinate()
    {
        return m_yCoordinate;
    }

    public int getZCoordinate()
    {
        return m_zCoordinate;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode()
    {
        return Objects.hash(
                m_worldUUID.getLeastSignificantBits(),
                m_worldUUID.getMostSignificantBits(),
                m_xCoordinate,
                m_yCoordinate,
                m_zCoordinate);
    }

    @Override
    public boolean equals(final Object anObject)
    {
        if (anObject instanceof BlockIdentity)
        {
            final BlockIdentity peer = (BlockIdentity) anObject;
            return Objects.equals(m_worldUUID, peer.m_worldUUID) &&
                    Objects.equals(m_xCoordinate, peer.m_xCoordinate) &&
                    Objects.equals(m_yCoordinate, peer.m_yCoordinate) &&
                    Objects.equals(m_zCoordinate, peer.m_zCoordinate);
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final UUID m_worldUUID;
    private final int m_xCoordinate;
    private final int m_yCoordinate;
    private final int m_zCoordinate;
}
