
package org.cytoscape.model.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.attrs.CyAttributes;

public interface AttributeDeletedEvent extends CyEvent<CyAttributes> {

    public String getAttributeName();

}

