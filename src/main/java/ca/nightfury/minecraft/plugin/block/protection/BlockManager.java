package ca.nightfury.minecraft.plugin.block.protection;

import java.util.Map;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

public class BlockManager
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public BlockManager(final Database database, final PluginLogger logger)
    {
        m_database = Objects.requireNonNull(database);
        m_logger = Objects.requireNonNull(logger);
    }

    public void registerBlockOwner(final Block block, final PlayerIdentity player)
    {
        m_database.createBlockOwner(block, player);
    }

    public void registerBlockOwner(final Block block, final Player player)
    {
        registerBlockOwner(block, new PlayerIdentity(player));
        m_logger.info(
                String.format(
                        "%s[%s] registered %s at %d/%d/%d",
                        player.getName(),
                        player.getUniqueId(),
                        block.getType(),
                        block.getX(),
                        block.getY(),
                        block.getZ()));
    }

    public void unregisterBlockOwner(final BlockIdentity blockIdentity)
    {
        m_database.deleteBlockOwner(blockIdentity);
    }

    public void unregisterBlockOwner(final Block block, final Player player)
    {
        unregisterBlockOwner(new BlockIdentity(block));
        m_logger.info(
                String.format(
                        "%s[%s] unregistered %s at %d/%d/%d",
                        player.getName(),
                        player.getUniqueId(),
                        block.getType(),
                        block.getX(),
                        block.getY(),
                        block.getZ()));
    }

    public boolean isBlockOwned(final BlockIdentity blockIdentity)
    {
        return m_database.isBlockOwned(blockIdentity);
    }

    public boolean isBlockOwned(final Location location)
    {
        return isBlockOwned(new BlockIdentity(location));
    }

    public boolean isBlockOwned(final Block block)
    {
        return isBlockOwned(new BlockIdentity(block));
    }

    public boolean isBlockOwnedByPlayer(final BlockIdentity blockIdentity, final PlayerIdentity playerIdentity)
    {
        return m_database.isBlockOwner(blockIdentity, playerIdentity);
    }

    public boolean isBlockOwnedByPlayer(final Location location, final Player player)
    {
        return isBlockOwnedByPlayer(new BlockIdentity(location), new PlayerIdentity(player));
    }

    public boolean isBlockOwnedByPlayer(final Block block, final Player player)
    {
        return isBlockOwnedByPlayer(new BlockIdentity(block), new PlayerIdentity(player));
    }

    public boolean isBlockOwnedByPlayer(final Block block, final PlayerIdentity playerIdentity)
    {
        return isBlockOwnedByPlayer(new BlockIdentity(block), playerIdentity);
    }

    public Map<BlockIdentity, PlayerIdentity> getBlockOwners()
    {
        return m_database.getBlockOwners();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final Database m_database;
    private final PluginLogger m_logger;
}
