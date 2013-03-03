package org.mylan.openie.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.mylan.openie.utils.Property;

/**
 * Describe class Mysql here.
 *
 *
 * Created: Wed Jan 16 16:49:55 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Mysql {
    private final Properties sqlProperty;
    private final String databaseName;

    private Connection sqlConnection = null;

    public Mysql(final String databaseName) {
        Properties analyseProperties = Property.create("analyse.properties");
        String language = analyseProperties.getProperty("language");

        this.databaseName = language + databaseName;
        sqlProperty = Property.create("sql.properties");
    }

    public void openConnection() {
        if (sqlConnection == null) {
            String database = sqlProperty.getProperty(databaseName + "Database");
            String server = sqlProperty.getProperty(databaseName + "Hostname");
            String user = sqlProperty.getProperty(databaseName + "User");
            String password = sqlProperty.getProperty(databaseName + "Password");
	    try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		sqlConnection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                closeConnection();
	    } catch (Exception e) {
                e.printStackTrace();
                closeConnection();
            }
	}
    }

    public void closeConnection(final PreparedStatement ... statements) {
        closePreparedStatements(statements);
	if (sqlConnection != null) {
	    try {
		sqlConnection.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private void closePreparedStatements(final PreparedStatement ... statements) {
        for (PreparedStatement statement : statements) {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PreparedStatement createPreparedStatement(final String statement) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = sqlConnection.prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    public void setAutoCommit(final boolean isSet) {
        try {
            sqlConnection.setAutoCommit(isSet);
        } catch (SQLException e) {
            System.out.println("Problem with setting autocommit: " + e);
        }
    }

    public void commit() throws SQLException {
        sqlConnection.commit();
    }

    public void rollback() throws SQLException {
        sqlConnection.commit();
    }
}
