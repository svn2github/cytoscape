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
 * * Description: Class that implements Icon and creates a toggleIcon for a 
 * * checkbox: square, but when selected square with cross in it.    
 **/

import javax.swing.*;
import java.awt.*;


/**
 * ***************************************************************
 * ToggleIcon.java:      		 Steven Maere & Karel Heymans (c) March 2005
 * ----------------
 * <p/>
 * Class that implements Icon and creates a toggleIcon for a
 * checkbox: square, but when selected square with cross in it.
 * ****************************************************************
 */


class ToggleIcon implements Icon {

    /*--------------------------------------------------------------
    FIELD.
    --------------------------------------------------------------*/

    /**
     * boolean with the state of the icon.
     */
    boolean state;

    /*-----------------------------------------------------------------
      CONSTRUCTOR.
      -----------------------------------------------------------------*/

    /**
     * Constructor which sets the state of the icon.
     *
     * @param boolean state
     */
    public ToggleIcon(boolean s) {
        state = s;
    }

    /*----------------------------------------------------------------
    PAINTCOMPONENT.
    ----------------------------------------------------------------*/

    /**
     * Paint-method for the icon.
     *
     * @param Component component
     * @param Graphics  graphics-object
     * @param x         x-coordinate
     * @param y         y-coordinate
     */
    public void paintIcon(Component c, Graphics g,
                          int x, int y) {
        int width = getIconWidth();
        int height = getIconHeight();
        g.setColor(Color.black);
        if (state) {
            g.drawRect(x, y, width, height);
            g.drawLine(x, y, x + width, y + height);
            g.drawLine(x + width, y, x, y + height);
        } else
            g.drawRect(x, y, width, height);
    }

    /*----------------------------------------------------------------
    GETTERS.
    ----------------------------------------------------------------*/

    /**
     * gets icons width.
     *
     * @return int width
     */
    public int getIconWidth() {
        return 10;
    }


    /**
     * gets icons height.
     *
     * @return int height
     */
    public int getIconHeight() {
        return 10;
    }
}

