package ca.nightfury.minecraft.plugin.block.rewards;

import java.util.Objects;

import org.bukkit.World;
import org.bukkit.World.Environment;

public enum RewardWorlds
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    NORMAL(Environment.NORMAL),
    NETHER(Environment.NETHER),
    THE_END(Environment.THE_END);

    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public boolean isApplicable(final World world)
    {
        return Objects.equals(m_environment, world.getEnvironment());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private RewardWorlds(final Environment environment)
    {
        m_environment = environment;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final Environment m_environment;
}
