package org.cytoscape.presentation.internal;
import  org.cytoscape.viewmodel.VisualProperty;
import  org.cytoscape.viewmodel.DependentVisualPropertyCallback;
import java.util.List;
import java.util.ArrayList;
/**
 * FIXME
 * Think of it as a column in the viewmodel table.
 */
public class DiscreteVisualProperty<T> implements VisualProperty<T> {
    private String id;
    private String name;
    //private T defaultValue;
    private List<T> values;
    private Class<T> dataType;
    private VisualProperty.GraphObjectType objectType;
    private DependentVisualPropertyCallback callback;

    public DiscreteVisualProperty(String id, String name, Class<T> dataType,
			      List<T> initialValues,
			      VisualProperty.GraphObjectType objectType){
	this(id, name, dataType, initialValues, objectType, null);
    }
    public DiscreteVisualProperty(String id, String name, Class<T> dataType,
				  List<T> initialValues,
				  VisualProperty.GraphObjectType objectType,
				  DependentVisualPropertyCallback callback){
	this.id = id;
	this.name = name;
	this.values = new ArrayList<T>(initialValues);
	this.dataType = dataType;
	this.objectType = objectType;
	this.callback = callback;
    }
    public VisualProperty.GraphObjectType getObjectType(){
	return objectType;
    }

	/**
	 * The type of object represented by this property.
	 * FIXME: should return DiscreteValue instead!!
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
	return values.get(0); //FIXME: defensive copy needed? how to do that?
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
