
package org.cytoscape.work.internal.submenu;

import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JMenuItem;

import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableMutator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTunableHandler;

public class SubmenuTunableMutator extends AbstractTunableInterceptor<SubmenuTunableHandler> 
	implements TunableMutator<SubmenuTunableHandler,JMenuItem> {
	
	private final DialogTaskManager dtm;

	private final Map<Object, TaskFactory<? super Object>> taskFactoriesByContext;
	
	public SubmenuTunableMutator(DialogTaskManager dtm) {
		this.dtm = dtm;
		taskFactoriesByContext = new WeakHashMap<Object, TaskFactory<? super Object>>();
	}

	public void setConfigurationContext(Object o) {
		// no-op 
	}

	public JMenuItem buildConfiguration(Object objectWithTunables) {
		TaskFactory<? super Object> tf = taskFactoriesByContext.get(objectWithTunables);
		if (tf == null || ! tf.isReady(objectWithTunables)) {
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

	void registerTunableContext(TaskFactory<? super Object> factory, Object tunableContext) {
		taskFactoriesByContext.put(tunableContext, factory);
	}
}

