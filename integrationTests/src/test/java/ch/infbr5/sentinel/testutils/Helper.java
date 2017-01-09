package ch.infbr5.sentinel.testutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;

import ch.infbr5.sentinel.client.ApplicationController;
import ch.infbr5.sentinel.client.StartupHandler;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.ApplicationFrame;
import ch.infbr5.sentinel.common.config.ConfigConstants;
import ch.infbr5.sentinel.server.ServerControl;
import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.Zutrittsregel;

public class Helper {

   private static ServerControl server;

   public static void setupRuntime() {

      writeClientProperties();

      server = new ServerControl(false, true);
      server.start("127.0.0.1", "8080");

      // Setup Database, so kommt kein Config Dialog zu beginn
      setupDatabase();
   }

   private static void setupDatabase() {

      final EntityManager em = EntityManagerHelper.createEntityManager();
      em.getTransaction().begin();

      // Zutrittsregeln
      final Zutrittsregel regel = ObjectFactory.createZutrittsregel();
      em.persist(regel);

      final List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>();
      regeln.add(regel);

      // Zone
      final Zone zone = ObjectFactory.createZone("Kommandoposten", regeln, false);
      em.persist(zone);

      // Checkpoint
      final List<Zone> checkInZonen = new ArrayList<Zone>();
      checkInZonen.add(zone);
      final List<Zone> checkOutZonen = new ArrayList<Zone>();
      final Checkpoint checkpoint = ObjectFactory.createCheckpoint("Haupteingang", checkInZonen, checkOutZonen);
      em.persist(checkpoint);

      // Konfiguration
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.PASSWORD_ADMIN, "leitnes", 0, ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.PASSWORD_SUPERUSER, "sentinel", 0, ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.PASSWORD_IDENTITY_CARD, "1nf8r5!", 0, ""));

      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_BACKGROUND_COLOR, "#ddd", 0, ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE, "#eee", 0,
            ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE, "false", 0,
            ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_SHOW_QR_CODE, "true", 0, ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_USE_USER_LOGO, "false", 0, ""));
      em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN, "false",
            0, ""));

      em.getTransaction().commit();
      em.close();

   }

   public static void cleanupRuntime() {
      server.stop();
      cleanupRuntimeDir();
   }

   public static void cleanupRuntimeDir() {
      new File("sentinel.properties").delete();
      new File("derby.log").delete();
   }

   public static FrameFixture getWindow() {
      final ApplicationFrame frame = GuiActionRunner.execute(new GuiQuery<ApplicationFrame>() {
         @Override
         protected ApplicationFrame executeInEDT() {
            new StartupHandler().startConfig();

            final ConfigurationLocalHelper config = ConfigurationLocalHelper.getConfig();
            final ApplicationController controller = new ApplicationController(config.getCheckpointWithName()
                  .getName(), config.getCheckpointId(), config.isAdminMode(), config.isSuperuserMode());
            controller.show();
            return controller.getFrame();
         }
      });
      return new FrameFixture(frame);
   }

   private static void writeClientProperties() {

      final Properties applicationProps = new Properties();
      applicationProps.setProperty("CheckpointId", "1");
      applicationProps.setProperty("ServerHostname", "127.0.0.1");
      applicationProps.setProperty("AdminMode", "true");
      try {
         FileOutputStream out;
         out = new FileOutputStream("sentinel.properties");
         applicationProps.store(out, "---Only for testing---");
         out.close();
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
