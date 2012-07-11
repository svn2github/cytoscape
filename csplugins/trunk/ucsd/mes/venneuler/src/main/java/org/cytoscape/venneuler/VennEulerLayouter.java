package org.cytoscape.venneuler;

import org.dishevelled.venn.VennModel;
import org.dishevelled.venn.VennLayouter;
import org.dishevelled.venn.VennLayouter.PerformanceHint;
import org.dishevelled.venn.VennLayout;

import edu.uic.ncdm.venn.VennData; 
import edu.uic.ncdm.venn.VennDiagram; 
import edu.uic.ncdm.venn.VennAnalytic; 

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Shape;

public final class VennEulerLayouter<T> {
	
	private final Map<Integer,List<T>> data = new HashMap<Integer,List<T>>();

	public VennLayout layout(VennModel<T> model,
	                         Rectangle2D boundingBox,
	                         PerformanceHint performanceHint) {
		if ( model == null )
			throw new NullPointerException("model can't be null");
		if ( boundingBox == null )
			throw new NullPointerException("bounding rectangle can't be null");
		if ( model.size() <= 0 )
			throw new IllegalArgumentException("model size can't be 0!");

		data.clear();

		// populate data
		for ( int i = 0; i < model.size(); i++ ) 
			for (T t : model.get(i)) 
				addElement(i,t);

		return create(boundingBox);
	}

	private void addElement(int setKey, T setValue) {
		if ( setValue == null )
			throw new NullPointerException("set value cannot be null");

		List<T> values = data.get(setKey);
		if ( values == null ) {
			values = new ArrayList<T>();
			data.put(setKey,values);
		}
		values.add(setValue);
	}

	private VennLayout create(Rectangle2D boundingBox) {
		// convert map into VennData
		int numRows = 0;
		for ( Integer key : data.keySet() ) 
			numRows += data.get(key).size();

		String[][] stringData = new String[numRows][2];
		int i = 0;
		for ( Integer key : data.keySet() ) {
			String keyString = key.toString();
			for ( T value : data.get(key) ) {
				stringData[i][0] = value.toString();
				stringData[i][1] = keyString;
				i++;
			}
		}
		VennData vennData = new VennData(stringData,new double[0], false);

		// now create the diagram
		VennDiagram vennDiagram = new VennAnalytic().compute(vennData);

		return new VennEulerLayout(vennDiagram,boundingBox);
	}
}
