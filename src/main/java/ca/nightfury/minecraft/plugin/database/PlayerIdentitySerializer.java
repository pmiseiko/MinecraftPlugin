package ca.nightfury.minecraft.plugin.database;

import java.io.IOException;
import java.io.InvalidObjectException;
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
    public void serialize(final DataOutput2 out, final PlayerIdentity value) throws IOException
    {
        final UUID playerUUID = value.getUUID();

        Serializer.INTEGER.serialize(out, OBJECT_VERSION);
        Serializer.UUID.serialize(out, playerUUID);
    }

    @Override
    public PlayerIdentity deserialize(final DataInput2 input, final int available) throws IOException
    {
        final int objectVersion = Serializer.INTEGER.deserialize(input, available);
        switch (objectVersion)
        {
            case 0:
                final UUID playerUUID = Serializer.UUID.deserialize(input, available);
                return new PlayerIdentity(playerUUID);

            default:
                throw new InvalidObjectException("Unsupported object version: " + objectVersion);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private PlayerIdentitySerializer()
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static int OBJECT_VERSION = 0;
}
