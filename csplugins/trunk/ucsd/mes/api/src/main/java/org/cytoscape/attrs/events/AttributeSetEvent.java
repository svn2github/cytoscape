
package org.cytoscape.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.attrs.CyAttributes;

public interface AttributeSetEvent extends CyEvent<CyAttributes> {

    public String getAttributeName();
}

