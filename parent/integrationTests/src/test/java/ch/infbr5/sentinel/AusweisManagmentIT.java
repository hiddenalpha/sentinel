package ch.infbr5.sentinel;

import static org.fest.swing.data.TableCell.row;

import java.util.regex.Pattern;

import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.infbr5.sentinel.client.AdminstrationFrame;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
import ch.infbr5.sentinel.client.gui.components.checkin.AusweisInfoPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.ConfigurationValuePanel;
import ch.infbr5.sentinel.client.gui.components.configuration.EinheitenConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.PersonenConfigPanel;
import ch.infbr5.sentinel.testutils.Helper;

public class AusweisManagmentIT {

	private FrameFixture window;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Helper.setupRuntime();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Helper.cleanupRuntime();
	}

	@Before
	public void setUp() throws Exception {
		window = Helper.getWindow();
		window.show(); // shows the frame to test

	}

	@After
	public void tearDown() throws Exception {
		window.cleanUp();
	}


	public void test_LeereDatenbank_ManuellenPersonenauswahl_Warnmeldung() {
		window.menuItem(AppMenuBar.CMD_DISPLAY_PERSON_SELECTION_DLG).click();
		window.optionPane().requireWarningMessage();
		window.dialog().button().click();
	}

	@Test
	public void test_ConfigruationErfassen() {
		window.menuItem(AppMenuBar.CMD_EINSTELLUNGEN).click();

		FrameFixture adminWindow = WindowFinder.findFrame(
				AdminstrationFrame.FRAME_NAME).using(window.robot);

		adminWindow.tabbedPane().selectTab("Configuration");
		adminWindow.button(ConfigurationValuePanel.BUTTON_ADMINPANEL_NEW).click();
		adminWindow.textBox("Key").enterText("AnzahlPersonen");
		adminWindow.textBox("String Value").enterText("23");
		adminWindow.textBox("Long Value").enterText("24");

		adminWindow.button(ConfigurationValuePanel.BUTTON_ADMINPANEL_SAVE).click();

		adminWindow.table().requireCellValue(row(6).column(0), "AnzahlPersonen");
		adminWindow.table().requireCellValue(row(6).column(1), "23");

	}

	@Test
	public void test_EinheitPersonErfassen() {
		window.menuItem(AppMenuBar.CMD_EINSTELLUNGEN).click();

		FrameFixture adminWindow = WindowFinder.findFrame(
				AdminstrationFrame.FRAME_NAME).using(window.robot);

		adminWindow.tabbedPane().selectTab("Einheiten");
		adminWindow.button(EinheitenConfigPanel.BUTTON_ADMINPANEL_NEW).click();
		adminWindow.textBox("Name").enterText("Test Einheit");
		adminWindow.textBox("RGB Color Gs Vb").enterText("000000");
		adminWindow.textBox("RGB Color Trp K").enterText("99BB11");
		adminWindow.textBox("RGB Color Einh").enterText("FFFFFF");
		adminWindow.textBox("Text Gs Vb").enterText("123");
		adminWindow.textBox("Text Trp K").enterText("abc");
		adminWindow.textBox("Text Einh").enterText("d");

		adminWindow.button(EinheitenConfigPanel.BUTTON_ADMINPANEL_SAVE).click();

		// Person erfassen (ohne Ausweis)

		adminWindow.tabbedPane().selectTab("Personen");
		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_NEW).click();
		adminWindow.textBox("Name").enterText("Muster");
		adminWindow.textBox("Vorname").enterText("Moritz");

		adminWindow.textBox("Geburtsdatum").enterText("01.01.1976");
		adminWindow.textBox("Funktion").enterText("Test Person");
		adminWindow.textBox("AhvNr").enterText("000.0000.9001.02");

		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_SAVE).click();
		adminWindow.table().requireRowCount(1);

		// Person erfassen (mit Ausweis)

		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_NEW).click();
		adminWindow.textBox("Name").enterText("Beispiel");
		adminWindow.textBox("Vorname").enterText("Ingo");

		adminWindow.textBox("Geburtsdatum").enterText("01.01.1976");
		adminWindow.textBox("Funktion").enterText("Test Person");
		adminWindow.textBox("AhvNr").enterText("000.0000.9002.01");

		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_SAVE).click();

		adminWindow.table().requireRowCount(2);
		adminWindow.table().selectRows(1);

		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_EDIT).click();
		adminWindow.button(PersonenConfigPanel.BUTTON_NEUER_AUSWEIS).click();
		adminWindow.dialog().button().click();
		adminWindow.button(PersonenConfigPanel.BUTTON_ADMINPANEL_SAVE).click();

		window.menuItem(AppMenuBar.CMD_DISPLAY_PERSON_SELECTION_DLG).click();
		window.optionPane().okButton().click();

		window.label(AusweisInfoPanel.LABEL_STATUS_TEXT).requireText(Pattern.compile("Checkin erfolgreich.*"));
	}

}
