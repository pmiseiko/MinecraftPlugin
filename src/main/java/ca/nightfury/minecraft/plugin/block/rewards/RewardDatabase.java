package ca.nightfury.minecraft.plugin.block.rewards;

import java.io.Closeable;
import java.io.Flushable;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;

public interface RewardDatabase extends Flushable, Closeable
{
    boolean rewardBlock(final BlockIdentity blockIdentity);
}
