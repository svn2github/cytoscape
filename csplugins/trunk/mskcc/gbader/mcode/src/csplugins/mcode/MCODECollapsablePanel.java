package csplugins.mcode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * The user-triggered collapsable panel containing the component (trigger) in the titled border
 */
public class MCODECollapsablePanel extends JPanel {
   
    CollapsableTitledBorder border; //includes upper left component and line type
    Border collapsedBorderLine = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    Border expandedBorderLine = null; //BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    JComponent component = new JPanel(); // displayed in the titled border
    JPanel panel; // content pane
    JPanel empty = new JPanel(); // content pane without any content for the collapsed state
    boolean collapsed; // stores curent state of the collapsable panel
    String text;//temporary

    //Expand/Collapse button
    final static int COLLAPSED = 0, EXPANDED = 1; //States
    ImageIcon[] iconArrow = createExpandAndCollapseIcon();
    JButton arrow = createArrowButton();

    public MCODECollapsablePanel () {}

    public MCODECollapsablePanel (JComponent titleComponent) {
        commonConstructor(titleComponent);
    }

    public MCODECollapsablePanel (String text) {
        this.text = text;
        JLabel titleComponent = new JLabel(text);
        commonConstructor(titleComponent);
    }

    public void commonConstructor (JComponent titleComponent) {
        //setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //setLayout(null);
        setLayout(new BorderLayout());

        component.add(titleComponent);
        component.add(arrow);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        add(component, BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);
        setCollapsed(true);

        placeTitleComponent();
    }

    public void placeTitleComponent() {
        Insets insets = this.getInsets();
        Rectangle containerRectangle = this.getBounds();
        System.out.println(text+" title " + containerRectangle.x + " " + containerRectangle.y + " " + containerRectangle.width + " " + containerRectangle.height);

        Rectangle componentRectangle = border.getComponentRect(containerRectangle, insets);//might want to change the arrow button to an image to avoid button border problems
        component.setBounds(componentRectangle);
    }

    public void setTitleComponent(JComponent newComponent) {
        remove(component);
        add(newComponent);
        border.setTitleComponent(newComponent);
        component = newComponent;
    }

    public JPanel getContentPane() {
        return panel;
    }

