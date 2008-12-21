package org.cytoscape.presentation.internal;
import  org.cytoscape.viewmodel.VisualProperty;
import  org.cytoscape.viewmodel.DependentVisualPropertyCallback;
import java.util.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A VisualProperty whose values are elements of a discrete set, all
 * implementing the same interface T
 * 
 * This VisualProperty is extensible by providing an OSGi service of
 * interface T. (See demo code at....)
 * 
 * Note that defaultValue instance also has to be registered as an
 * OSGi service.
 * 
 * TODO: will need some events so that UI can handle services being
 * added/removed. (Maybe UI will listen directly to OSGi events, maybe
 * DiscreteVisualProperty will wrap OSGi events, so that UI can be
 * OSGi-agnostic.)
 * 
 */
public class DiscreteVisualProperty<T> implements VisualProperty<T> {
    private String id;
    private String name;
    private T defaultValue;
    private Class<T> dataType;
    private VisualProperty.GraphObjectType objectType;
    private DependentVisualPropertyCallback callback;
    private BundleContext bc;
    
    public DiscreteVisualProperty(String id, String name, Class<T> dataType,
				  T defaultValue,
				  VisualProperty.GraphObjectType objectType,
				  BundleContext bc){
	this(id, name, dataType, defaultValue, objectType, null, bc);
    }
    public DiscreteVisualProperty(String id, String name, Class<T> dataType,
				  T defaultValue,
				  VisualProperty.GraphObjectType objectType,
				  DependentVisualPropertyCallback callback,
				  BundleContext bc){
	this.id = id;
	this.name = name;
	this.defaultValue = defaultValue;
	this.dataType = dataType;
	this.objectType = objectType;
	this.callback = callback;
	this.bc = bc;
    }
    /**
     * Return all known values
     *
     * This method is to allow UI to show a list of values so that user can pick one.
     *
     * This implementation simply queries the OSGi framework for all
     * services implementing dataType interface.
     */
    public Set<T> getValues(){ // copy-paste-modified from CyEventHelperImpl in core3/model
	Set<T> ret = new HashSet<T>();
	if (bc == null)
	    return ret;
	try {
	    ServiceReference[] sr = bc.getServiceReferences(dataType.getName(), null);
	    
	    if (sr != null){
		for (ServiceReference r : sr) {
		    T value = (T) bc.getService(r);
		    
		    if (value != null)
			ret.add(value);
		}
	    } else {
		System.out.println("sr is null");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return ret;
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
