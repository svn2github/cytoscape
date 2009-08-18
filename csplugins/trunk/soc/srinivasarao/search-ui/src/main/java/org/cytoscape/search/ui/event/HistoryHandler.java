package org.cytoscape.search.ui.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import org.cytoscape.search.ui.SearchComboBox;
import org.cytoscape.search.ui.SearchPanelFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.SessionLoadedEvent;
import org.cytoscape.session.SessionLoadedListener;
import org.cytoscape.session.SessionSavedEvent;
import org.cytoscape.session.SessionSavedListener;

public class HistoryHandler implements SessionLoadedListener,
		SessionSavedListener {

	private CyNetworkManager netmgr;

	public HistoryHandler(CyNetworkManager nm) {
		this.netmgr = nm;
	}

	void init() {
		// register the listeners
		// cytoscape.Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this)
	}

	public void handleEvent(SessionSavedEvent e) {
		// save the search history here
		System.out.println("Session Saved");
		SearchComboBox b = SearchPanelFactory.getGlobalInstance(netmgr).getmainPanel().getSearchBox();
		Vector<String> history = b.getHistory();
		try {
			System.out.println(new File(".").getAbsolutePath());
			PrintWriter pw = new PrintWriter("./SessionHistory");
			for (String str : history) {
				pw.write(str + "\n");

			}
			pw.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}

	}

	public void handleEvent(SessionLoadedEvent e) {
		// restore session here
		System.out.println("Session Loaded");
		SearchComboBox b = SearchPanelFactory.getGlobalInstance(netmgr)
				.getmainPanel().getSearchBox();
		Vector<String> history = b.getHistory();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("./SessionHistory")));
			String str;
			while ((str = br.readLine()) != null) {
				history.add(str);
				b.addItem(str);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
