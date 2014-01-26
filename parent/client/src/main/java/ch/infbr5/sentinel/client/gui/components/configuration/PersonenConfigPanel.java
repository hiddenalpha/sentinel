package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.datatype.XMLGregorianCalendar;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.EinheitDetailsClient;
import ch.infbr5.sentinel.client.util.ImageCreator;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.util.XMLGregorianCalendarConverter;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.EinheitDetails;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;

public class PersonenConfigPanel extends AbstractAdminOverviewPanel<PersonDetails> {

	public static final String BUTTON_NEUER_AUSWEIS = "BUTTON_NEUER_AUSWEIS";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int IMAGE_WIDTH = 135;
	private static final int IMAGE_HEIGHT = 180;

	private JFrame parentFrame;

	public PersonenConfigPanel(JFrame parentFrame,boolean adminMode) {
		super(adminMode);
		this.parentFrame = parentFrame;
	}

	@Override
	protected AbstractAdminTableModel<PersonDetails> getTableModel() {
		return new MyTableModel();
	}

	public class MyTableModel extends AbstractAdminTableModel<PersonDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String[] headerNames = { "Grad", "Name", "Vorname", "Funktion", "Ausweis", "Einheit" };

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return getDataRecord(rowIndex).getGrad();
			} else if (columnIndex == 1) {
				return getDataRecord(rowIndex).getName();
			} else if (columnIndex == 2) {
				return getDataRecord(rowIndex).getVorname();
			} else if (columnIndex == 3) {
				return getDataRecord(rowIndex).getFunktion();
			} else if (columnIndex == 4) {
				return getDataRecord(rowIndex).getBarcode();
			} else if (columnIndex == 5) {
				return getDataRecord(rowIndex).getEinheitText();
			}

			return null;
		}

		@Override
		public PersonDetails getNewDataRecord() {
			PersonDetails detail = new PersonDetails();
			detail.setGrad("");
			detail.setName("");
			detail.setVorname("");
			detail.setBarcode("");
			detail.setEinheitId(-1l);
			detail.setFunktion("");
			detail.setImageId("");
			detail.setAhvNr("");
			detail.setImage(null);

			return detail;
		}

		@Override
		public void removeBackendObject(PersonDetails object) {
			ServiceHelper.getConfigurationsService().removePerson(object.getId());
		}

		@Override
		public void updateBackendObject(PersonDetails object) {
			ServiceHelper.getConfigurationsService().updatePerson(object);

		}

		@Override
		public List<PersonDetails> getBackendObjects() {
			ConfigurationResponse response = ServiceHelper.getConfigurationsService().getPersonen();
			return response.getPersonDetails();
		}

		@Override
		public String[] getHeaderNames() {
			return headerNames;
		}
	}

	@Override
	protected AbstractAdminDetailPanel<PersonDetails> getDetailPanel() {
		return new MyDetailPanel();
	}

	public class MyDetailPanel extends AbstractAdminDetailPanel<PersonDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField fieldAhvNr;
		private JTextField fieldFunktion;
		private JTextField fieldGeburtsdatum;
		private JComboBox<String> comboBoxGrad;
		private JTextField fieldName;
		private JTextField fieldVorname;
		private JComboBox<EinheitDetailsClient> comboBoxEinheit;
		private ImageIcon noFotoIcon;
		private JLabel fotoLabel;
		private JButton neuerAusweisButton;
		private JButton ausweisSperrenButton;
		private AbstractAction abstractActionAusweisSperren;
		private AbstractAction abstractActionNeuerAusweis;
		private ImageEditor imageEditor;

		public MyDetailPanel() {
			setLayout(new MigLayout("inset 20"));

			SwingHelper.addSeparator(this, "Person");

			List<String> gradValues = ServiceHelper.getConfigurationsService().getGradValues().getItem();
			comboBoxGrad = createDropdown("Grad", gradValues.toArray(new String[0]));
			fieldName = createField("Name");
			fieldVorname = createField("Vorname");

			comboBoxEinheit = createDropdown("Einheit");

			fieldGeburtsdatum = createField("Geburtsdatum","\\d{2}\\.\\d{2}\\.\\d{4}");
			fieldFunktion = createField("Funktion",".{2,}.*");
			fieldAhvNr = createField("AhvNr","[ahvnr]");
			
			SwingHelper.addSeparator(this, "Ausweis");
			JPanel ausweisPanel = new JPanel();
			ausweisPanel.setLayout(new MigLayout("", "[][]", "[][]"));
			createFotoLabel();
			ausweisPanel.add(fotoLabel, "spany");

			ausweisPanel.add(getAusweisSperrenButton());

			ausweisPanel.add(getNeuerAusweisButton());

			add(ausweisPanel);
		}

		private JButton getAusweisSperrenButton() {
			if (ausweisSperrenButton == null) {
				ausweisSperrenButton = new JButton("Ausweis sperren");

				ausweisSperrenButton.setAction(getAbstractActionAusweisSperren());
			}

			return ausweisSperrenButton;
		}

		private AbstractAction getAbstractActionAusweisSperren() {
			if (abstractActionAusweisSperren == null) {
				abstractActionAusweisSperren = new AbstractAction("Ausweis sperren", null) {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent evt) {
						ausweisSperren();
					}
				};
			}

			return abstractActionAusweisSperren;
		}

		private void ausweisSperren() {
			OperationResponse operationResponse = ServiceHelper.getSentinelService().invalidateAusweise(data.getId());
			if (operationResponse.getStatus() == OperationResponseStatus.SUCESS) {
				JOptionPane.showMessageDialog(this, "Ausweis gesperrt");
			} else {
				JOptionPane.showMessageDialog(this, "Konnte Ausweis nicht sperren");
			}
		}

		private JButton getNeuerAusweisButton() {
			if (neuerAusweisButton == null) {
				neuerAusweisButton = new JButton("Neuer Ausweis");
				neuerAusweisButton.setName(BUTTON_NEUER_AUSWEIS);
				neuerAusweisButton.setAction(getAbstractActionNeuerAusweis());
			}

			return neuerAusweisButton;
		}

		private AbstractAction getAbstractActionNeuerAusweis() {
			if (abstractActionNeuerAusweis == null) {
				abstractActionNeuerAusweis = new AbstractAction("Neuer Ausweis", null) {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent evt) {
						neuerAusweis();
					}
				};
			}

			return abstractActionNeuerAusweis;
		}

		private void neuerAusweis() {
			OperationResponse operationResponse = ServiceHelper.getSentinelService().neuerAusweis(data.getId());
			if (operationResponse.getStatus() == OperationResponseStatus.SUCESS) {
				JOptionPane.showMessageDialog(this, "Neuer Ausweis wird erstellt");
			} else {
				JOptionPane.showMessageDialog(this, "Konnte Ausweiserstellung nicht in Auftrag geben");
			}
		}

		private void openEditImagePanel() {
			imageEditor = new ImageEditor(parentFrame);
			imageEditor.setResizable(false);
			imageEditor.setVisible(true);

			imageEditor.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent arg0) {
					parentFrame.setEnabled(false);
				}

				@Override
				public void windowIconified(WindowEvent arg0) {
				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
				}

				@Override
				public void windowClosing(WindowEvent arg0) {
				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					parentFrame.setEnabled(true);

					if (imageEditor.getImage() != null) {
						data.setImage(convertBufferedImageToByte(imageEditor.getImage()));
					} else {
						data.setImage(null);
					}

					setFieldValues();
				}

				@Override
				public void windowActivated(WindowEvent arg0) {
				}
			});
		}

		private List<EinheitDetailsClient> copyEinheitModel(List<EinheitDetails> einheitDetails) {
			List<EinheitDetailsClient> einheiten = new ArrayList<EinheitDetailsClient>();
			for (EinheitDetails einheit : einheitDetails) {
				einheiten.add(new EinheitDetailsClient(einheit.getId(), einheit.getName()));
			}

			return einheiten;
		}

		private JComboBox<EinheitDetailsClient> createDropdown(String caption) {
			JComboBox<EinheitDetailsClient> combo = new JComboBox<EinheitDetailsClient>();

			addComboBox(caption, combo);

			return combo;
		}

		private JComboBox<String> createDropdown(String caption, String[] values) {
			JComboBox<String> combo = new JComboBox<String>(values);
			addComboBox(caption, combo);
			return combo;
		}

		private void addComboBox(String caption, JComboBox<?> combo) {
			add(SwingHelper.createLabel(caption), "gap para");
			add(combo, "span, growx");
			combo.setEditable(false);
		}

		private void refreshComboBoxEinheit() {
			comboBoxEinheit.removeAllItems();

			ConfigurationResponse response = ServiceHelper.getConfigurationsService().getEinheiten();
			List<EinheitDetailsClient> einheitDetails = copyEinheitModel(response.getEinheitDetails());
			for (EinheitDetailsClient einheit : einheitDetails) {
				comboBoxEinheit.addItem(einheit);
			}
		}

		private void createFotoLabel() {
			URL imageURL = getClass().getResource("/images/nobody.jpg");
			if (imageURL != null) {
				noFotoIcon = new ImageIcon(imageURL);
			}

			fotoLabel = new JLabel(this.noFotoIcon);
			fotoLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (fotoLabel.isEnabled()) {
						openEditImagePanel();
					}
				}
			});
		}

		public void getFieldValues() {
			data.setGrad(comboBoxGrad.getItemAt(comboBoxGrad.getSelectedIndex()));
			data.setName(fieldName.getText());
			data.setVorname(fieldVorname.getText());

			data.setEinheitId(comboBoxEinheit.getItemAt(comboBoxEinheit.getSelectedIndex()).getId());

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
			data.setGeburtsdatum(XMLGregorianCalendarConverter.stringToXMLGregorianCalendar(fieldGeburtsdatum.getText(), sdf));

			data.setFunktion(fieldFunktion.getText());
			data.setAhvNr(fieldAhvNr.getText());
		}

		public void setFieldValues() {
			comboBoxGrad.setSelectedItem(data.getGrad());
			fieldName.setText(data.getName());
			fieldVorname.setText(data.getVorname());

			refreshComboBoxEinheit();
			for (int i = 0; i < comboBoxEinheit.getItemCount(); ++i) {
				Long comboBoxId = comboBoxEinheit.getItemAt(i).getId();
				if (comboBoxId != null && comboBoxId.equals(data.getEinheitId())) {
					comboBoxEinheit.setSelectedIndex(i);

					break;
				}
			}

			XMLGregorianCalendar geburtsdatum = data.getGeburtsdatum();
			fieldGeburtsdatum.setText(geburtsdatum != null ? new SimpleDateFormat("dd.MM.yyyy").format(geburtsdatum
					.toGregorianCalendar().getTime()) : "");
			fieldFunktion.setText(data.getFunktion());
			fieldAhvNr.setText(data.getAhvNr());

			if (data.getImage() != null && fotoLabel.isEnabled()) {
				InputStream in = new ByteArrayInputStream(data.getImage());
				BufferedImage image;
				try {
					image = ImageIO.read(in);
					Image scaledImage = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, 0);
					ImageIcon imageIcon = new ImageIcon(scaledImage);
					fotoLabel.setIcon(imageIcon);
				} catch (IOException e) {
					fotoLabel.setIcon(noFotoIcon);
				}
			} else {
				BufferedImage image = ImageCreator.createImage(data.getImageId());
				if (image != null) {
					Image scaledImage = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, 0);
					ImageIcon imageIcon = new ImageIcon(scaledImage);
					fotoLabel.setIcon(imageIcon);
				} else {
					fotoLabel.setIcon(noFotoIcon);
				}
			}
		}

		public void clearFieldValues() {
			comboBoxGrad.setSelectedIndex(0);
			fieldName.setText("");
			fieldVorname.setText("");
			comboBoxEinheit.setSelectedIndex(0);
			fieldGeburtsdatum.setText("");
			fieldFunktion.setText("");
			fieldAhvNr.setText("");
			fotoLabel.setIcon(noFotoIcon);
		}

		@Override
		public void setEditable(boolean isEditable) {
			fotoLabel.setEnabled(isEditable);
			comboBoxGrad.setEnabled(isEditable);
			fieldName.setEditable(isEditable);
			fieldVorname.setEditable(isEditable);
			comboBoxEinheit.setEnabled(isEditable);
			fieldGeburtsdatum.setEditable(isEditable);
			fieldFunktion.setEditable(isEditable);
			fieldAhvNr.setEditable(isEditable);
			ausweisSperrenButton.setEnabled(isEditable);
			neuerAusweisButton.setEnabled(isEditable);
		}

		private byte[] convertBufferedImageToByte(BufferedImage image) {
			
			int DEST_WIDTH = 300;
			int DEST_HEIGHT = 400;
			
			if (image == null)
				return null;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] resultImageAsRawBytes;

			if ((image.getWidth() > DEST_WIDTH) || (image.getHeight() > DEST_HEIGHT)) {
				BufferedImage dest = new BufferedImage(DEST_WIDTH, DEST_HEIGHT, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = dest.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance((double) DEST_WIDTH / image.getWidth(), (double) DEST_HEIGHT
						/ image.getHeight());
				g.drawRenderedImage(image, at);
				image = dest;
			}

			try {
				ImageIO.write(image, "jpg", baos);
				baos.flush();
				resultImageAsRawBytes = baos.toByteArray();
				baos.close();
				return resultImageAsRawBytes;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return null;
			
		}
	}
}
