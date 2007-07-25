/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.visualization.Orientable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Class OrientationPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class OrientationPanel extends Box
    implements ActionListener, Orientable {
    protected ButtonGroup orientationGroup;
    protected JRadioButton orientationNorth;
    protected JRadioButton orientationSouth;
    protected JRadioButton orientationEast;
    protected JRadioButton orientationWest;
    protected JRadioButton orientationCenter;
    protected short orientation = ORIENTATION_INVALID;
    
    public OrientationPanel(boolean hasCenter) {
        super(BoxLayout.Y_AXIS);
        orientationGroup = new ButtonGroup();
        orientationNorth = new JRadioButton("North");
        orientationNorth.setAlignmentX(0.5f);
        orientationNorth.setAlignmentY(0.5f);
        orientationGroup.add(orientationNorth);
        orientationNorth.addActionListener(this);
        add(orientationNorth);
        

        Box hbox = Box.createHorizontalBox();
        orientationWest = new JRadioButton("West");
        orientationWest.setAlignmentX(0.5f);
        orientationWest.setAlignmentY(0.5f);
        orientationGroup.add(orientationWest);
        orientationWest.addActionListener(this);
        hbox.add(orientationWest);

        if (hasCenter) {
            orientationCenter = new JRadioButton("Center");
            orientationGroup.add(orientationCenter);
            orientationCenter.addActionListener(this);
            hbox.add(orientationCenter, BorderLayout.CENTER);
        }
        
        orientationEast = new JRadioButton("East");
        orientationEast.setAlignmentX(0.5f);
        orientationEast.setAlignmentY(0.5f);
        orientationGroup.add(orientationEast);
        orientationEast.addActionListener(this);
        hbox.add(orientationEast);
        add(hbox);
        
        orientationSouth = new JRadioButton("South");
        orientationSouth.setAlignmentX(0.5f);
        orientationSouth.setAlignmentY(0.5f);
        orientationGroup.add(orientationSouth);
        orientationSouth.addActionListener(this);
        add(orientationSouth);
    }
    
    public OrientationPanel() {
        this(false);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (orientationNorth.isSelected()) {
            orientation = ORIENTATION_NORTH;
        }
        else if (orientationSouth.isSelected()) {
            orientation = ORIENTATION_SOUTH;
        }
        else if (orientationEast.isSelected()) {
            orientation = ORIENTATION_EAST;
        }
        else if (orientationWest.isSelected()) {
            orientation = ORIENTATION_WEST;
        }
        else if (orientationCenter.isSelected()) {
            orientation = ORIENTATION_CENTER;
        }
    }
    
    public short getOrientation() {
        return orientation;
    }
    
    public void setOrientation(short orientation) {
        if (this.orientation == orientation) return;
        this.orientation = orientation;
        switch(orientation) {
        case ORIENTATION_NORTH:
            orientationNorth.setSelected(true);
            break;
        case ORIENTATION_SOUTH:
            orientationSouth.setSelected(true);
            break;
        case ORIENTATION_EAST:
            orientationEast.setSelected(true);
            break;
        case ORIENTATION_WEST:
            orientationWest.setSelected(true);
            break;
        case ORIENTATION_CENTER:
            if (orientationCenter != null)
                orientationCenter.setSelected(true);
            break;
        default:
            orientationGroup.getSelection().setSelected(false);
        }
    }
}
