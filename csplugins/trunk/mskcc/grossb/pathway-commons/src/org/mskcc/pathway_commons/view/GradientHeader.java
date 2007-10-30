package org.mskcc.pathway_commons.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Gradient Header Panel.
 *
 * @author Ethan Cerami
 */
public class GradientHeader extends JPanel {

    /**
     * Constructor.
     * @param header Header Title.
     */
    public GradientHeader(String header) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(header);
        label.setBorder(new EmptyBorder(0,0,0,0));
        Font font = label.getFont();
        Font newFont = new Font (font.getFamily(), Font.BOLD, font.getSize()+1);
        label.setFont(newFont);
        label.setForeground(new Color(102,51,51));
        label.setOpaque(false);
        this.add (label);
        this.setBorder (new EmptyBorder(0,0,0,0));
    }

    /**
     * Override paintComponent() with gradient painting code.
     * @param graphics Graphics Object.
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;

        //  Create the gradient
        GradientPaint p = new GradientPaint (0,0, new Color (0xFFFFFF),
                0, getHeight(), new Color (0xC8D2DE));

        //  Save the state
        Paint oldPaint = g2.getPaint();

        //  Paint the background
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //  Restore the state
        g2.setPaint(oldPaint);
    }
}
