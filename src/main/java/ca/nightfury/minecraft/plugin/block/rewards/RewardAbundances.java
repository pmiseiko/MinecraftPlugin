package ca.nightfury.minecraft.plugin.block.rewards;

import ca.nightfury.minecraft.plugin.services.PRNG;

public enum RewardAbundances
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    VERY_COMMON(20_000), // 2.00%
    COMMON(10_000), // 1.00%
    UNCOMMON(5_000), // 0.50%
    RARE(2_500), // 0.25%
    VERY_RARE(1_250); // 0.125%

    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public boolean discovered()
    {
        return m_chance > PRNG.nextInt(MAXIMUM_CHANCE_VALUE);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private RewardAbundances(final int chance)
    {
        m_chance = chance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static int MAXIMUM_CHANCE_VALUE = 1_000_000;
    private final int m_chance;
}