package org.cytoscape.presentation.internal;
import  org.cytoscape.viewmodel.VisualProperty;
import  org.cytoscape.viewmodel.DependentVisualPropertyCallback;

/**
 * FIXME
 * Think of it as a column in the viewmodel table.
 */
public class VisualPropertyImpl<T> implements VisualProperty<T> {
    private String id;
    private String name;
    private T defaultValue;
    private Class<T> dataType;
    private VisualProperty.GraphObjectType objectType;
    private DependentVisualPropertyCallback callback;

    public VisualPropertyImpl(String id, String name, T defaultValue, Class<T> dataType,
			      VisualProperty.GraphObjectType objectType){
	this(id, name, defaultValue, dataType, objectType, null);
    }
    public VisualPropertyImpl(String id, String name, T defaultValue, Class<T> dataType,
			      VisualProperty.GraphObjectType objectType,
			      DependentVisualPropertyCallback callback){
	this.id = id;
	this.name = name;
	this.defaultValue = defaultValue;
	this.dataType = dataType;
	this.objectType = objectType;
	this.callback = callback;
    }
    public VisualProperty.GraphObjectType getObjectType(){
	return objectType;
    }

	/**
	 * The type of object represented by this property.
	 *
	 * @return  DOCUMENT ME!
	 */
    public Class<T> getType(){
	// neither of these work, see
	// http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=021798
	//return defaultValue.getClass();
	//return T.class;
	// this is needed, and thus the need for 'dataType' in the constructor:
	return dataType;
    }

	/**
	 * The default value of this property.
	 * 
	 * @return  DOCUMENT ME!
	 */
    public T getDefault(){
	return defaultValue; //FIXME: defensive copy needed? how to do that?
    }

	/**
	 * Used for hashes identifying this property.
	 *
	 * @return  DOCUMENT ME!
	 */
    public String getID(){
	return id;
    }

	/**
	 * For presentation to humans.
	 *
	 * @return  DOCUMENT ME!
	 */
    public String getName(){
	return name;
    }

	/**
	 * 
	 * 
	 * @return callback, or null if there isn't one
	 */
    public DependentVisualPropertyCallback dependentVisualPropertyCallback(){return callback;}
}
