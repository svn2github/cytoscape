package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
* *
* * Authors : Steven Maere
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
* * Authors: Steven Maere
* * Date: Mar.25.2005
* * Description: Class that extends JPanel ; makes color scale panel
**/

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * ***************************************************************
 * ColorPanel.java:       Steven Maere (c) March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel ; makes color scale panel
 * <p/>
 * ******************************************************************
 */

public class ColorPanel extends JPanel {

    /**
     * the height of the panel
     */
    private final int DIM_HEIGHT = 75;
    /**
     * the width of the panel
     */
    private final int DIM_WIDTH = 250;

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/
    private static String alpha1;
    private static String alpha2;
    private static Color color1;
    private static Color color2;

    /*--------------------------------------------------------------
     Constructor.
    --------------------------------------------------------------*/

    public ColorPanel(String alpha1, String alpha2, Color color1, Color color2) {
        super();
        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.color1 = color1;
        this.color2 = color2;
        setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        setOpaque(false);
        setBackground(Color.WHITE);
        //create border.
        setBorder(BorderFactory.createEtchedBorder());
    }

    /*----------------------------------------------------------------
    PAINT.
    ----------------------------------------------------------------*/


    public void paint(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;
        Point2D.Float p1 = new Point2D.Float(75.f, 30.f);  //Gradient line start
        Point2D.Float p2 = new Point2D.Float(175.f, 30.f);  //Gradient line end
        float width = 150;
        float height = 25;
        GradientPaint g1 = new GradientPaint(p1, color1, p2, color2, false); //Acyclic gradient
        Rectangle2D.Float rect1 = new Rectangle2D.Float(p1.x - 25, p1.y, width, height);
        g2D.setPaint(g1);
        g2D.fill(rect1);
        g2D.setPaint(Color.BLACK);
        g2D.draw(rect1);
        g2D.drawString(alpha1, p1.x - 50, p1.y - 12);
        g2D.drawString("< " + alpha2, p2.x - 10, p2.y - 12);
    }
}
