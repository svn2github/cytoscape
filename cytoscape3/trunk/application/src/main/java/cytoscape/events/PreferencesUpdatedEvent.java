
package cytoscape.events;

import org.cytoscape.event.CyEvent;
import java.util.Properties;
/**
 * 
 */
public interface PreferencesUpdatedEvent extends CyEvent<Object> {
	Properties getOldProperties();
	Properties getNewProperties();
}
