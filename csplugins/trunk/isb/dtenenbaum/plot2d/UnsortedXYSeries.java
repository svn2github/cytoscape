//UnsortedXYSeries

package csplugins.isb.dante.plot2d;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jfree.data.*;
import org.jfree.util.ObjectUtils;


/**
 * Extends XYSeries to override the automatic sorting of series by
 * ascending X values. Allows data with non-sequential X values to be plotted.
 * This implementation is suitable for Plot2D's needs but would need to be
 * further modified if it were to be added in to JFreeChart. 
 * 
 * @author Dan Tenenbaum
 */
public class UnsortedXYSeries extends XYSeries {
	
	
	/**
	 * Constructs an unsorted XY series with the given name. 
	 * Calls the superclass constructor in such a way as to allow
	 * duplicate X values.
	 * 
	 * @param name The name of the series to construct.
	 */
	public UnsortedXYSeries(String name) {
		super(name,true);
	} //ctor
	

	/**
	 * Subclass to override the sorting in the superclass.
	 * 
	 * @param item The item to add
	 * @param notify Whether to notify series change listeners 
	 */
	public void add(XYDataItem item, boolean notify) throws SeriesException {

		if (item == null) {
			throw new IllegalArgumentException("UnsortedXYSeries.add(...): null item not allowed.");
		}
		
		
		data.add(item);
		if (getItemCount() > getMaximumItemCount()) {
			this.data.remove(0);
		}
        
		if (notify) {
			fireSeriesChanged();
		}
	} //add

} // class UnsortedXYSeries


