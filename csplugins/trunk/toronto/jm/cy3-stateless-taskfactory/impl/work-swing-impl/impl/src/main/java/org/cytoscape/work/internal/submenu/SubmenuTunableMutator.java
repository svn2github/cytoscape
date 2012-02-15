
package org.cytoscape.work.internal.submenu;

import java.util.Map;

import javax.swing.JMenuItem;

import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.work.TaskContextManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableMutator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.swing.SubmenuTunableHandler;

public class SubmenuTunableMutator extends AbstractTunableInterceptor<SubmenuTunableHandler> 
	implements TunableMutator<SubmenuTunableHandler,JMenuItem> {
	
	private final DialogTaskManager dtm;
	private TaskContextManager contextManager;

	public SubmenuTunableMutator(DialogTaskManager dtm, TaskContextManager contextManager) {
		this.dtm = dtm;
		this.contextManager = contextManager;
	}

	public void setConfigurationContext(Object o) {
		// no-op 
	}

	public JMenuItem buildConfiguration(Object objectWithTunables) {
		TaskFactory tf;
		if ( objectWithTunables instanceof TaskFactory )
			tf = (TaskFactory)objectWithTunables;
		else
			return null;

		Object context = contextManager.getContext(tf);
		Map<String,SubmenuTunableHandler> handlers = getHandlers(context);
		for ( SubmenuTunableHandler handler : handlers.values() ) {
			handler.setExecutionParams(dtm,tf,context);
			handler.handle();
			return handler.getSubmenuItem();
		}
		return null;
	}

	public boolean validateAndWriteBack(Object objs) {
	 	return true;	
	}

}

