
package cytoscape.genomespace;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.genomespace.client.GsSession;
import org.genomespace.client.exceptions.GSClientException;
import org.genomespace.client.User;

import org.genomespace.datamanager.core.GSFileMetadata;

final class GSUtils {

	private GSUtils() {};

	private static GsSession session = null;

	public static GsSession getSession() {
		if ( session == null || !session.isLoggedIn()) {
			try {
				session = new GsSession();
				String username = "test";
				String password = "password";
				User user = session.login(username, password);
			} catch (Exception e) { throw new GSClientException("failed to login",e); }
		}
		return session;
	}

	public static Map<String,GSFileMetadata> getFileNameMap(Collection<GSFileMetadata> l) {
		Map<String,GSFileMetadata> nm = new HashMap<String,GSFileMetadata>();
		for ( GSFileMetadata f : l )
			nm.put(f.getName(), f);

		return nm;
	}
}

