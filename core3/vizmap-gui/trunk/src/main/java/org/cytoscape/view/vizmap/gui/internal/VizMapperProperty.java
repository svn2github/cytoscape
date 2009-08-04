package org.cytoscape.view.vizmap.gui.internal;

import com.l2fprod.common.propertysheet.DefaultProperty;

/**
 * Extended version of DefaultProperty which accepts one more value as hidden
 * object.
 * 
 * From 3.0: This is a type-safe container.
 * 
 * @author kono
 * @version 0.5
 */
public class VizMapperProperty<T> extends DefaultProperty {
	private final static long serialVersionUID = 1202339868680341L;
	private T hiddenObject;

	public VizMapperProperty() {
		
	}
	
//	@Override public Class<T> getType() {
//		return super.getType();
//	}
//	
//	@Override public T getValue() {
//		return 
//	}
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param obj
	 *            DOCUMENT ME!
	 */
	public void setHiddenObject(T obj) {
		this.hiddenObject = obj;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public T getHiddenObject() {
		return hiddenObject;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param object
	 *            DOCUMENT ME!
	 */
	public void readFromObject(Object object) {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param object
	 *            DOCUMENT ME!
	 */
	public void writeToObject(Object object) {
	}
}
