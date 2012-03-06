
package org.cytoscape.work.internal.submenu;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JMenuItem;

import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableMutator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTunableHandler;

public class SubmenuTunableMutator extends AbstractTunableInterceptor<SubmenuTunableHandler> 
	implements TunableMutator<SubmenuTunableHandler,JMenuItem> {
	
	private final DialogTaskManager dtm;

	private final Map<Object, TaskFactory<?>> taskFactoriesByContext;
	
	public SubmenuTunableMutator(DialogTaskManager dtm) {
		this.dtm = dtm;
		taskFactoriesByContext = new IdentityHashMap<Object, TaskFactory<?>>();
	}

	public void setConfigurationContext(Object o) {
		// no-op 
	}

	public JMenuItem buildConfiguration(Object objectWithTunables) {
		TaskFactory<?> tf = taskFactoriesByContext.get(objectWithTunables);
		if (tf == null) {
			return null;
		}

		Map<String,SubmenuTunableHandler> handlers = getHandlers(objectWithTunables);
		for ( SubmenuTunableHandler handler : handlers.values() ) {
			handler.setExecutionParams(dtm,tf);
			handler.handle();
			return handler.getSubmenuItem();
		}
		return null;
	}

	public boolean validateAndWriteBack(Object objs) {
	 	return true;	
	}

	void registerTunableContext(TaskFactory<?> factory, Object tunableContext) {
		taskFactoriesByContext.put(tunableContext, factory);
	}
	
	void unregisterTunableContext(Object tunableContext) {
		taskFactoriesByContext.remove(tunableContext);
	}
}

