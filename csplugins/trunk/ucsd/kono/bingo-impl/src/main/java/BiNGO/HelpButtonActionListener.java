package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class which implements ActionListener and produces a help menubar.     
 **/


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * *****************************************************************
 * HelpMenuBar.java     Steven Maere & Karel Heymans (c) March 2005
 * ----------------
 * <p/>
 * Class which implements ActionListener and produces a help menubar.
 * ******************************************************************
 */


public class HelpButtonActionListener implements ActionListener {


    /*--------------------------------------------------------------
    FIELD.
    --------------------------------------------------------------*/
    private SettingsPanel settingsPanel;

    /*--------------------------------------------------------------
       CONSTRUCTOR.
    --------------------------------------------------------------*/

    public HelpButtonActionListener(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;

    }

    /*--------------------------------------------------------------
    LISTENER-PART.
    --------------------------------------------------------------*/

    /**
     * Method performed when help clicked.
     *
     * @param event event that triggers action, here clicking of the menu item.
     */
    public void actionPerformed(ActionEvent e) {

        /*JOptionPane.showMessageDialog(settingsPanel,
                                          "For help, see website \n" +
                                          "http://www.psb.ugent.be/cbd/papers/BiNGO");*/
        try {
            //Browser link = new Browser() ;
            Browser.init();
            Browser.displayURL("http://www.psb.ugent.be/cbd/papers/BiNGO");
        }
        catch (IOException ee) {
            JOptionPane.showMessageDialog(settingsPanel, "Could not open website :" + ee);
        }
    }
}
