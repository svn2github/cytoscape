package org.cytoscape.internal.actions;

import java.net.URL;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.cytoscape.internal.task.OpenRecentSessionTaskFactory;
import org.cytoscape.io.read.CySessionReaderManager;
import org.cytoscape.io.util.RecentlyOpenedTracker;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;

/**
 * Update menu
 * 
 */
public class RecentSessionManager implements SessionLoadedListener {

	private final RecentlyOpenedTracker tracker;
	private final CyServiceRegistrar registrar;

	private final CySessionManager sessionManager;
	private final CySessionReaderManager readerManager;
	private final CyApplicationManager appManager;

	private final Set<OpenRecentSessionTaskFactory> currentMenuItems;

	public RecentSessionManager(final RecentlyOpenedTracker tracker, final CyServiceRegistrar registrar,
			final CySessionManager sessionManager, final CySessionReaderManager readerManager,
			final CyApplicationManager appManager) {
		this.tracker = tracker;
		this.registrar = registrar;
		this.sessionManager = sessionManager;
		this.readerManager = readerManager;
		this.appManager = appManager;
		
		this.currentMenuItems = new HashSet<OpenRecentSessionTaskFactory>();

		updateMenuItems();
	}

	private void updateMenuItems() {
		// Unregister services
		for (final OpenRecentSessionTaskFactory currentItem : currentMenuItems)
			registrar.unregisterAllServices(currentItem);

		currentMenuItems.clear();

		final List<URL> urls = tracker.getRecentlyOpenedURLs();

		for (final URL url : urls) {
			final Dictionary<String, String> dict = new Hashtable<String, String>();
			dict.put("preferredMenu", "File");
			dict.put("title", url.getFile());
			final OpenRecentSessionTaskFactory factory = new OpenRecentSessionTaskFactory(sessionManager, readerManager, appManager, tracker, url);
			registrar.registerService(factory, OpenRecentSessionTaskFactory.class, dict);

			this.currentMenuItems.add(factory);
		}

	}

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		updateMenuItems();
	}

}
