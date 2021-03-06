package ca.nightfury.minecraft.plugin.block.rewards;

import java.util.Objects;

import org.bukkit.block.Biome;

public enum RewardBiomes
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    ANY(null),
    BADLANDS(Biome.BADLANDS),
    MOUNTAINS(Biome.MOUNTAINS);

    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public boolean isApplicable(final Biome biome)
    {
        return ANY.equals(this) || Objects.equals(biome, m_biome);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private RewardBiomes(final Biome biome)
    {
        m_biome = biome;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final Biome m_biome;
}