package db.migration;

import java.sql.Connection;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

public class V1_1__Init implements JdbcMigration {
    @Override
	public void migrate(Connection connection) throws Exception {
    	/* Sample - siehe parent/doc/entwickler.doc */

        //PreparedStatement statement = connection.prepareStatement("INSERT INTO CHECKPOINT (name) VALUES ('Nebenposten')");

        //try {
        //    statement.execute();
        //} finally {
        //    statement.close();
        //}
    }

}
