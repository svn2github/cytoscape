package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


import java.awt.event.*;

class menuActionPressed extends AbstractAction {
    public menuActionPressed(String text, ImageIcon icon,
                      String desc, Integer mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    public void actionPerformed(ActionEvent e) {
        GUI yay = new GUI();
        yay.loadGui();
    }
}
