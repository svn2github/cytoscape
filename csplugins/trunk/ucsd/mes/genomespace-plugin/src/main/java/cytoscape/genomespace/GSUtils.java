package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.genomespace.ui.UserNamePasswordDialog;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import javax.swing.JOptionPane;

import org.genomespace.client.GsSession;
import org.genomespace.client.exceptions.AuthorizationException;
import org.genomespace.client.exceptions.GSClientException;
import org.genomespace.client.User;

import org.genomespace.datamanager.core.GSFileMetadata;


final class GSUtils {
	private GSUtils() {};

	private static GsSession session = null;

	public static GsSession getSession() {
		if (session == null || !session.isLoggedIn()) {
			try {
				session = new GsSession();
				if (!loginToGenomeSpace())
					throw new GSClientException("failed to login!", null);
			} catch (Exception e) {
				throw new GSClientException("failed to login", e);
			}
		}
		return session;
	}

	public static boolean loginToGenomeSpace() {
		for (;;) {
			final UserNamePasswordDialog userNamePasswordDialog = new UserNamePasswordDialog();
			userNamePasswordDialog.setVisible(true);
			final String userName = userNamePasswordDialog.getUserName();
			final String password = userNamePasswordDialog.getPassword();
			if (userName == null || userName.isEmpty() || password == null || password.isEmpty())
				return false;

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

