package ch.infbr5.sentinel.client;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.infbr5.sentinel.client.gui.components.configuration.AbstractAdminOverviewPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.CheckpointConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.ConfigurationValuePanel;
import ch.infbr5.sentinel.client.gui.components.configuration.EinheitenConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.PersonenConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.PrintConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.ZoneConfigPanel;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;

public class AdminstrationFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static AdminstrationFrame instance;

	public static AdminstrationFrame getInstance() {
		if (instance == null) {
			instance = new AdminstrationFrame();
			instance.setVisible(false);
		}
		
		return instance;
	}

	private JTabbedPane tabbedPane;

	private AdminstrationFrame() {
		initComponents();
	}
	
	private void initComponents() {
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JTabbedPane source = (JTabbedPane) arg0.getSource();
				Component c = source.getSelectedComponent();
				if (c instanceof AbstractAdminOverviewPanel) {
					AbstractAdminOverviewPanel<?> a = (AbstractAdminOverviewPanel<?>) c;
					a.clearAndHideFilter();
					a.updateModel();
				}
			}
		});
		tabbedPane.addTab("Checkpoints", new CheckpointConfigPanel(ConfigurationLocalHelper.getConfig().isAdminMode()));
		tabbedPane.addTab("Einheiten", new EinheitenConfigPanel(ConfigurationLocalHelper.getConfig().isAdminMode()));
		tabbedPane.addTab("Personen", new PersonenConfigPanel((JFrame) this,ConfigurationLocalHelper.getConfig().isAdminMode()));
		tabbedPane.addTab("Printjobs", new PrintConfigPanel(ConfigurationLocalHelper.getConfig().isAdminMode()));
		tabbedPane.addTab("Configuration", new ConfigurationValuePanel(ConfigurationLocalHelper.getConfig().isAdminMode()));
		tabbedPane.addTab("Zonen", new ZoneConfigPanel(ConfigurationLocalHelper.getConfig().isAdminMode()));
		
		this.setContentPane(tabbedPane);
		
		pack();
	}
}
