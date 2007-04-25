package cytoscape.visual.ui;

import cytoscape.visual.Arrow;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.Line;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;



/**
 * Managing value editors in for each data types.<br>
 * This enum contains both continuous and discrete editors.
 * 
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public enum EditorDisplayer {
    DISCRETE_COLOR(JColorChooser.class, "showDialog",
        new Class[] { Component.class, String.class, Color.class },
        new Object[] { null, "Select Color...", null }, Color.class), 
    DISCRETE_FONT(JColorChooser.class, "showDialog",
        new Class[] { Component.class, String.class, Color.class },
        new Object[] { null, "Select Color...", null }, Font.class), 
    DISCRETE_NUMBER(JOptionPane.class, "showInputDialog",
        new Class[] { Component.class, Object.class },
        new Object[] { null, "Please enter new numeric value:" }, Number.class), 
    DISCRETE_STRING(JOptionPane.class, "showInputDialog",
        new Class[] { Component.class, Object.class },
        new Object[] { null, "Please enter new text value:" }, String.class), 
    DISCRETE_SHAPE(ValueSelectDialog.class, "showDialog",
        new Class[] { VisualPropertyType.class, JDialog.class },
        new Object[] { VisualPropertyType.NODE_SHAPE, null }, NodeShape.class), 
    DISCRETE_ARROW_SHAPE(ValueSelectDialog.class, "showDialog",
        new Class[] { VisualPropertyType.class, JDialog.class },
        new Object[] { VisualPropertyType.EDGE_SRCARROW_SHAPE, null },
        Arrow.class), 
    DISCRETE_LINE_TYPE(ValueSelectDialog.class, "showDialog",
        new Class[] { VisualPropertyType.class, JDialog.class },
        new Object[] { VisualPropertyType.EDGE_LINETYPE, null }, Line.class), 
    DISCRETE_LABEL_POSITION(PopupLabelPositionChooser.class, "showDialog",
        new Class[] { Frame.class, LabelPosition.class },
        new Object[] { null, null }, LabelPosition.class), 
    CONTINUOUS_COLOR(GradientEditorPanel.class, "showDialog",
        new Class[] { int.class, int.class, String.class, VisualPropertyType.class },
        new Object[] { 450, 200, "Gradient Editor", null }, Color.class), 
    CONTINUOUS_CONTINUOUS(JColorChooser.class, "showDialog",
        new Class[] { Component.class, String.class, Number.class },
        new Object[] { null, "Select Color...", null }, Color.class), 
    CONTINUOUS_DISCRETE(JColorChooser.class, "showDialog",
        new Class[] { Component.class, String.class, Color.class },
        new Object[] { null, "Select Color...", null }, null);
    
    private Class chooserClass;
    private String command;
    private Class[] paramTypes;
    private Object[] parameters;
    private Class compatibleClass;
    
    /**
     * Defines editor type.
     */
    public enum EditorType {
    	CONTINUOUS, DISCRETE;
    }
    private EditorDisplayer(Class chooserClass, String command,
        Class[] paramTypes, Object[] parameters, Class compatibleClass) {
        this.chooserClass = chooserClass;
        this.command = command;
        this.paramTypes = paramTypes;
        this.parameters = parameters;
        this.compatibleClass = compatibleClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getActionClass() {
        return chooserClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCommand() {
        return command;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class[] getParamTypes() {
        return this.paramTypes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getParameters() {
        return this.parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getCompatibleClass() {
        return this.compatibleClass;
    }
    
    public void setParameters(Object[] param) {
    	this.parameters = param;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static EditorDisplayer getEditor(final VisualPropertyType type,
        final EditorType editor) {
        final Class dataType = type.getDataType();

        for (EditorDisplayer command : values()) {
            if ((dataType == command.getCompatibleClass()) &&
                    (((editor == EditorType.CONTINUOUS) &&
                    command.toString()
                               .startsWith(EditorType.CONTINUOUS.name())) ||
                    ((editor == EditorType.DISCRETE) &&
                    command.toString()
                               .startsWith(EditorType.DISCRETE.name()))))
                return command;
        }

        return null;
    }
}
