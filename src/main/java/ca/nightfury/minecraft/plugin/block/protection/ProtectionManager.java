package ca.nightfury.minecraft.plugin.block.protection;

import java.util.List;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.block.Block;

public interface ProtectionManager extends ProtectionDatabase
{
    @Override
    void integrityCheck(final Server server);

    int getMinimumProtectableBlocksBeforeProtectionActivation();

    Set<Block> getAttachedProtectableBlocks(final Block originBlock, final int limit);

    List<Block> getProtectedBlocks(final Block originBlock, final int distance);
}
