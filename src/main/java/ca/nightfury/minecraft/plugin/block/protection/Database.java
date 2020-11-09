package ca.nightfury.minecraft.plugin.block.protection;

import java.util.Map;

import org.bukkit.block.Block;

public interface Database
{
    boolean createBlockOwner(final Block block, final PlayerIdentity player);

    boolean deleteBlockOwner(final BlockIdentity blockIdentity);

    boolean isBlockOwned(final BlockIdentity blockIdentity);

    boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity);

    Map<BlockIdentity, PlayerIdentity> getBlockOwners();

    String getBlockType(final BlockIdentity block);
}
