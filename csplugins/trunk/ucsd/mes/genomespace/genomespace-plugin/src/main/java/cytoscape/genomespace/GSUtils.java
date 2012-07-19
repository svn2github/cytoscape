package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import java.awt.Dialog;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import javax.swing.JOptionPane;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.exceptions.AuthorizationException;
import org.genomespace.client.exceptions.GSClientException;
import org.genomespace.client.ui.GSLoginDialog;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.datamanager.core.GSDataFormat;
import org.genomespace.client.ConfigurationUrls;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


final class GSUtils {
	private GSUtils() { } // Prevent constructor calls.

	private static GsSession session = null;

	public static synchronized boolean loggedInToGS() {
		return (session != null && session.isLoggedIn()); 
	}

	public static synchronized GsSession getSession() {
		if (session == null ) {
			try {
				String gsenv = CytoscapeInit.getProperties().getProperty("genomespace.environment","test").toString();
				ConfigurationUrls.init(gsenv);
				session = new GsSession();
			} catch (Exception e) {
				throw new GSClientException("failed to create GenomeSpace session", e);
			}
		}

		if (!session.isLoggedIn()) {
			try {
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
				Cytoscape.getDesktop().setStatusBarMsg("Logged out of GenomeSpace");
			} catch (Exception e) { }
			session = null;
		}

		getSession();
	}

	public static synchronized boolean loginToGenomeSpace() {
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
				Cytoscape.getDesktop().setStatusBarMsg("Logged in to GenomeSpace as: " + userName);
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

	public static GSDataFormat findConversionFormat(Collection<GSDataFormat> availableFormats, String targetExt) {
		if ( targetExt == null || targetExt.equals("") || availableFormats == null )
			return null;

		for ( GSDataFormat format : availableFormats ) 
			if ( targetExt.equalsIgnoreCase( format.getFileExtension() ) )
				return format;

		return null;
	}

	public static File downloadToTempFile(String urlString) {
		return downloadToTempFile(urlString,null);
	}

	public static File downloadToTempFile(String urlString, GSDataFormat format) {
		InputStream is = null;
		OutputStream os = null;
		File tempFile = null;
		try {
			URL url = new URL(urlString);
			DataManagerClient dmc = getSession().getDataManagerClient();

			if ( format == null )
				is = dmc.getInputStream(url);
			else
				is = dmc.getInputStream(dmc.getFileUrl(url,format.getUrl()));

			tempFile = File.createTempFile("tempGS","." + getExtension(url.toString()));
			os =new FileOutputStream(tempFile);
			byte buf[] = new byte[1024];
			int len;
			while( (len = is.read(buf)) > 0 )
				os.write(buf,0,len);
		} catch (Exception e) {
			throw new IllegalArgumentException("failed to load url: " + urlString, e);
		} finally {
			try { 
				if ( is != null )
					is.close();
				if ( os != null )
					os.close();
			} catch (IOException ioe) {
				throw new IllegalArgumentException("couldn't even close streams", ioe);
			}
		}

		return tempFile;
	}

	public static String getExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1 ? fileName : fileName.substring(lastDotPos + 1)).toLowerCase();
	}

	public static String getNetworkTitle(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return lastDotPos == -1 ? fileName : fileName.substring(0, lastDotPos);
	}

    // Returns the directory component of "path"
    public static String dirName(final String path) {
        final int lastSlashPos = path.lastIndexOf('/');
        return path.substring(0, lastSlashPos + 1);
    }


    // Returns the basename component of "path"
    public static String baseName(final String path) {
        final int lastSlashPos = path.lastIndexOf('/');
        return lastSlashPos == -1 ? path : path.substring(lastSlashPos + 1);
    }

}

