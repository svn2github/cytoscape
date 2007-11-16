package org.systemsbiology.cytoscape.dialog;

import org.systemsbiology.cytoscape.AttrSelectAction;

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cytoscape.Cytoscape;

/*
 * Creates dialog box to choose attributes for broadcast
 * TODO: currently ok/cancel do not dispose of the dialog box as they should
 */
public class CyAttrDialog extends JDialog
	{
	public static final int SINGLE_SELECT = 1;
	public static final int MULTIPLE_SELECT = 2;
	public static final String DEFAULT_NODE_ID = "ID";
	private String[] allAttrList;
	private String[] preSelectAttrList;
	private AbstractButton[] attrCheckBoxes;
	private ButtonGroup radioButtonGroup;
	private String dialogText;
	private int selectMode; // SINGLE_SELECT or MULTIPLE_SELECT
	private AttrSelectAction selectOk;

	public CyAttrDialog(String[] attrList, AttrSelectAction action, int mode)
		{
		super(Cytoscape.getDesktop(), "Confirm Attributes for Broadcast", false);
		allAttrList = attrList;
		selectMode = mode;
		selectOk = action;
		this.setTitle("Select Attributes");
		/*
		 * SINGLE_SELECT: construct list of radio buttons for attributes
		 * MULTIPLE_SELECT: construct list of check boxes for attributes
		 */
		if (mode == SINGLE_SELECT)
			{
			attrCheckBoxes = new JRadioButton[attrList.length + 1];
			// need to put radio buttons in ButtonGroup to activate mutually exclusive
			// selection
			radioButtonGroup = new ButtonGroup();
			// add ID (this should be pre-selected by the caller)
			JRadioButton defaultButton = new JRadioButton(DEFAULT_NODE_ID);
			attrCheckBoxes[0] = defaultButton;
			radioButtonGroup.add(defaultButton);
			for (int i = 0; i < attrList.length; i++)
				{
				JRadioButton rb = new JRadioButton(attrList[i]);
				attrCheckBoxes[i + 1] = rb;
				radioButtonGroup.add(rb);
				}
			}
		else
			{
			attrCheckBoxes = new JCheckBox[attrList.length];
			for (int i = 0; i < attrList.length; i++)
				{
				JCheckBox cb = new JCheckBox(attrList[i]);
				attrCheckBoxes[i] = cb;
				}
			}
		}

	/*
	 * instruction text for dialog
	 */
	public void setDialogText(String text)
		{
		dialogText = text;
		}

	/*
	 * mark buttons corresponding to attributes in 'preselect' as selected the
	 * 'preselect' array should contain only one element for SINGLE_SELECT mode
	 */
	public void preSelectCheckBox(String[] preselect)
		{
		for (int i = 0; i < preselect.length; i++)
			{
			String matchStr = preselect[i];
			for (int j = 0; j < attrCheckBoxes.length; j++)
				{
				if (attrCheckBoxes[j].getText().equals(matchStr))
					{
					// mark as preselected if check box text matches
					attrCheckBoxes[j].setSelected(true);
					break;
					}
				}
			}
		}

	/*
	 * main routine that puts dialog GUI together
	 */
	public void buildDialogWin()
		{
		JPanel panel = new JPanel(new BorderLayout());
		// panel.setPreferredSize(new Dimension(400, 250));
		/*
		 * Text Area
		 */
		Box northBox = Box.createVerticalBox();
		northBox.add(Box.createVerticalStrut(10));
		northBox.add(Box.createHorizontalStrut(10));
		northBox.setBorder(BorderFactory.createEmptyBorder(2, 9, 4, 9));
		JTextArea text = new JTextArea();
		text.setText(dialogText);
		text.setEditable(false);
		text.setDragEnabled(false);
		text.setBackground(panel.getBackground());
		// add to north region of main panel
		northBox.add(text);
		northBox.add(Box.createVerticalStrut(10));
		northBox.add(Box.createHorizontalStrut(10));
		panel.add(northBox, BorderLayout.NORTH);
		/*
		 * CheckBoxes in ScrollPane
		 */
		JPanel attrPanel = new JPanel(new GridLayout(0, 1));
		attrPanel.setBackground(Color.WHITE);
		// add checkboxes to panel
		for (int i = 0; i < attrCheckBoxes.length; i++)
			attrPanel.add(attrCheckBoxes[i]);
		// add panel to scrollpane
		JScrollPane scrollPane = new JScrollPane(attrPanel);
		scrollPane.setPreferredSize(new Dimension(300, 120));
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBackground(Color.WHITE);
		// add to center region of main panel
		Box centerBox = Box.createHorizontalBox();
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(Box.createHorizontalGlue());
		centerBox.add(scrollPane);
		centerBox.add(Box.createHorizontalGlue());
		centerBox.add(Box.createVerticalStrut(10));
		panel.add(centerBox, BorderLayout.CENTER);
		/*
		 * Ok and Cancel Buttons add SelectAll and ClearAll Buttons if selectMode is
		 * MULTIPLE_SELECT
		 */
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		JButton selectAllButton = new JButton("Select All");
		JButton clearAllButton = new JButton("Clear All");
		okButton.addActionListener(new okAction());
		selectAllButton.addActionListener(new selectAllAction());
		clearAllButton.addActionListener(new clearAllAction());
		cancelButton.addActionListener(new cancelAction());
		// add to south region of main panel
		Box southBox = Box.createHorizontalBox();
		southBox.setBorder(BorderFactory.createEmptyBorder(2, 9, 4, 9));
		southBox.add(Box.createVerticalStrut(10));
		if (selectMode == MULTIPLE_SELECT)
			{
			southBox.add(Box.createHorizontalGlue()); // used to line up the buttons
			southBox.add(selectAllButton);
			southBox.add(Box.createHorizontalGlue()); // used to line up the buttons
			southBox.add(clearAllButton);
			}
		southBox.add(Box.createHorizontalGlue()); // used to line up the buttons
		southBox.add(okButton);
		southBox.add(Box.createHorizontalGlue()); // used to line up the buttons
		southBox.add(cancelButton);
		southBox.add(Box.createHorizontalGlue()); // used to line up the buttons
		southBox.add(Box.createVerticalStrut(10));
		panel.add(southBox, BorderLayout.SOUTH);
		/*
		 * pack and ready to go!
		 */
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.setVisible(true);
		}
	/*
	 * inner class: ActionListener for Cancel button
	 */
	private class cancelAction implements ActionListener
		{
		public void actionPerformed(ActionEvent event)
			{
			// exit and do nothing
			CyAttrDialog.this.dispose();
			}
		}
	/*
	 * inner class: ActionListener for SelectAll button
	 */
	private class selectAllAction implements ActionListener
		{
		public void actionPerformed(ActionEvent event)
			{
			for (int i = 0; i < attrCheckBoxes.length; i++)
				attrCheckBoxes[i].setSelected(true);
			}
		}
	/*
	 * inner class: ActionListener for ClearAll button
	 */
	private class clearAllAction implements ActionListener
		{
		public void actionPerformed(ActionEvent event)
			{
			for (int i = 0; i < attrCheckBoxes.length; i++)
				attrCheckBoxes[i].setSelected(false);
			}
		}
	/*
	 * inner class: ActionListener for Ok button
	 */
	private class okAction implements ActionListener
		{
		public void actionPerformed(ActionEvent event)
			{
			ArrayList selectAttr = new ArrayList();
			// extract all selected attributes from check boxes
			for (int i = 0; i < attrCheckBoxes.length; i++)
				{
				if (attrCheckBoxes[i].isSelected())
					{
					selectAttr.add(attrCheckBoxes[i].getText());
					}
				}
			String[] selectAttrName = new String[selectAttr.size()];
			selectAttr.toArray(selectAttrName);
			// callback
			selectOk.takeAction(selectAttrName);
			// close popup window
			CyAttrDialog.this.dispose();
			}
		}
	}
