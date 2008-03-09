package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import giny.model.*;

import java.awt.event.*;

class menuActionPressed extends AbstractAction {
	Node selectedNode;
    public menuActionPressed(String text, ImageIcon icon,
                      Node node, Integer mnemonic) {
        super(text, icon);   
        selectedNode = node;
    }
    public void actionPerformed(ActionEvent e) {
        GUI newGUI = new GUI(selectedNode);
        newGUI.loadGui(selectedNode);
    }
}
