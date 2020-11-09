package ca.nightfury.minecraft.plugin.block.rewards;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import ca.nightfury.minecraft.plugin.services.PRNG;

/** @see <a href="https://minecraft.gamepedia.com/Ore">Ore Availability</a> */
public enum RewardMaterials
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    COAL_ORE(
            Material.COAL,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            131, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.VERY_COMMON,
            RewardBreakingTool.WOODEN_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    IRON_ORE(
            Material.IRON_NUGGET,
            1, // Minimum Amount (inclusive)
            9, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            67, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.COMMON,
            RewardBreakingTool.STONE_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    LAPIS_LAZULI_ORE(
            Material.LAPIS_LAZULI,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            14, // Minimum Y Coordinate (inclusive)
            33, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.RARE,
            RewardBreakingTool.STONE_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    GOLD_ORE_ANY(
            Material.GOLD_NUGGET,
            1, // Minimum Amount (inclusive)
            9, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            33, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.UNCOMMON,
            RewardBreakingTool.IRON_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    GOLD_ORE_BADLANDS(
            Material.GOLD_NUGGET,
            1, // Minimum Amount (inclusive)
            9, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            79, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.BADLANDS,
            RewardAbundances.COMMON,
            RewardBreakingTool.IRON_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    REDSTONE_ORE(
            Material.REDSTONE,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            19, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.UNCOMMON,
            RewardBreakingTool.IRON_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    DIAMOND_ORE(
            Material.DIAMOND,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            19, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.ANY,
            RewardAbundances.RARE,
            RewardBreakingTool.IRON_PICKAXE,
            Arrays.asList(Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.STONE)),
    EMERALD_ORE_MOUNTAINS(
            Material.EMERALD,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            5, // Minimum Y Coordinate (inclusive)
            32, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NORMAL,
            RewardBiomes.MOUNTAINS,
            RewardAbundances.VERY_RARE,
            RewardBreakingTool.IRON_PICKAXE,
            Arrays.asList(Material.STONE)),
    NETHER_QUARTZ(
            Material.QUARTZ,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            10, // Minimum Y Coordinate (inclusive)
            127, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NETHER,
            RewardBiomes.ANY,
            RewardAbundances.VERY_COMMON,
            RewardBreakingTool.WOODEN_PICKAXE,
            Arrays.asList(Material.NETHERRACK)),
    NETHER_GOLD(
            Material.GOLD_NUGGET,
            1, // Minimum Amount (inclusive)
            9, // Maximum Amount (inclusive)
            15, // Minimum Y Coordinate (inclusive)
            116, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NETHER,
            RewardBiomes.ANY,
            RewardAbundances.COMMON,
            RewardBreakingTool.WOODEN_PICKAXE,
            Arrays.asList(Material.BASALT, Material.BLACKSTONE, Material.NETHERRACK)),
    ANCIENT_DEBRIS(
            Material.NETHERITE_SCRAP,
            1, // Minimum Amount (inclusive)
            1, // Maximum Amount (inclusive)
            6, // Minimum Y Coordinate (inclusive)
            119, // Maximum Y Coordinate (inclusive)
            RewardWorlds.NETHER,
            RewardBiomes.ANY,
            RewardAbundances.VERY_RARE,
            RewardBreakingTool.WOODEN_PICKAXE,
            Arrays.asList(Material.BASALT, Material.BLACKSTONE, Material.NETHERRACK));

    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public static List<RewardMaterials> getRewardMaterials(
            final World world,
            final Biome biome,
            final Location location,
            final Material rewardingMaterial,
            final Material breakingTool)
    {
        final List<RewardMaterials> rewardBlocks = new LinkedList<>();
        for (final RewardMaterials material : values())
        {
            if (material.isApplicable(world, biome, location, rewardingMaterial, breakingTool))
            {
                rewardBlocks.add(material);
            }
        }

        return Collections.unmodifiableList(rewardBlocks);
    }

    public boolean discovered()
    {
        return m_abundance.discovered();
    }

    public ItemStack getItemStack()
    {
        final int amount = (m_minimumAmount == m_maximumAmount) ? m_maximumAmount
                : m_minimumAmount + PRNG.nextInt(m_maximumAmount);

        return new ItemStack(m_rewardMaterial, amount);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private RewardMaterials(
            final Material rewardMaterial,
            final int minimumAmount,
            final int maximumAmount,
            final double minimumYCoordinate,
            final double maximumYCoordinate,
            final RewardWorlds world,
            final RewardBiomes biome,
            final RewardAbundances abundance,
            final RewardBreakingTool breakingTool,
            final List<Material> rewardingMaterials)
    {
        m_rewardMaterial = rewardMaterial;
        m_minimumYCoordinate = minimumYCoordinate;
        m_maximumYCoordinate = maximumYCoordinate;
        m_world = world;
        m_biome = biome;
        m_abundance = abundance;
        m_breakingTool = breakingTool;
        m_rewardingMaterials = rewardingMaterials;
        m_minimumAmount = minimumAmount;
        m_maximumAmount = maximumAmount;
    }

    private boolean isApplicable(
            final World world,
            final Biome biome,
            final Location location,
            final Material rewardingMaterial,
            final Material breakingTool)
    {
        final double yCoordinate = location.getY();

        return (yCoordinate >= m_minimumYCoordinate) &&
                (yCoordinate <= m_maximumYCoordinate) &&
                m_world.isApplicable(world) &&
                m_biome.isApplicable(biome) &&
                m_breakingTool.isApplicable(breakingTool) &&
                m_rewardingMaterials.contains(rewardingMaterial);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final Material m_rewardMaterial;
    private final int m_minimumAmount;
    private final int m_maximumAmount;
    private final double m_minimumYCoordinate;
    private final double m_maximumYCoordinate;
    private final RewardWorlds m_world;
    private final RewardBiomes m_biome;
    private final RewardAbundances m_abundance;
    private final RewardBreakingTool m_breakingTool;
    private final List<Material> m_rewardingMaterials;
}
