// MapperAndAttribute.java
//------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------
package cytoscape.visual.ui;
//------------------------------------------------------------------------
import cytoscape.util.MutableString;
//------------------------------------------------------------------------
public class MapperAndAttribute {
    Object mapper;
    MutableString attribute;

    public MapperAndAttribute() {
    }

    public MapperAndAttribute(Object newMapper, MutableString newAttribute) {
	this.mapper = newMapper;
	this.attribute = newAttribute;
    }

}
