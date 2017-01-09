package ch.infbr5.sentinel.server.db;

import org.flywaydb.core.Flyway;


public class DatabaseMigration {

   public void start() {
      // Create the Flyway instance
      Flyway flyway = new Flyway();

      // Point it to the database
      flyway.setDataSource("jdbc:derby:db;create=true", "sentinel", "PWD");

      // Start the migration
      flyway.setTable("FLYWAY_SCHEMA_VERSION");
      flyway.setBaselineOnMigrate(true);
      flyway.migrate();
   }

}
