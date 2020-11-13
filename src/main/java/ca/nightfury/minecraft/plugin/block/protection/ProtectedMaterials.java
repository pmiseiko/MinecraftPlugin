package ca.nightfury.minecraft.plugin.block.protection;

import org.bukkit.Material;

public class ProtectedMaterials
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isProtectedInteractiveMaterial(final Material material)
    {
        switch (material)
        {
            case BARREL:
            case BLAST_FURNACE:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case FURNACE:
            case HOPPER:
            case SMOKER:

            case ACACIA_DOOR:
            case ACACIA_TRAPDOOR:
            case BIRCH_DOOR:
            case BIRCH_TRAPDOOR:
            case CRIMSON_DOOR:
            case CRIMSON_TRAPDOOR:
            case DARK_OAK_DOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_DOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_DOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_DOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_DOOR:
            case SPRUCE_TRAPDOOR:
            case WARPED_DOOR:
            case WARPED_TRAPDOOR:

            case ACACIA_BUTTON:
            case BIRCH_BUTTON:
            case CRIMSON_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case POLISHED_BLACKSTONE_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case WARPED_BUTTON:

            case ACACIA_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case CRIMSON_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case POLISHED_BLACKSTONE_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
            case WARPED_PRESSURE_PLATE:
                return true;

            default:
                return false;
        }
    }

    public static boolean isProtectedBuildingMaterial(final Material material)
    {
        switch (material)
        {
            case COAL_BLOCK:
            case IRON_BLOCK:
            case LAPIS_BLOCK:
            case GOLD_BLOCK:
            case REDSTONE_BLOCK:
            case DIAMOND_BLOCK:
            case EMERALD_BLOCK:
            case QUARTZ_BLOCK:
            case NETHERITE_BLOCK:

            case ACACIA_SLAB:
            case ANDESITE_SLAB:
            case BIRCH_SLAB:
            case BLACKSTONE_SLAB:
            case BRICK_SLAB:
            case COBBLESTONE_SLAB:
            case CRIMSON_SLAB:
            case CUT_RED_SANDSTONE_SLAB:
            case CUT_SANDSTONE_SLAB:
            case DARK_OAK_SLAB:
            case DARK_PRISMARINE_SLAB:
            case DIORITE_SLAB:
            case END_STONE_BRICK_SLAB:
            case GRANITE_SLAB:
            case JUNGLE_SLAB:
            case MOSSY_COBBLESTONE_SLAB:
            case MOSSY_STONE_BRICK_SLAB:
            case NETHER_BRICK_SLAB:
            case OAK_SLAB:
            case PETRIFIED_OAK_SLAB:
            case POLISHED_ANDESITE_SLAB:
            case POLISHED_BLACKSTONE_BRICK_SLAB:
            case POLISHED_BLACKSTONE_SLAB:
            case POLISHED_DIORITE_SLAB:
            case POLISHED_GRANITE_SLAB:
            case PRISMARINE_BRICK_SLAB:
            case PRISMARINE_SLAB:
            case PURPUR_SLAB:
            case QUARTZ_SLAB:
            case RED_NETHER_BRICK_SLAB:
            case RED_SANDSTONE_SLAB:
            case SANDSTONE_SLAB:
            case SMOOTH_QUARTZ_SLAB:
            case SMOOTH_RED_SANDSTONE_SLAB:
            case SMOOTH_SANDSTONE_SLAB:
            case SMOOTH_STONE_SLAB:
            case SPRUCE_SLAB:
            case STONE_BRICK_SLAB:
            case STONE_SLAB:
            case WARPED_SLAB:

            case ACACIA_STAIRS:
            case ANDESITE_STAIRS:
            case BIRCH_STAIRS:
            case BLACKSTONE_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case CRIMSON_STAIRS:
            case DARK_OAK_STAIRS:
            case DARK_PRISMARINE_STAIRS:
            case DIORITE_STAIRS:
            case END_STONE_BRICK_STAIRS:
            case GRANITE_STAIRS:
            case JUNGLE_STAIRS:
            case MOSSY_COBBLESTONE_STAIRS:
            case MOSSY_STONE_BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
            case OAK_STAIRS:
            case POLISHED_ANDESITE_STAIRS:
            case POLISHED_BLACKSTONE_BRICK_STAIRS:
            case POLISHED_BLACKSTONE_STAIRS:
            case POLISHED_DIORITE_STAIRS:
            case POLISHED_GRANITE_STAIRS:
            case PRISMARINE_BRICK_STAIRS:
            case PRISMARINE_STAIRS:
            case PURPUR_STAIRS:
            case QUARTZ_STAIRS:
            case RED_NETHER_BRICK_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case SANDSTONE_STAIRS:
            case SMOOTH_QUARTZ_STAIRS:
            case SMOOTH_RED_SANDSTONE_STAIRS:
            case SMOOTH_SANDSTONE_STAIRS:
            case SPRUCE_STAIRS:
            case STONE_BRICK_STAIRS:
            case STONE_STAIRS:
            case WARPED_STAIRS:

            case BRICKS:
            case CHISELED_NETHER_BRICKS:
            case CHISELED_STONE_BRICKS:
            case CRACKED_NETHER_BRICKS:
            case CRACKED_POLISHED_BLACKSTONE_BRICKS:
            case CRACKED_STONE_BRICKS:
            case END_STONE_BRICKS:
            case INFESTED_CHISELED_STONE_BRICKS:
            case INFESTED_CRACKED_STONE_BRICKS:
            case INFESTED_MOSSY_STONE_BRICKS:
            case INFESTED_STONE_BRICKS:
            case MOSSY_STONE_BRICKS:
            case NETHER_BRICKS:
            case POLISHED_BLACKSTONE_BRICKS:
            case PRISMARINE_BRICKS:
            case QUARTZ_BRICKS:
            case RED_NETHER_BRICKS:
            case STONE_BRICKS:

            case GLASS:
            case GLASS_PANE:
            case BLACK_STAINED_GLASS:
            case BLACK_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS:
            case BROWN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS:
            case CYAN_STAINED_GLASS_PANE:
            case GRAY_STAINED_GLASS:
            case GRAY_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS:
            case GREEN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS:
            case ORANGE_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS:
            case PINK_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS:
            case PURPLE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS:
            case RED_STAINED_GLASS_PANE:
            case WHITE_STAINED_GLASS:
            case WHITE_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS:
            case YELLOW_STAINED_GLASS_PANE:
                return true;

            default:
                return false;
        }
    }

    public static boolean isProtectedMaterial(final Material material)
    {
        return isProtectedBuildingMaterial(material) || isProtectedInteractiveMaterial(material);
    }
}