    public void setCollapsed(boolean collapse) {
        if (collapse) {
            //collapse the panel, remove content and set border to empty border
            remove(panel);
            arrow.setIcon(iconArrow[COLLAPSED]);
            border = new CollapsableTitledBorder(collapsedBorderLine, component);
        } else {
            //expand the panel, add content and set border to titled border
            add(panel, BorderLayout.NORTH);
            arrow.setIcon(iconArrow[EXPANDED]);
            border = new CollapsableTitledBorder(expandedBorderLine, component);
        }
        setBorder(border);
        collapsed = collapse;
        updateUI();
    }

    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        //if (transmittingAllowed && transmitter != null) {
        //    transmitter.setChildrenEnabled(enable);
        //}
    }

    /**
     *
     * @return Returns true if the panel is collapsed and false if it is expanded
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    public ImageIcon[] createExpandAndCollapseIcon () {
        ImageIcon[] iconArrow = new ImageIcon[2];
        URL iconURL;

        iconURL = this.getClass().getResource("resources/arrow_collapsed.gif");
        if (iconURL != null) {
            iconArrow[COLLAPSED] = new ImageIcon(iconURL);
        }
        iconURL = this.getClass().getResource("resources/arrow_expanded.gif");
        if (iconURL != null) {
            iconArrow[EXPANDED] = new ImageIcon(iconURL);
        }
        return iconArrow;
    }

    public JButton createArrowButton () {
        JButton button = new JButton(iconArrow[COLLAPSED]);
        button.addActionListener(new MCODECollapsablePanel.ExpandAndCollapseAction());
        button.setBorderPainted(false);
        return button;
    }

    /**
     * Handles expanding of hidden content
     */
    private class ExpandAndCollapseAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            setCollapsed(!isCollapsed());
        }
    }

    private class CollapsableTitledBorder extends TitledBorder {
        JComponent component;

        public CollapsableTitledBorder(JComponent component) {
            this(null, component, LEFT, TOP);
        }

        public CollapsableTitledBorder(Border border) {
            this(border, null, LEFT, TOP);
        }

        public CollapsableTitledBorder(Border border, JComponent component) {
            this(border, component, LEFT, TOP);
        }

        public CollapsableTitledBorder(Border border, JComponent component, int titleJustification, int titlePosition) {
            //TitledBorder needs border, title, justification, position, font, and color
            super(border, null, titleJustification, titlePosition, null, null);
            this.component = component;
            if (border == null) {
                this.border = super.getBorder();
            }
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Rectangle borderR = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING, width - (EDGE_SPACING * 2), height - (EDGE_SPACING * 2));
            Insets borderInsets;
            if (border != null) {
                borderInsets = border.getBorderInsets(c);
            } else {
                borderInsets = new Insets(0, 0, 0, 0);
            }

            Rectangle rect = new Rectangle(x, y, width, height);
            Insets insets = getBorderInsets(c);
            Rectangle compR = getComponentRect(rect, insets);
            int diff;
            switch (titlePosition) {
                case ABOVE_TOP:
                    diff = compR.height + TEXT_SPACING;
                    borderR.y += diff;
                    borderR.height -= diff;
                    break;
                case TOP:
                case DEFAULT_POSITION:
                    diff = insets.top / 2 - borderInsets.top - EDGE_SPACING;
                    borderR.y += diff;
                    borderR.height -= diff;
                    break;
                case BELOW_TOP:
                case ABOVE_BOTTOM:
                    break;
                case BOTTOM:
                    diff = insets.bottom / 2 - borderInsets.bottom - EDGE_SPACING;
                    borderR.height -= diff;
                    break;
                case BELOW_BOTTOM:
                    diff = compR.height + TEXT_SPACING;
                    borderR.height -= diff;
                    break;
            }
            border.paintBorder(c, g, borderR.x, borderR.y, borderR.width, borderR.height);
            Color col = g.getColor();
            g.setColor(c.getBackground());
            g.fillRect(compR.x, compR.y, compR.width, compR.height);
            g.setColor(col);
            component.repaint();
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            Insets borderInsets;
            if (border != null) {
                borderInsets = border.getBorderInsets(c);
            } else {
                borderInsets = new Insets(0, 0, 0, 0);
            }
            insets.top = EDGE_SPACING + TEXT_SPACING + borderInsets.top;
            insets.right = EDGE_SPACING + TEXT_SPACING + borderInsets.right;
            insets.bottom = EDGE_SPACING + TEXT_SPACING + borderInsets.bottom;
            insets.left = EDGE_SPACING + TEXT_SPACING + borderInsets.left;

            if (c == null || component == null) {
                return insets;
            }

            int compHeight = component.getPreferredSize().height /2; //TODO: This determines how far the content is from the top

            switch (titlePosition) {
                case ABOVE_TOP:
                    insets.top += compHeight + TEXT_SPACING;
                    break;
                case TOP:
                case DEFAULT_POSITION:
                    insets.top += Math.max(compHeight, borderInsets.top);// - borderInsets.top;
                    break;
                case BELOW_TOP:
                    insets.top += compHeight + TEXT_SPACING;
                    break;
                case ABOVE_BOTTOM:
                    insets.bottom += compHeight + TEXT_SPACING;
                    break;
                case BOTTOM:
                    insets.bottom += Math.max(compHeight, borderInsets.bottom) - borderInsets.bottom;
                    break;
                case BELOW_BOTTOM:
                    insets.bottom += compHeight + TEXT_SPACING;
                    break;
            }
            return insets;
        }

        public JComponent getTitleComponent() {
            return component;
        }

        public void setTitleComponent(JComponent component) {
            this.component = component;
        }

        public Rectangle getComponentRect(Rectangle rect, Insets borderInsets) {
            Dimension compD = component.getPreferredSize();
            Rectangle compR = new Rectangle(0, 0, compD.width, compD.height);
            switch (titlePosition) {
                case ABOVE_TOP:
                    compR.y = EDGE_SPACING;
                    break;
                case TOP:
                case DEFAULT_POSITION:
                    compR.y = EDGE_SPACING + (borderInsets.top - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
                    break;
                case BELOW_TOP:
                    compR.y = borderInsets.top - compD.height - TEXT_SPACING;
                    break;
                case ABOVE_BOTTOM:
                    compR.y = rect.height - borderInsets.bottom + TEXT_SPACING;
                    break;
                case BOTTOM:
                    compR.y = rect.height - borderInsets.bottom + TEXT_SPACING + (borderInsets.bottom - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
                    break;
                case BELOW_BOTTOM:
                    compR.y = rect.height - compD.height - EDGE_SPACING;
                    break;
            }
            switch (titleJustification) {
                case LEFT:
                case DEFAULT_JUSTIFICATION:
                    compR.x = TEXT_INSET_H + borderInsets.left;
                    break;
                case RIGHT:
                    compR.x = rect.width - borderInsets.right - TEXT_INSET_H - compR.width;
                    break;
                case CENTER:
                    compR.x = (rect.width - compR.width) / 2;
                    break;
            }
            return compR;
        }
    }
}
