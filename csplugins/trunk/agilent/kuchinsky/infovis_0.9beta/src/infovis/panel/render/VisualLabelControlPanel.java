/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import infovis.visualization.Orientable;
import infovis.visualization.render.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import say.swing.JFontChooser;

/**
 * Class VisualLabelControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualLabel
 */
public class VisualLabelControlPanel extends AbstractVisualColumnPanel
    implements ActionListener, ChangeListener {
    protected JCheckBox    labelItems;
    protected JCheckBox    clipItems;
    protected JCheckBox    outlineItems;
    protected JSlider      justification;
    protected JFontChooser fontChooser;
    protected JButton      fontButton;
    protected JRadioButton orientationHorizontal;
    protected JRadioButton orientationVertical;
    protected JRadioButton orientationAutomatic;
    protected DefaultVisualLabel  vcd;
    protected VisualLabel   vl;

    public VisualLabelControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vl = (VisualLabel)vc; 
        if (vc instanceof DefaultVisualLabel) {
            vcd = (DefaultVisualLabel) vc;
        }
        createCombo();

        Box checkBox = Box.createHorizontalBox();
        if (vcd != null) {
            labelItems = new JCheckBox("Label all items");
            labelItems.addChangeListener(this);
            checkBox.add(labelItems);
    
            clipItems = new JCheckBox("Clip");
            clipItems.addChangeListener(this);
            checkBox.add(clipItems);
            
            outlineItems = new JCheckBox("Outline");
            outlineItems.addChangeListener(this);
            checkBox.add(outlineItems);
        }
        
        fontChooser = new JFontChooser();
        fontButton = new JButton("Font");
        fontButton.addActionListener(this);
        checkBox.add(fontButton);
        
        checkBox.setAlignmentX(LEFT_ALIGNMENT);
        add(checkBox);
        
        if (vcd != null) {
            Box orientationBox = Box.createHorizontalBox();
            ButtonGroup orientationGroup = new ButtonGroup();
            orientationHorizontal = new JRadioButton("Horizontal");
            orientationHorizontal.addActionListener(this);
            orientationGroup.add(orientationHorizontal);
            orientationBox.add(orientationHorizontal);
            orientationAutomatic = new JRadioButton("Automatic");
            orientationAutomatic.addActionListener(this);
            orientationGroup.add(orientationAutomatic);
            orientationBox.add(orientationAutomatic);
            orientationVertical = new JRadioButton("Vertical");
            orientationVertical.addActionListener(this);
            orientationGroup.add(orientationVertical);
            orientationBox.add(orientationVertical);
            orientationBox.setAlignmentX(LEFT_ALIGNMENT);
            orientationBox.setBorder(
                    BorderFactory.createTitledBorder("Orientation"));
            add(orientationBox);
            
            justification = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            justification.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    vcd.setJustification(justification.getValue() / 100.0f);
                }
            });
            justification.setAlignmentX(LEFT_ALIGNMENT);
            justification.setMajorTickSpacing(10);
            justification.setPaintLabels(true);
            justification.setPaintTicks(true);
            justification.setName("Justification");
            add(justification);
        }

        // orientationPanel = new OrientationPanel(true);
        // orientationPanel.setAlignmentX(LEFT_ALIGNMENT);
        // add(orientationPanel);

        update();
    }

    public void update() {
        super.update();
        fontButton.setFont(vl.getFont());
        fontChooser.setSelectedFont(vl.getFont());
        if (vcd != null) {
            labelItems.setSelected(vcd.isShowingLabel());
            clipItems.setSelected(vcd.isClipped());
            justification.setValue((int) (vcd.getJustification() * 100));
            switch(vcd.getOrientation()) {
            case Orientable.ORIENTATION_NORTH:
            case Orientable.ORIENTATION_SOUTH:
                orientationVertical.setSelected(true);
                break;
            case Orientable.ORIENTATION_INVALID:
                orientationAutomatic.setSelected(true);
                break;
            default:
                orientationHorizontal.setSelected(true);
                break;
            }
            outlineItems.setSelected(vcd.isOutlined());
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        if (vcd != null) {
            if (e.getSource() == labelItems) {
                vcd.setShowingLabel(labelItems.isSelected());
            }
            else if (e.getSource() == clipItems) {
                vcd.setClipped(clipItems.isSelected());
            }
            else if (e.getSource() == outlineItems) {
                vcd.setOutlined(outlineItems.isSelected());
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (vcd != null && e.getSource() instanceof JRadioButton) {
            if (orientationHorizontal.isSelected()) {
                vcd.setOrientation(Orientable.ORIENTATION_WEST);
            }
            else if (orientationVertical.isSelected()) {
                vcd.setOrientation(Orientable.ORIENTATION_NORTH);
            }
            else if (orientationAutomatic.isSelected()) {
                vcd.setOrientation(Orientable.ORIENTATION_INVALID);
            }
        }
        else if (e.getSource() == fontButton) {
            int result = fontChooser.showDialog(fontButton);
            if (result == JFontChooser.OK_OPTION) {
                Font font = fontChooser.getSelectedFont();
                fontButton.setFont(font);
                vcd.setFont(font);
            }
        }
    }
}
