package cytoscape.plugin;


import cytoscape.logger.CyLogger;


public final class PluginVersionUtils {
	public static final String VALID_CYTOSCAPE_VERSION_PATTERN = "^\\d+\\.\\d+(\\.\\d+(-[a-zA-Z]+)?)?$";
	public static final int MINOR = 2;
	public static final String VERSION_SEPARATOR = "\\.";
	private static final CyLogger logger = CyLogger.getLogger(PluginVersionUtils.class);

	public static boolean isVersion(String vers, int vt) {
		String[] version = vers.split("\\.");
		if (version.length == 2 && version[1].equals("0"))
			version = new String[]{version[0]};

		return vt == version.length;
	}

	/**
	 * Return the newer of the two versions.
	 *
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static String getNewerVersion(String arg0, String arg1) {
		String MostRecentVersion = null;
		int max = 3;

		String[] SplitVersionA = arg0.split("\\.");
		String[] SplitVersionB = arg1.split("\\.");

		for (int i = 0; i < max; i++) {
			int a = 0;
			int b = 0;

			if (i == (max - 1)) {
				logger.debug("A length: " + SplitVersionA.length + " B length: " + SplitVersionB.length);
				a = (SplitVersionA.length == max) ? Integer
					.valueOf(SplitVersionA[i]) : 0;
				b = (SplitVersionB.length == max) ? Integer
					.valueOf(SplitVersionB[i]) : 0;
			} else {
				a = Integer.valueOf(SplitVersionA[i]);
				b = Integer.valueOf(SplitVersionB[i]);
			}

			if (a != b) {
				MostRecentVersion = (a > b) ? arg0 : arg1;
				break;
			}
		}
		return MostRecentVersion;
	}

	// this just checks the downloadable object version and the cytoscape version
	public static boolean versionOk(String version, final boolean downloadObj) {
		final String pattern = downloadObj ? "^\\d+\\.\\d+$" : VALID_CYTOSCAPE_VERSION_PATTERN;

		// Check to see if we've got a subversion number
		if (version.indexOf('.') < 0)
			version = version+".0";	// No, give it one
		return version.matches(pattern);
	}
}
