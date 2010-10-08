
package org.cytoscape.session.events;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

import org.cytoscape.session.CySession;
import org.cytoscape.session.CySessionManager;

import org.cytoscape.event.AbstractCyEvent;

/**
 * This event is fired synchronously by the CySessionManager at beginning of the
 * {@link CySessionManager#getCySession()} method.  The intent is to allow
 * listeners to provide information to this event object or to update their 
 * state before that state is interrogated by the CySessionManager. 
 */
public final class SessionAboutToBeSavedEvent extends AbstractCyEvent<CySessionManager> {
	final Map<String,List<File>> pluginFileListMap;

	// TODO should the source be the session manager
	public SessionAboutToBeSavedEvent(final CySessionManager source) {
		super(source, SessionAboutToBeSavedListener.class);

		pluginFileListMap = new HashMap<String,List<File>>();
	}

	/**
	 * @param pluginName The name of the plugin that these files should be stored for.
	 * @param files The list of File objects to be stored in the session file.
	 */
	public void addPluginFiles(final String pluginName, List<File> files) throws Exception {
		// Throw checked Exceptions here to force plugin authors to deal with
		// problems they might create.
		if ( pluginName == null )
			throw new Exception("plugin name is null");
			
		if ( pluginName == "" )
			throw new Exception("plugin name is empty");

		if ( pluginFileListMap.containsKey( pluginName ) )
			throw new Exception("The plugin file list already contains a list of files identified by the name: " + pluginName);

		if ( files == null )
			throw new Exception("file list is null");

		// allow empty lists

		pluginFileListMap.put(pluginName, new ArrayList<File>(files));
	}

	/**
	 * This method is not meant to be used by listeners for this event, 
	 * although you can and no harm should come to you.
	 * @return A map of plugin names to lists of files to be stored in the
	 * session for that plugin.
	 */
	public Map<String,List<File>> getPluginFileListMap() {
		// Make the return value immutable so that listeners
		// can't mess with us.
		return Collections.unmodifiableMap( pluginFileListMap );
	}
}
