
package cytoscape.events;

import org.cytoscape.event.AbstractCyEvent;
import java.util.Properties;
/**
 * 
 */
public final class PreferencesUpdatedEvent extends AbstractCyEvent<Object> {
	private final Properties oldProps;
	private final Properties newProps;

	public PreferencesUpdatedEvent(final Object source, final Properties oldProps, final Properties newProps) {
		super(source, PreferencesUpdatedListener.class);
		this.oldProps = oldProps;
		this.newProps = newProps;
	}

	public Properties getOldProperties() {
		return oldProps;
	}

	public Properties getNewProperties() {
		return newProps;
	}
}
