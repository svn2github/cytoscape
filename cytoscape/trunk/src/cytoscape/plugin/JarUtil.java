package cytoscape.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import cytoscape.util.URLUtil;
import cytoscape.util.ZipUtil;
import java.util.zip.ZipFile;

/**
 * A utility class designed to capture methods used by multiple classes.
 */
class JarUtil {

	// Bug 2055 changing regexp used to match jars
	// Was "\\w+\\.jar", which seemed unecessarily restrictive
	static final String MATCH_JAR_REGEXP = ".*\\.jar$";

	/**
	 * Iterate through all class files, return the subclass of CytoscapePlugin.
	 * Similar to CytoscapeInit, however only plugins with manifest files that
	 * describe the class of the CytoscapePlugin are valid.
	 */
	static String getPluginClass(String fileName, PluginInfo.FileType type) throws IOException {

		String pluginClassName = null;

		try {

		switch (type) {
		case JAR:
			JarFile jar = new JarFile(fileName);
            try {
                pluginClassName = getManifestAttribute(jar.getManifest());
            } finally {
                if (jar != null) 
                    jar.close();
            }
			break;

		case ZIP:
			List<ZipEntry> Entries = ZipUtil
					.getAllFiles(fileName, MATCH_JAR_REGEXP);
			if (Entries.size() <= 0) {
				String[] FilePath = fileName.split("/");
				fileName = FilePath[FilePath.length - 1];
				throw new IOException(
						fileName
								+ " does not contain any jar files or is not a zip file.");
			}

            ZipFile zf = null;

            try {
				zf = new ZipFile(fileName);
                for (ZipEntry entry : Entries) {
                    String entryName = entry.getName();

                    InputStream is = null;

                    try {
                        JarInputStream jis = null;

						is = ZipUtil.readFile(zf, entryName);
                        try {
							jis = new JarInputStream(is);
                            pluginClassName = getManifestAttribute(jis.getManifest());
                        } finally {
                            if (jis != null) 
                                jis.close();
                        }
                    } finally {
                        if (is != null) 
                            is.close();
                    }
                }
            } finally {
                if (zf != null) 
                	zf.close();
            }
		}

		} catch (Throwable t) {
			throw new IOException(t);
		}

        return pluginClassName;
	}

	/*
	 * Gets the manifest file value for the Cytoscape-Plugin attribute
	 */
	static String getManifestAttribute(Manifest m) {
		String value = null;
		if (m != null) {
			value = m.getMainAttributes().getValue("Cytoscape-Plugin");
		}
		return value;
	}
}
