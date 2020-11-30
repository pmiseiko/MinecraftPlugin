package ca.nightfury.minecraft.plugin.block.protection;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Server;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentity;

public interface ProtectionDatabase extends Flushable, Closeable
{
    void integrityCheck(final Server server);

    void createBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity);

    void deleteBlockOwner(final BlockIdentity blockIdentity);

    Set<BlockIdentity> getOwnedBlocks();

    PlayerIdentity getBlockOwner(final BlockIdentity blockIdentity);

    boolean isBlockOwned(final BlockIdentity blockIdentity);

    boolean isBlockOwner(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity);

    Map<BlockIdentity, Material> getBlockTypes();

    Material getBlockType(final BlockIdentity blockIdentity);

    void setBlockType(final BlockIdentity blockIdentity, final Material material);

    void deleteBlockType(final BlockIdentity blockIdentity);
}
