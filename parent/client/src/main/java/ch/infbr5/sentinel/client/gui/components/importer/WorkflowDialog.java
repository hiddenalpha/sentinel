package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import net.miginfocom.swing.MigLayout;

public class WorkflowDialog {

	private JDialog dialog;
	
	private List<WorkflowStep> workflowSteps = new ArrayList<>();
	
	private List<JPanel> panelsWorkflowSteps = new ArrayList<>();
	
	private JPanel panelWorkflowSteps;
	
	private JPanel panelNavigation;
	
	private JPanel panelCurrentStepInfo;
	
	private JButton btnPrev;
	
	private JButton btnNext;
	
	private JButton btnAbort;
	
	private JScrollPane scrollPane;
	
	private int indexCurrentWorkflowStep = 0;
	
	private JLabel currentStepUserInfoLabel;
	
	public void addWorkflowStep(WorkflowStep step) {
		workflowSteps.add(step);
	}
	
	private void createDialog() {
		dialog.setTitle("Personendaten importieren");
		dialog.setLayout(new MigLayout());
		dialog.setModal(true);
		dialog.setSize(720, 600);
		dialog.setResizable(false);
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getCurrentStep().abort();
			}
		});
	}
	
	private void createPanelWorkflowSteps() {
		panelWorkflowSteps = new JPanel(new MigLayout());
		int i = 1;
		for (WorkflowStep step : workflowSteps) {
			JLabel lbl = new JLabel(String.valueOf(i) + ". " + step.getName());
			JPanel panel = new JPanel(new MigLayout());
			panel.setPreferredSize(new Dimension(200, 40));
			panel.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.add(lbl, "gaptop 1");
			panelsWorkflowSteps.add(panel);
			panelWorkflowSteps.add(panel, "wrap");
			i++;
		}
		CompoundBorder bordering = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Ablauf"),BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelWorkflowSteps.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), bordering));
	}
	
	private void createPanelCurrentStepInfo() {
		currentStepUserInfoLabel = new JLabel();
		panelCurrentStepInfo = new JPanel(new MigLayout());
		panelCurrentStepInfo.setPreferredSize(new Dimension(230, 300));
		panelCurrentStepInfo.setBackground(new Color(224, 224, 224));
		panelCurrentStepInfo.add(currentStepUserInfoLabel, "gaptop 3");
		
		CompoundBorder bordering = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Informationen"),BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelCurrentStepInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), bordering));
	}
	
	private void createPanelNavigation() {
		panelNavigation = new JPanel(new MigLayout("fillx,insets 10"));
		btnPrev = new JButton("Zur�ck");
		btnPrev.setEnabled(false);
		btnPrev.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				goToPreviousStep();
			}
		});
		btnNext = new JButton("Weiter");
		btnNext.setEnabled(false);
		btnNext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				goToNextStep();
			}
		});
		btnAbort = new JButton("Abbrechen");
		btnAbort.setEnabled(true);
		btnAbort.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentStep().abort();
				dialog.setVisible(false);
			}
		});
		panelNavigation.add(btnPrev, "split,right");
		panelNavigation.add(btnNext, "");
		panelNavigation.add(btnAbort, "");
	}
	
	public void show() {
		dialog = new JDialog();
		
		createDialog();
		createPanelWorkflowSteps();
		createPanelCurrentStepInfo();
		createPanelNavigation();
		
		dialog.add(panelWorkflowSteps, "cell 0 0, gap 10");
		dialog.add(panelCurrentStepInfo, "cell 0 1, gap 10");
		addCurrentStepPanelToView();
		dialog.add(panelNavigation, "dock south");
		
		updatePanelWorkflowSteps();
		
		dialog.setVisible(true);
	}
	
	private void addCurrentStepPanelToView() {
		String currentStepNumber = String.valueOf(indexCurrentWorkflowStep + 1);
		Border bordering = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Schritt " + currentStepNumber + ": " + getCurrentStep().getName()),BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel panel = getCurrentStep().getPanel();
		panel.setPreferredSize(new Dimension(420, (int) panel.getPreferredSize().getHeight()));
		
		scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(500, (int) scrollPane.getPreferredSize().getHeight()));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), bordering));
		dialog.add(scrollPane, "cell 1 0 1 2, aligny top, height 100%");
	}
	
	private void removeCurrentStepPanelFromView() {
		if (scrollPane != null) {
			dialog.remove(scrollPane);
		}
	}
	
	private WorkflowStep getCurrentStep() {
		return workflowSteps.get(indexCurrentWorkflowStep);
	}
	
	private void loadNextStep() {
		++indexCurrentWorkflowStep;
	}
	
	private void loadPreviousStep() {
		--indexCurrentWorkflowStep;
	}
	
	private boolean hasNextStep() {
		if (indexCurrentWorkflowStep == workflowSteps.size() - 1) {
			return false;
		} else {
			return true;
		}
	}
	
	private void finishDialog() {
		dialog.setVisible(false);
	}
	
	private void finishCurrentStepReturn() {
		WorkflowStep currentStep = getCurrentStep();
		currentStep.finishReturn();
		removeCurrentStepPanelFromView();
	}
	
	private void finishCurrentStepNext() {
		WorkflowStep currentStep = getCurrentStep();
		currentStep.finishNext();
		removeCurrentStepPanelFromView();
	}
	
	private void initateCurrentStep() {
		// Navigation
		if (indexCurrentWorkflowStep == 0) {
			btnPrev.setEnabled(false);
		} else {
			btnPrev.setEnabled(true);
		}
		btnNext.setEnabled(false);
				
		// Step starten
		getCurrentStep().init();
		addCurrentStepPanelToView();
		updatePanelWorkflowSteps();
		dialog.validate();
		dialog.repaint();
	}
	
	private void goToPreviousStep() {
		// Aktueller Step beenden
		finishCurrentStepReturn();
		
		// Load prev step
		loadPreviousStep();
		
		// Nächster Step starten
		initateCurrentStep();
	}
	
	private void goToNextStep() {
		// Aktueller Step beenden
		finishCurrentStepNext();

		if (hasNextStep()) {
			// Load Next step
			loadNextStep();
		
			// Nächster Step starten
			initateCurrentStep();
		} else {
			finishDialog();
		}
	}
	
	private void updatePanelWorkflowSteps() {
		for (JPanel panel : panelsWorkflowSteps) {
			panel.setBackground(null);
		}
		JPanel panel = getCurrentWorkflowStepPanel();
		panel.setBackground(Color.yellow);
		
		changeToSmaller(currentStepUserInfoLabel);
		currentStepUserInfoLabel.setText("<html>" + getCurrentStep().getUserInfo() + "</html>");
	}
	
	private JPanel getCurrentWorkflowStepPanel() {
		return panelsWorkflowSteps.get(indexCurrentWorkflowStep);
	}
	
	private void changeToSmaller(JLabel label) {
		Font font = label.getFont();
		Font boldFont = new Font(font.getFontName(), font.getStyle(), 12);
		label.setFont(boldFont);
	}
	
	public WorkflowInterceptor getWorkflowInterceptor() {
		return new WorkflowInterceptor() {
			
			@Override
			public void activateNext() {
				btnNext.setEnabled(true);
			}

			@Override
			public void deactivateNext() {
				btnNext.setEnabled(false);
			}
		};
	}
	
}
