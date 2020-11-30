package ca.nightfury.minecraft.plugin.database;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerIdentity
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public PlayerIdentity(final Player player)
    {
        m_uuid = player.getUniqueId();
    }

    public PlayerIdentity(final UUID playerIdentifier)
    {
        m_uuid = playerIdentifier;
    }

    public UUID getUUID()
    {
        return m_uuid;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Object Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode()
    {
        return Objects.hash(m_uuid.getLeastSignificantBits(), m_uuid.getMostSignificantBits());
    }

    @Override
    public boolean equals(final Object anObject)
    {
        if (anObject instanceof PlayerIdentity)
        {
            final PlayerIdentity peer = (PlayerIdentity) anObject;
            return Objects.equals(m_uuid, peer.m_uuid);
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final UUID m_uuid;
}
