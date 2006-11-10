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
    JComponent component;
    JPanel panel;

    //Expand/Collapse buttoN
    final static int COLLAPSED = 0, EXPANDED = 1; //States
    ImageIcon[] iconArrow = createExpandAndCollapseIcon();
    JButton arrow = createArrowButton();

    public MCODECollapsablePanel () {}

    public MCODECollapsablePanel (JComponent titleComponent) {
        //setLayout(new GridLayout(1,1));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //setLayout(null);
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleComponent);
        titlePanel.add(arrow);
        this.component = titlePanel;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(component);
        add(panel);
        setCollapsed(true);
    }

    public MCODECollapsablePanel (String text) {
        //setLayout(new GridLayout(0,1));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //setLayout(null);
        JLabel title = new JLabel(text);
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        titlePanel.add(arrow);
        this.component = titlePanel;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(component);
        add(panel);
        setCollapsed(true);
    }

    public void doLayout1() {
        Insets insets = getInsets();
        //insets = new Insets(40, 4, 4, 4);
        //System.out.println("insets:"+insets.top+" "+insets.bottom+" "+insets.left+" "+insets.right);
        Rectangle rect = getBounds();
        //Rectangle rect = panel.getBounds();
        System.out.println("get bounds: rect x:"+rect.width+" y:"+rect.height);
        rect.x = 0;
        rect.y = 0;

        Rectangle componentRectangle = border.getComponentRect(rect, insets);
        component.setBounds(componentRectangle);
        rect.x += insets.left;
        rect.y += insets.top;
        rect.width -= insets.left + insets.right;
        rect.height -= insets.top + insets.bottom;
        panel.setBounds(rect);
        System.out.println("after: rect x:"+rect.width+" y:"+rect.height);
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

    public void setCollapsed(boolean collapsed) {
        if (collapsed) {
            //collapse the panel, remove content and set border to empty border
            getContentPane().setVisible(false);
            arrow.setIcon(iconArrow[COLLAPSED]);
            border = new CollapsableTitledBorder(collapsedBorderLine, component);
            setBorder(border);
        } else {
            //expand the panel, add content and set border to titled border
            getContentPane().setVisible(true);
            arrow.setIcon(iconArrow[EXPANDED]);
            border = new CollapsableTitledBorder(expandedBorderLine, component);
            setBorder(border);
        }
    }

    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        //if (transmittingAllowed && transmitter != null) {
        //    transmitter.setChildrenEnabled(enable);
        //}
    }

    /**
     *
     * @return Returns whether the panel is collapsed or expanded
     */
    public boolean isCollapsed() {
        return !getContentPane().isVisible();
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

        public void paintBorder1(Component c, Graphics g, int x, int y, int width,
            int height) {
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

        public Insets getBorderInsets1(Component c, Insets insets) {
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

            int compHeight = (component.getPreferredSize().height + 10)/2; //TODO: This determines how far the content is from the top

            switch (titlePosition) {
                case ABOVE_TOP:
                    insets.top += compHeight + TEXT_SPACING;
                    break;
                case TOP:
                case DEFAULT_POSITION:
                    insets.top += Math.max(compHeight, borderInsets.top) - borderInsets.top;
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
