package ca.nightfury.minecraft.plugin.block.protection;

import java.io.IOException;
import java.util.UUID;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class PlayerIdentitySerializer implements Serializer<PlayerIdentity>
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    public final static PlayerIdentitySerializer SINGLETON = new PlayerIdentitySerializer();

    ///////////////////////////////////////////////////////////////////////////
    // Serializer Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void serialize(DataOutput2 out, PlayerIdentity value) throws IOException
    {
        final UUID playerUUID = value.getUUID();

        Serializer.UUID.serialize(out, playerUUID);
    }

    @Override
    public PlayerIdentity deserialize(DataInput2 input, int available) throws IOException
    {
        final UUID playerUUID = Serializer.UUID.deserialize(input, available);

        return new PlayerIdentity(playerUUID);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private PlayerIdentitySerializer()
    {
    }
}
