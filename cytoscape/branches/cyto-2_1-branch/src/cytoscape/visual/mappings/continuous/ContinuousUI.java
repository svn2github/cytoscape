//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.CyNetwork;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.ui.ValueDisplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Provides User Interface Controls for Continuous Mappers.
 */
public class ContinuousUI extends JPanel implements ActionListener {

    //  Public Constants
    public static final int LESSER = 0;
    public static final int EQUAL = 1;
    public static final int GREATER = 2;

    //  Internal Components
    private ArrayList components;

    //  Parent Dialog, Mappers, Layout Managers, and Internal Data
    private Object defaultObject;
    private JDialog parentDialog;
    private CyNetwork network;
    private ContinuousMapping cm;

    /**
     *  Constructor.
     */
    public ContinuousUI(JDialog jd, Object defaultObject, CyNetwork network,
            ContinuousMapping cMap) {
        this.parentDialog = jd;
        this.defaultObject = defaultObject;
        this.network = network;
        this.cm = cMap;
        init();
        createPointPanel();
    }

    /**
     * Initializes all ArrayLists.
     */
    private void init() {
        components = new ArrayList();
    }

    /**
     * Resets the UI and Updates the Continuous Mapper.
     */
    public void resetUI() {
        this.removeAll();
        createPointPanel();

        //  Layout components again.
        validate();
        parentDialog.validate();
    }

    /**
     * Apply Changes.
     * @param e ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        cm.fireStateChanged();
    }

    /**
     * Enables or disables all components of the panel.
     * @param b whether to enable the panel (true) or disable it (false)
     */
    public void setEnabled(boolean b) {
        for (int i = 0; i < components.size(); i++) {
            JComponent component = (JComponent) components.get(i);
            component.setEnabled(b);
        }
    }

    /**
     * Generates the main UI Panel.
     */
    public void createPointPanel() {
        this.setLayout(new BorderLayout());
        GridBagGroup grid = new GridBagGroup();

        //  Create Add Button
        JButton newPointButton = new JButton("Add Point");
        newPointButton.addActionListener(new AddPointListener(this, cm,
                defaultObject));
        MiscGB.insert(grid, newPointButton, 4, 0);

        //  Create all Points
        createPointUI(grid);
        add(grid.panel, BorderLayout.CENTER);

        //  Removed for now:  Create Add Button
        //  JButton applyButton = new JButton("Apply to Graph");
        //  applyButton.addActionListener(this);
        //  JPanel panel = new JPanel();
        //  panel.add(applyButton);
        //  add(panel, BorderLayout.SOUTH);
    }

    private void createPointUI(GridBagGroup grid) {
        for (int i = 0; i < cm.getPointCount(); i++) {
            ContinuousMappingPoint point = cm.getPoint(i);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(6);
            String textValue = numberFormat.format(point.getValue());
            JTextField text = new JTextField(textValue);
            text.setColumns(6);
            PointTextListener ptl = new PointTextListener(text, cm,
                    parentDialog, i);
            text.addFocusListener(ptl);

            MiscGB.insert(grid, text, 1, i + 1);

            //  Create Delete Button
            JButton bDel = new JButton("Del");
            bDel.addActionListener(new DeletePointListener(this, cm, i));
            MiscGB.insert(grid, bDel, 0, i + 1);

            //  Create Value Displayers
            createButtonAndVD(grid, LESSER, i);
            createButtonAndVD(grid, EQUAL, i);
            createButtonAndVD(grid, GREATER, i);
        }
    }

    /**
     * Creates Button and Value Displayer.
     */
    private void createButtonAndVD(GridBagGroup grid, int lesserEqualGreater,
            int i) {
        String buttonString[] = {"Below", "Equal", "Above"};
        ContinuousMappingPoint cmp = cm.getPoint(i);
        BoundaryRangeValues range = cmp.getRange();
        Object o = getValue(lesserEqualGreater, range);
        ValueDisplayer chooser = ValueDisplayer.getDisplayFor(parentDialog,
                "Select Appearance", o);

        //  Create Value Listener
        ValueListener listener = new ValueListener(this, cm, i,
                lesserEqualGreater);
        chooser.addItemListener(listener);

        JButton button = new JButton(buttonString[lesserEqualGreater]);
        button.addActionListener(chooser.getInputListener());

        components.add(chooser);
        components.add(button);

        //  Conditionally add to UI
        if (((i == 0) && (lesserEqualGreater == LESSER)) ||
                (lesserEqualGreater == EQUAL) ||
                ((i == cm.getPointCount() - 1) &&
                (lesserEqualGreater == GREATER))) {
            MiscGB.insert(grid, button, 2, i + lesserEqualGreater, 1, 1,
                    GridBagConstraints.HORIZONTAL);
            MiscGB.insert(grid, chooser, 3, i + lesserEqualGreater);
        }
    }

    /**
     * Gets Correct BoundaryRange Object.
     * @param lesserEqualGreater Integer value:  LESSER, EQUAL or GREATER.
     * @param range BoundaryRangeValues object.
     * @return Value Object.
     */
    private Object getValue(int lesserEqualGreater,
            BoundaryRangeValues range) {
        Object color;
        switch (lesserEqualGreater) {
            case LESSER:
                color = range.lesserValue;
                break;
            case EQUAL:
                color = range.equalValue;
                break;
            case GREATER:
            default:
                color = range.greaterValue;
                break;
        }
        return color;
    }
}
