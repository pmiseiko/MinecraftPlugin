package ca.nightfury.minecraft.plugin.block.rewards;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public enum RewardBreakingTool
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    // Pickaxe type -------- Attack damage --- Damage per second (DPS)
    // Wooden Pickaxe ------ 2 --------------- 2.4
    // Golden Pickaxe ------ 2 --------------- 2.4
    // Stone Pickaxe ------- 3 --------------- 3.6
    // Iron Pickaxe -------- 4 --------------- 4.8
    // Diamond Pickaxe ----- 5 --------------- 6
    // Netherite Pickaxe --- 6 --------------- 7.2
    NETHERITE_PICKAXE(Material.NETHERITE_PICKAXE),
    DIAMOND_PICKAXE(Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE),
    IRON_PICKAXE(Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE),
    STONE_PICKAXE(Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE),
    GOLDEN_PICKAXE(
            Material.NETHERITE_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.IRON_PICKAXE,
            Material.STONE_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.WOODEN_PICKAXE),
    WOODEN_PICKAXE(
            Material.NETHERITE_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.IRON_PICKAXE,
            Material.STONE_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.WOODEN_PICKAXE);

    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public boolean isApplicable(final Material tool)
    {
        return m_breakingTool.contains(tool);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private RewardBreakingTool(final Material... tools)
    {
        m_breakingTool = Arrays.asList(tools);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final List<Material> m_breakingTool;
}
