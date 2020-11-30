package ca.nightfury.minecraft.plugin.player.hearth;

import java.io.Closeable;
import java.io.Flushable;

import ca.nightfury.minecraft.plugin.database.PlayerIdentity;

public interface HearthDatabase extends Flushable, Closeable
{
    void disableHearth(final PlayerIdentity playerIdentity);

    boolean isHearthDisabled(final PlayerIdentity playerIdentity);
}
