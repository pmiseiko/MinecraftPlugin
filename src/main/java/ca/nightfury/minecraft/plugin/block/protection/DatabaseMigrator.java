package ca.nightfury.minecraft.plugin.block.protection;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.plugin.PluginLogger;

import ca.nightfury.minecraft.plugin.database.BlockIdentity;
import ca.nightfury.minecraft.plugin.database.PlayerIdentity;

public class DatabaseMigrator implements Flushable, Closeable
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public DatabaseMigrator(final File dataFolder, final PluginLogger logger) throws SQLException
    {
        final String absolutePath = dataFolder.getAbsolutePath();
        final String databaseURL = String.format("jdbc:sqlite:%s/%s", absolutePath, DATABASE_FILENAME);

        m_databaseConnection = DriverManager.getConnection(databaseURL);
        m_databaseConnection.setAutoCommit(false);
        m_logger = logger;

        final Statement sqlStatement = m_databaseConnection.createStatement();

        sqlStatement.execute(CREATE_BLOCK_OWNERSHIP_TABLE);
    }

    public void migrate(final ProtectionDatabase newDatabase)
    {
        final Map<BlockIdentity, PlayerIdentity> blockOwners = getBlockOwners();

        m_logger.info(String.format("Old database had %d owned blocks", blockOwners.size()));

        for (final Entry<BlockIdentity, PlayerIdentity> entry : blockOwners.entrySet())
        {
            final BlockIdentity blockIdentity = entry.getKey();
            final PlayerIdentity playerIdentity = entry.getValue();
            final Material blockType = Material.getMaterial(getBlockType(blockIdentity));

            newDatabase.createBlockOwner(blockIdentity, playerIdentity);
            newDatabase.setBlockType(blockIdentity, blockType);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flushable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void flush() throws IOException
    {
        try
        {
            m_databaseConnection.commit();
            m_logger.info("Block protection database flushed");
        }
        catch (final SQLException exception)
        {
            throw new IOException(exception);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Closeable Override(s).
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void close() throws IOException
    {
        try
        {
            m_databaseConnection.close();
            m_logger.info("Block protection database closed");
        }
        catch (final SQLException exception)
        {
            throw new IOException(exception);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    private Map<BlockIdentity, PlayerIdentity> getBlockOwners()
    {
        final Map<BlockIdentity, PlayerIdentity> blockOwners = new HashMap<>();

        try
        {
            final PreparedStatement preparedStatement = m_databaseConnection.prepareStatement(QUERY_BLOCK_OWNERS);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                final BlockIdentity blockIdentity = new BlockIdentity(
                        UUID.fromString(resultSet.getString("world_id")),
                        resultSet.getInt("block_x_coordinate"),
                        resultSet.getInt("block_y_coordinate"),
                        resultSet.getInt("block_z_coordinate"));
                final PlayerIdentity playerIdentity = new PlayerIdentity(
                        UUID.fromString(resultSet.getString("owner_id")));

                blockOwners.put(blockIdentity, playerIdentity);
            }
        }
        catch (final SQLException exception)
        {
            m_logger.log(Level.SEVERE, QUERY_BLOCK_OWNER, exception);
        }

        return blockOwners;
    }

    private String getBlockType(final BlockIdentity blockIdentity)
    {
        try
        {
            final PreparedStatement preparedStatement = m_databaseConnection.prepareStatement(QUERY_BLOCK_TYPE);

            preparedStatement.setString(1, blockIdentity.getWorldUUID().toString());
            preparedStatement.setInt(2, blockIdentity.getXCoordinate());
            preparedStatement.setInt(3, blockIdentity.getYCoordinate());
            preparedStatement.setInt(4, blockIdentity.getZCoordinate());

            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                return resultSet.getString("block_type");
            }
        }
        catch (final SQLException exception)
        {
            m_logger.log(Level.SEVERE, QUERY_BLOCK_OWNED, exception);
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Non-Public Field(s).
    ///////////////////////////////////////////////////////////////////////////

    private final static String CREATE_BLOCK_OWNERSHIP_TABLE;
    private final static String QUERY_BLOCK_OWNED;
    private final static String QUERY_BLOCK_OWNER;
    private final static String QUERY_BLOCK_OWNERS;
    private final static String QUERY_BLOCK_TYPE;
    private final static String DATABASE_FILENAME = "block_manager.db";
    private final Connection m_databaseConnection;
    private final PluginLogger m_logger;

    ///////////////////////////////////////////////////////////////////////////
    // Static Initializer(s).
    ///////////////////////////////////////////////////////////////////////////

    static
    {
        final StringBuilder createBlockOwnershipTable = new StringBuilder();
        final StringBuilder queryBlockOwned = new StringBuilder();
        final StringBuilder queryBlockOwner = new StringBuilder();
        final StringBuilder queryBlockOwners = new StringBuilder();
        final StringBuilder queryBlockType = new StringBuilder();

        createBlockOwnershipTable.append("CREATE TABLE IF NOT EXISTS block_ownership (");
        createBlockOwnershipTable.append("world_id TEXT NOT NULL,");
        createBlockOwnershipTable.append("block_type TEXT NOT NULL,");
        createBlockOwnershipTable.append("block_x_coordinate INTEGER NOT NULL,");
        createBlockOwnershipTable.append("block_y_coordinate INTEGER NOT NULL,");
        createBlockOwnershipTable.append("block_z_coordinate INTEGER NOT NULL,");
        createBlockOwnershipTable.append("owner_id INTEGER NOT NULL,");
        createBlockOwnershipTable.append("UNIQUE(world_id,");
        createBlockOwnershipTable.append("       block_x_coordinate,");
        createBlockOwnershipTable.append("       block_y_coordinate,");
        createBlockOwnershipTable.append("       block_z_coordinate))");

        queryBlockOwned.append("SELECT owner_id FROM block_ownership WHERE ");
        queryBlockOwned.append("world_id = ? AND ");
        queryBlockOwned.append("block_x_coordinate = ? AND ");
        queryBlockOwned.append("block_y_coordinate = ? AND ");
        queryBlockOwned.append("block_z_coordinate = ?");

        queryBlockOwner.append("SELECT owner_id FROM block_ownership WHERE ");
        queryBlockOwner.append("world_id = ? AND ");
        queryBlockOwner.append("block_x_coordinate = ? AND ");
        queryBlockOwner.append("block_y_coordinate = ? AND ");
        queryBlockOwner.append("block_z_coordinate = ? AND ");
        queryBlockOwner.append("owner_id = ?");

        queryBlockOwners.append("SELECT * FROM block_ownership");

        queryBlockType.append("SELECT block_type FROM block_ownership WHERE ");
        queryBlockType.append("world_id = ? AND ");
        queryBlockType.append("block_x_coordinate = ? AND ");
        queryBlockType.append("block_y_coordinate = ? AND ");
        queryBlockType.append("block_z_coordinate = ?");

        CREATE_BLOCK_OWNERSHIP_TABLE = createBlockOwnershipTable.toString();
        QUERY_BLOCK_OWNED = queryBlockOwned.toString();
        QUERY_BLOCK_OWNER = queryBlockOwner.toString();
        QUERY_BLOCK_OWNERS = queryBlockOwners.toString();
        QUERY_BLOCK_TYPE = queryBlockType.toString();
    }
}
