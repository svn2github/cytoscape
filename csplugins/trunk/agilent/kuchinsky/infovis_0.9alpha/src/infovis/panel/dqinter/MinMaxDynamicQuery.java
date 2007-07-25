/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.column.NumberColumn;
import infovis.panel.DefaultDoubleBoundedRangeModel;
import infovis.panel.DynamicQuery;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <code>BoundedFloatModel</ocde> for <code>NumberColumn</code>s.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class MinMaxDynamicQuery
    extends DefaultDoubleBoundedRangeModel
    implements ChangeListener {
    protected DoubleRangeSlider component;

  class MaxDynamicQuery extends AbstractDynamicQuery {
    protected NumberColumn numberColumn;

    public MaxDynamicQuery(NumberColumn nc) {
      super(nc);
      this.numberColumn = nc;
    }
  
    public void update() {
        MinMaxDynamicQuery.this.update();
    }

    public boolean isFiltered(int row) {
      if (column.isValueUndefined(row))
	return false;
      double v = numberColumn.getDoubleAt(row);
        double min = numberColumn.round(getValue());
	boolean ret = ! (v >= min);
        return ret;
    }
    public JComponent getComponent() {
      return MinMaxDynamicQuery.this.getComponent();
    }
  }

  class MinDynamicQuery extends AbstractDynamicQuery {
    protected NumberColumn numberColumn;

    public MinDynamicQuery(NumberColumn nc) {
      super(nc);
      this.numberColumn = nc;
    }
    
    public void update() {
        MinMaxDynamicQuery.this.update();
    }

  
    public boolean isFiltered(int row) {
      if (column.isValueUndefined(row))
	return false;
      double v = numberColumn.getDoubleAt(row);
        double max = numberColumn.round(getValue()+getExtent());
	boolean ret = ! (v <= max);
        return ret;
    }

    public JComponent getComponent() {
      return MinMaxDynamicQuery.this.getComponent();
    }
  }

  protected MaxDynamicQuery maxDQ;
  protected MinDynamicQuery minDQ;
  
  public MinMaxDynamicQuery(NumberColumn min, NumberColumn max) {
    minDQ = new MinDynamicQuery(min);
    maxDQ = new MaxDynamicQuery(max);
  }

  public DynamicQuery getMinDQ() { return minDQ; }
  public DynamicQuery getMaxDQ() { return maxDQ; }

    public JComponent getComponent() {
	if (component == null) {
	    component = new DoubleRangeSlider(this);
            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));            
	    component.setEnabled(true);
	}
	return component;
    }


    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == minDQ.numberColumn 
            || e.getSource() == maxDQ.numberColumn) {
            update();
        }
    }

    public void update() {
        setRangeProperties(maxDQ.numberColumn.getDoubleMin(),
                   minDQ.numberColumn.getDoubleMax()-maxDQ.numberColumn.getDoubleMin(),
                   maxDQ.numberColumn.getDoubleMin(),
                   minDQ.numberColumn.getDoubleMax(),
                   false);
    }
    
    protected void fireStateChanged() {
        super.fireStateChanged();
        minDQ.apply();
        maxDQ.apply();
    }

}
