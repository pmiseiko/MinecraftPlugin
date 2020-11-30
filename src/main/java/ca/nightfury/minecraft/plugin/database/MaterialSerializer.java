package ca.nightfury.minecraft.plugin.database;

import java.io.IOException;
import java.io.InvalidObjectException;

import org.bukkit.Material;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class MaterialSerializer implements Serializer<Material>
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    public final static MaterialSerializer SINGLETON = new MaterialSerializer();

    ///////////////////////////////////////////////////////////////////////////
    // Serializer Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void serialize(final DataOutput2 out, final Material value) throws IOException
    {
        Serializer.INTEGER.serialize(out, OBJECT_VERSION);
        Serializer.STRING.serialize(out, value.name());
    }

    @Override
    public Material deserialize(final DataInput2 input, final int available) throws IOException
    {
        final int objectVersion = Serializer.INTEGER.deserialize(input, available);
        switch (objectVersion)
        {
            case 0:
                final String name = Serializer.STRING.deserialize(input, available);
                return Material.getMaterial(name);

            default:
                throw new InvalidObjectException("Unsupported object version: " + objectVersion);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private MaterialSerializer()
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static int OBJECT_VERSION = 0;
}
