// VizMapTab.java
//--------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;

import cytoscape.visual.calculators.Calculator;
import javax.swing.*;
import java.awt.*;
/**
 *  VizMapTab defines an organizational abstract class for tabs in the
 *  Set Visual Properties dialog. You probably don't want to extend this
 *  class, since {@link VizMapAttrTab}, {@link VizMapFontTab}, and
 *  {@link VizMapSizeTab} provide
 *  UI functionality for all mappable attributes in Cytoscape.
 */
public abstract class VizMapTab extends JPanel {
    protected VizMapTab(LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
    }

    protected VizMapTab(LayoutManager layout) {
	this(layout, true);
    }

    protected VizMapTab(boolean isDoubleBuffered) {
	this(new FlowLayout(), isDoubleBuffered);
    }

    protected VizMapTab() {
	this(true);
    }

    /**
     * Check that the calculator selected on this tab is not c. Because Java's
     * AWT only allows one parent per Component, bad things happen when multiple
     * tabs attempt to use the same calculator. {@link VizMapUI} prevents these
     * conflicts from happening by asking each VizMapTab to check if its
     * current calculator matches one that another VizMapTab is trying to select.
     *
     * The method returns VizMapTab since instances of VizMapTab should not block
     * themselves from selecting a calculator. (eg. it ignores the warning if the
     * VizMapTab that was attempting to select a calculator is itself)
     *
     * @param	c	newly selected calculator
     * @return	null if calculator is not selected by this object, or
     *		the {@link VizMapTab} that currently owns the calculator
     */
    abstract VizMapTab checkCalcSelected(Calculator c);

    /**
     * When the data structures underlying the visualization system change,
     * (eg. NodeAttributes, EdgeAttributes) refresh the UI.
     */
    public abstract void refreshUI();
}
