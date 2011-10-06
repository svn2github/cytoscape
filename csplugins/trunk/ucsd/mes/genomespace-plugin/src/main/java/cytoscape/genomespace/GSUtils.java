package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import java.awt.Dialog;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import javax.swing.JOptionPane;

import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.exceptions.AuthorizationException;
import org.genomespace.client.exceptions.GSClientException;
import org.genomespace.client.ui.GSLoginDialog;

import org.genomespace.datamanager.core.GSFileMetadata;


final class GSUtils {
	private GSUtils() { } // Prevent constructor calls.

	private static GsSession session = null;

	public static synchronized GsSession getSession() {
		if (session == null || !session.isLoggedIn()) {
			try {
				// Session won't be logged after this because
				// token parameter is null!
				session = new GsSession(null);
				if (!loginToGenomeSpace())
					throw new GSClientException("failed to login!", null);
			} catch (Exception e) {
				throw new GSClientException("failed to login", e);
			}
		}
		return session;
	}

	public static synchronized void reloginToGenomeSpace() {
		if ( session != null && session.isLoggedIn() ) {
			try { 
				session.logout();
			} catch (Exception e) { }
			session = null;
		}

		getSession();
	}

	public static synchronized boolean loginToGenomeSpace() {
		String gsenv = CytoscapeInit.getProperties().getProperty("genomespace.environment","test").toString();
		org.genomespace.client.ConfigurationUrls.init(gsenv);
		for (;;) {
			final GSLoginDialog loginDialog =
				new GSLoginDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
			loginDialog.setVisible(true);
			final String userName = loginDialog.getUsername();
			final String password = loginDialog.getPassword();
			if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
				return false;
			}

			try {
				session.login(userName, password);
				return true;
			} catch (final AuthorizationException e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							      "Invalid user name or password!",
							      "Login Error",
							      JOptionPane.ERROR_MESSAGE);
				continue;
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							      e.getMessage(),
							      "Login Error",
							      JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
	}

	public static Map<String,GSFileMetadata> getFileNameMap(Collection<GSFileMetadata> l) {
		Map<String,GSFileMetadata> nm = new HashMap<String,GSFileMetadata>();
		for ( GSFileMetadata f : l )
			nm.put(f.getName(), f);

		return nm;
	}
}

