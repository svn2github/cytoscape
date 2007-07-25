/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import infovis.Visualization;
import infovis.visualization.ColorVisualization;
import infovis.visualization.render.VisualColor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class DefaultColorVisualizationControlPanel
	extends ColorVisualizationControlPanel {
    JButton button;
    
    /**
     * Creates a new OrderedColorControlPanel object.
     *
     * @param visualization the Visualization
     */
    public DefaultColorVisualizationControlPanel(Visualization visualization) {
        super(visualization);
        button = new JButton("Default Color");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color c = chooseColor(button.getBackground());
                    if (! c.equals(button.getBackground())) {
                        VisualColor vc = VisualColor.get(getVisualization());
                        vc.setDefaultColor(c);
                        update();
                        getVisualization().repaint();
                    }
                }
            });
        add(button);

        update();
    }

    
    public void update() {
        VisualColor vc = VisualColor.get(visualization);
        button.setBackground(vc.getDefaultColor());
        button.setForeground(ColorVisualization.colorComplement(button.getBackground()));
        
    }

}
