package csplugins.mcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Gary Bader
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 ** User: Gary Bader
 ** Date: Feb 6, 2004
 ** Time: 5:00:00 PM
 ** Description
 **/
public class MCODEParameterChangeDialog extends JDialog {
	private MCODEAlgorithm alg;
	//used in scoring stage
	private boolean includeLoops;
	private int degreeCutOff;
	//used in complex finding stage
	private int maxDepthFromStart;
	private double nodeScoreCutOff;
	private boolean fluff;
	private boolean haircut;
	private double fluffNodeDensityCutOff;
	//used in directed mode
	private boolean preprocessNetwork;

	//resetable UI elements
	JCheckBox includeLoopsCheckBox;
	JCheckBox haircutCheckBox;
	JCheckBox fluffCheckBox;
	JCheckBox processCheckBox;

	public MCODEParameterChangeDialog(Frame parentFrame) {
		super(parentFrame, "MCODE Parameters", false);
		setResizable(false);

		//get MCODE algorithm instance
		alg = MCODE.getInstance().alg;
		initParams(alg);

		//main panel for dialog box
		JPanel panel = new JPanel(new BorderLayout());

		//network scoring panel
		JComponent scorePanel = new JPanel();
		includeLoopsCheckBox = new JCheckBox("Include loops", false) {
			public JToolTip createToolTip() {
				return new JMultiLineToolTip();
			}
		};
		includeLoopsCheckBox.addItemListener(new MCODEParameterChangeDialog.includeLoopsCheckBoxAction());
		includeLoopsCheckBox.setToolTipText("If checked, MCODE will include loops (self-edges) in the neighborhood\n" +
		        "density calculation.  This is expected to make a small difference in the results.");
		includeLoopsCheckBox.setSelected(includeLoops);
		scorePanel.add(includeLoopsCheckBox);

		//find complexes panel
		JComponent findPanel = new JPanel();
		haircutCheckBox = new JCheckBox("Haircut", false) {
			public JToolTip createToolTip() {
				return new JMultiLineToolTip();
			}
		};
		haircutCheckBox.addItemListener(new MCODEParameterChangeDialog.haircutCheckBoxAction());
		haircutCheckBox.setToolTipText("If checked, MCODE will give complexes a haircut\n" +
		        "(remove singly connected nodes).");
		haircutCheckBox.setSelected(haircut);
		findPanel.add(haircutCheckBox);

		fluffCheckBox = new JCheckBox("Fluff", false) {
			public JToolTip createToolTip() {
				return new JMultiLineToolTip();
			}
		};
		fluffCheckBox.addItemListener(new MCODEParameterChangeDialog.fluffCheckBoxAction());
		fluffCheckBox.setToolTipText("If checked, MCODE will fluff complexes\n" +
		        "(expand core complex one neighbour shell outwards according to fluff\n" +
		        "density threshold).");
		fluffCheckBox.setSelected(fluff);
		findPanel.add(fluffCheckBox);

		//directed mode panel
		JComponent directedModePanel = new JPanel();
		processCheckBox = new JCheckBox("Preprocess network", false) {
			public JToolTip createToolTip() {
				return new JMultiLineToolTip();
			}
		};
		processCheckBox.addItemListener(new MCODEParameterChangeDialog.processCheckBoxAction());
		processCheckBox.setToolTipText("If checked, MCODE will limit complex expansion to the\n" +
		        "direct neighborhood of the spawning node.  If unchecked, the complex will be allowed\n" +
		        "to branch out to denser regions of the network.");
		processCheckBox.setSelected(preprocessNetwork);
		directedModePanel.add(processCheckBox);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Directed Mode", null, directedModePanel, "Set parameters for directed mode");
		tabbedPane.addTab("Find Complexes", null, findPanel, "Set parameters for complex finding stage (Stage 2)");
		tabbedPane.addTab("Network Scoring", null, scorePanel, "Set parameters for scoring stage (Stage 1)");
		panel.add(tabbedPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout());

		JButton OKButton = new JButton("OK");
		OKButton.addActionListener(new MCODEParameterChangeDialog.OKAction(this));
		bottomPanel.add(OKButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new MCODEParameterChangeDialog.cancelAction(this));
		bottomPanel.add(cancelButton);

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new MCODEParameterChangeDialog.resetAction(this));
		bottomPanel.add(resetButton);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(panel);
	}

	private void initParams(MCODEAlgorithm alg) {
		//used in scoring stage
		includeLoops = alg.isIncludeLoops();
		degreeCutOff = alg.getDegreeCutOff();
		//used in complex finding stage
		maxDepthFromStart = alg.getMaxDepthFromStart();
		nodeScoreCutOff = alg.getNodeScoreCutOff();
		fluff = alg.isFluff();
		haircut = alg.isHaircut();
		fluffNodeDensityCutOff = alg.getFluffNodeDensityCutOff();
		//used in directed mode
		preprocessNetwork = alg.isPreprocessNetwork();
	}

	private void resetParams() {
		includeLoopsCheckBox.setSelected(alg.isIncludeLoops());
		haircutCheckBox.setSelected(alg.isHaircut());
		fluffCheckBox.setSelected(alg.isFluff());
		processCheckBox.setSelected(alg.isPreprocessNetwork());
	}

	private void saveParams(MCODEAlgorithm alg) {
		//used in scoring stage
		alg.setIncludeLoops(includeLoops);
		alg.setDegreeCutOff(degreeCutOff);
		//used in complex finding stage
		alg.setMaxDepthFromStart(maxDepthFromStart);
		alg.setNodeScoreCutOff(nodeScoreCutOff);
		alg.setFluff(fluff);
		alg.setHaircut(haircut);
		alg.setFluffNodeDensityCutOff(fluffNodeDensityCutOff);
		//used in directed mode
		alg.setPreprocessNetwork(preprocessNetwork);
	}

	private class resetAction extends AbstractAction {
		resetAction(JDialog popup) {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			resetParams();
		}
	}

	private class OKAction extends AbstractAction {
		private JDialog dialog;

		OKAction(JDialog popup) {
			super();
			this.dialog = popup;
		}

		public void actionPerformed(ActionEvent e) {
			saveParams(alg);
			dialog.dispose();
		}
	}

	private class cancelAction extends AbstractAction {
		private JDialog dialog;

		cancelAction(JDialog popup) {
			super();
			this.dialog = popup;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}

	private class includeLoopsCheckBoxAction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				includeLoops = false;
			} else {
				includeLoops = true;
			}
		}
	}

	private class haircutCheckBoxAction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				haircut = false;
			} else {
				haircut = true;
			}
		}
	}

	private class fluffCheckBoxAction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				fluff = false;
			} else {
				fluff = true;
			}
		}
	}

	private class processCheckBoxAction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				preprocessNetwork = false;
			} else {
				preprocessNetwork = true;
			}
		}
	}
}
