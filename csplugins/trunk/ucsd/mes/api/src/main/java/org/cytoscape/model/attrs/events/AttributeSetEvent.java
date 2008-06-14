
package org.cytoscape.model.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.attrs.CyAttributes;

public interface AttributeSetEvent extends CyEvent<CyAttributes> {

    public String getAttributeName();
}

