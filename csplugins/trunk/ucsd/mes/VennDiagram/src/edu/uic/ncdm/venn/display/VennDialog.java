/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package edu.uic.ncdm.venn.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;

public class VennDialog extends JDialog implements ActionListener, ItemListener {


	private String attrName;
	private List<CyNetwork> networks;
	private boolean printIntersection;

    private JList netList;
    private JList attrList;
	private JCheckBox printIntCheck;

	public String getAttributeName() {
		return attrName;
	}
	public List<CyNetwork> getNetworks() {
		return networks;
	}

	public boolean printIntersection() {
		return printIntersection;
	}

	public static VennDialog showDialog() {
		// get network names
		Set<CyNetwork> allNetworks = Cytoscape.getNetworkSet();
		String[] allNames = new String[allNetworks.size()];
		int i = 0;
		for ( CyNetwork n : allNetworks )
			allNames[i++] = n.getTitle();

		Arrays.sort(allNames);

		// get visible attributes and make sure that canonicalName is
		// always the first attribute listed (i.e. the default)
		List<String> visibleAttrs = new ArrayList<String>();
		CyAttributes attrs = Cytoscape.getNodeAttributes();
		for ( String at : attrs.getAttributeNames() ) 
			if ( attrs.getUserVisible(at) && !at.equals("canonicalName") )
				visibleAttrs.add( at );

		Collections.sort(visibleAttrs);
		visibleAttrs.add(0,"canonicalName");

		return new VennDialog( allNames, allNames[0], visibleAttrs.toArray(), "canonicalName");
	}

    private VennDialog(Object[] possibleValues,
						String initialValue,
                       Object[] possibleKeys,
					   String initialKey) {
        super(Cytoscape.getDesktop(), "Venn/Euler Diagram Creator", true);

		networks = new ArrayList<CyNetwork>();
		attrName = "canonicalName";
		printIntersection = true;

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton selectButton = new JButton("Select");
        selectButton.setActionCommand("Select");
        selectButton.addActionListener(this);
        getRootPane().setDefaultButton(selectButton);

        // network list
        netList = new JList(possibleValues); 
        netList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        netList.setVisibleRowCount(-1);
        JScrollPane netListScroller = new JScrollPane(netList);
        netListScroller.setPreferredSize(new Dimension(450, 120));
        netListScroller.setAlignmentX(LEFT_ALIGNMENT);


        // attribute list
        attrList = new JList(possibleKeys); 
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attrList.setVisibleRowCount(-1);
        JScrollPane attrListScroller = new JScrollPane(attrList);
        attrListScroller.setPreferredSize(new Dimension(450, 120));
        attrListScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Select mulitple networks to create Venn/Euler Diagram:");
        label.setLabelFor(netList);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(netListScroller);

        listPane.add(Box.createRigidArea(new Dimension(0,20)));

        JLabel attrLabel = new JLabel("Select attribute used to determine set membership:");
		listPane.add(attrLabel);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(attrListScroller);

		// print intersections checkbox
		printIntCheck = new JCheckBox("Print the set counts on the intersections?");
		printIntCheck.setSelected(printIntersection);
		printIntCheck.addItemListener(this);
		JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.PAGE_AXIS));
		checkPanel.add(printIntCheck);
        checkPanel.setPreferredSize(new Dimension(450, 50));
		listPane.add(checkPanel); 

        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(selectButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        netList.setSelectedValue(initialValue, true);
        attrList.setSelectedValue(initialKey, true);
        pack();
		setVisible(true);
    }

    //Handle check box 
    public void itemStateChanged(ItemEvent e) {
		if ( e.getSource() == printIntCheck ) {
			printIntersection = printIntCheck.isSelected();
		}
	}

    //Handle clicks on the Select and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Select".equals(e.getActionCommand())) {
			networks.clear();
			for ( Object netName : netList.getSelectedValues() ) 
				networks.add( Cytoscape.getNetwork( netName.toString() ) );

			if ( networks.size() < 2 ) {
				boolean isMac = System.getProperty("os.name").startsWith("Mac");
				JOptionPane.showMessageDialog(this, "You must select more than one network to create a Venn/Euler diagram!\nEither " + (isMac ? "CMD-Click" : "CTRL-Click") + " to select individual networks or SHIFT-Click to select ranges.", "ERROR!", JOptionPane.ERROR_MESSAGE);

				// don't close window!
				return;
			}
				
			attrName = (String)(attrList.getSelectedValue());
        }
        setVisible(false);
    }
}

