package ca.nightfury.minecraft.plugin.block.protection;

import java.io.IOException;
import java.util.UUID;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class BlockIdentitySerializer implements Serializer<BlockIdentity>
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    public final static BlockIdentitySerializer SINGLETON = new BlockIdentitySerializer();

    ///////////////////////////////////////////////////////////////////////////
    // Serializer Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void serialize(final DataOutput2 out, final BlockIdentity value) throws IOException
    {
        final UUID worldUUID = value.getWorldUUID();
        final int xCoordinate = value.getXCoordinate();
        final int yCoordinate = value.getYCoordinate();
        final int zCoordinate = value.getZCoordinate();

        Serializer.UUID.serialize(out, worldUUID);
        Serializer.INTEGER.serialize(out, xCoordinate);
        Serializer.INTEGER.serialize(out, yCoordinate);
        Serializer.INTEGER.serialize(out, zCoordinate);
    }

    @Override
    public BlockIdentity deserialize(final DataInput2 input, final int available) throws IOException
    {
        final UUID worldUUID = Serializer.UUID.deserialize(input, available);
        final int xCoordinate = Serializer.INTEGER.deserialize(input, available);
        final int yCoordinate = Serializer.INTEGER.deserialize(input, available);
        final int zCoordinate = Serializer.INTEGER.deserialize(input, available);

        return new BlockIdentity(worldUUID, xCoordinate, yCoordinate, zCoordinate);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private BlockIdentitySerializer()
    {
    }
}
