package cytoscape;

import org.cytoscape.model.*;
import org.cytoscape.viewmodel.*;
import org.cytoscape.vizmap.*;

/**
 */
public class PassthroughMappingCalculator<T> implements MappingCalculator<T> {
    private String attributeName;
    private VisualProperty<T> vp;
    private Class<T> dataType;
    /**
     * dataType is the type of the _attribute_ !!
     * currently we force that to be the same as the VisualProperty;
     * FIXME: allow different once? but how to coerce?
     */
    public PassthroughMappingCalculator(String attributeName, VisualProperty<T> vp,
					Class<T> dataType){
	this.attributeName=attributeName;
	this.vp = vp;
	this.dataType = dataType;
    }
    public void setMappingAttributeName(String attributeName){
	this.attributeName=attributeName;
    }

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public String getMappingAttributeName(){
	return attributeName;
    }

	/**
	 * The visual property the attribute gets mapped to.
	 *
	 * @param vp  DOCUMENT ME!
	 */
    public void setVisualProperty(VisualProperty<T> vp){
	this.vp = vp;
    }

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public VisualProperty<T> getVisualProperty() {return vp;}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
    public <V extends GraphObject> void apply(View<V> v){
	T value = v.getSource().attrs().get(attributeName, dataType);
	v.setVisualProperty(vp, value);
    }
}
