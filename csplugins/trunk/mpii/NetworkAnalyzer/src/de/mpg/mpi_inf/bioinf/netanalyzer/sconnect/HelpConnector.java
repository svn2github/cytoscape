package de.mpg.mpi_inf.bioinf.netanalyzer.sconnect;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.io.SettingsSerializer;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.settings.PluginSettings;

/**
 * Controller class providing static methods that construct URLs to specific online help sections.
 * 
 * @author Yassen Assenov
 */
public final class HelpConnector {

	/**
	 * Gets the full URL for network interpretation.
	 * 
	 * @return URL for online help on network interpretation.
	 */
	public static String getInterpretURL() {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpInterpret();
	}

	/**
	 * Gets the full URL for complex parameter definition.
	 * 
	 * @param aID ID of the complex parameter of interest.
	 * @return URL for online help on the given complex parameter.
	 */
	public static String getParamURL(String aID) {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpParams() + "#" + aID;
	}

	/**
	 * Gets the full URL for removing duplicated edges.
	 * 
	 * @return URL for online help on removing duplicated edges.
	 */
	public static String getRemDuplicatesURL() {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpRemDuplicates();
	}

	/**
	 * Gets the full URL for removing self-loops.
	 * 
	 * @return URL for online help on removing self-loops.
	 */
	public static String getRemSelfloopsURL() {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpRemSelfloops();
	}

	/**
	 * Gets the full URL for plugin's settings.
	 * 
	 * @return URL for online help on NetworkAnalyzer's settings.
	 */
	public static String getSettingsURL() {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpSettings();
	}

	/**
	 * Gets the full URL for fitting functions.
	 * 
	 * @return URL for online help on NetworkAnalyzer's fitting functions.
	 */
	public static String getFittingURL() {
		final PluginSettings settings = SettingsSerializer.getPluginSettings();
		return getBaseUrl(settings) + settings.getHelpFitting();
	}

	/**
	 * Gets the help base URL.
	 * 
	 * @param aSettings Plugin settings instance, storing help URL and section names.
	 * @return Base URL for help documents, ending with <code>/</code> (or <code>\</code>, if
	 *         help is stored locally on a windows machine).
	 */
	private static String getBaseUrl(PluginSettings aSettings) {
		String baseURL = aSettings.getHelpUrlString();
		HttpURLConnection.setFollowRedirects(false);
		try {
			URL url = new URL(baseURL);
			URLConnection conn = url.openConnection();
			if (conn instanceof HttpURLConnection) {
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setRequestMethod("HEAD");
				httpConn.connect();
				String location = httpConn.getHeaderField("Location");
				if (location != null && (!"".equals(location))) {
					baseURL = location;
				}
				httpConn.disconnect();
			}
		} catch (Exception ex) {
			// MalformedURLException, ...
			// Fall through
		} finally {
			HttpURLConnection.setFollowRedirects(true);
		}
		final String endChar = baseURL.indexOf('\\') != -1 ? "\\" : "/";
		if (!baseURL.endsWith(endChar)) {
			baseURL += endChar;
		}
		return baseURL;
	}
}
