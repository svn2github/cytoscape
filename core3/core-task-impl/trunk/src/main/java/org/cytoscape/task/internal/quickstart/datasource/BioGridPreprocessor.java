package org.cytoscape.task.internal.quickstart.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.cytoscape.property.CyProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BioGridPreprocessor implements InteractionFilePreprocessor {

	private static final Logger logger = LoggerFactory
			.getLogger(BioGridPreprocessor.class);

	private static final String DB_NAME = "BioGRID";
	private static final String SOURCE_URL = "http://thebiogrid.org/downloads/archives/Release%20Archive/BIOGRID-3.1.74/BIOGRID-ORGANISM-3.1.74.mitab.zip";

	private static final String INTERACTION_DIR_NAME = "interactions";
	private static final String DEF_USER_DIR = System.getProperty("user.home");

	private final PsiMiToSif p2s;
	private final File dataFileDirectory;

	private final Map<String, URL> sourceMap;

	private URL sourceFileLocation;

	private boolean isLatest;

	public BioGridPreprocessor(final CyProperty<Properties> properties) {
		this.isLatest = false;

		this.p2s = new PsiMiToSif();
		this.sourceMap = new HashMap<String, URL>();
		try {
			sourceFileLocation = new URL(SOURCE_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (properties == null)
			throw new NullPointerException("Property service is null.");

		final Properties props = properties.getProperties();

		if (props == null)
			throw new NullPointerException("Property is missing.");

		String configDirectory = props
				.getProperty(CyProperty.DEFAULT_CONFIG_DIR);
		if (configDirectory == null || configDirectory.trim().length() == 0)
			configDirectory = DEF_USER_DIR;

		final File configFileLocation = new File(configDirectory,
				CyProperty.DEFAULT_CONFIG_DIR);
		this.dataFileDirectory = new File(configFileLocation,
				INTERACTION_DIR_NAME);
		if (!dataFileDirectory.exists())
			dataFileDirectory.mkdir();

		logger.debug("BioGrid interaction data directory: "
				+ dataFileDirectory.toString());
	}

	@Override
	public void processFile(URL source) throws IOException {
		if (source != null)
			this.sourceFileLocation = source;

		boolean test = isUpToDate();
		if (!test) {
			final InputStream is = sourceFileLocation.openStream();
			extractEntrey(is);
		}
	}

	private boolean isUpToDate() throws IOException {
		final File[] files = this.dataFileDirectory.listFiles();
        boolean up2date = false;
        
		for (File file : files) {
			final String name = file.getName();			
			final Pattern pattern = Pattern.compile("BIOGRID");
			final Matcher matcher = pattern.matcher(name);
			boolean test = matcher.find();
			if (test) {
				up2date = true;
				this.sourceMap.put(createFileName(name), file.toURI().toURL());
			}
		}
		return up2date;
	}

	private void extractEntrey(final InputStream sourceInputStream)
			throws IOException {
		ZipInputStream zis = new ZipInputStream(sourceInputStream);
		try {

			// Extract list of entries
			ZipEntry zen = null;
			String entryName = null;

			while ((zen = zis.getNextEntry()) != null) {
				entryName = zen.getName();
				File outFile = new File(dataFileDirectory, entryName + ".sif");
				outFile.createNewFile();
				FileWriter outWriter = new FileWriter(outFile);
				String line;
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(zis));

				int count = 0;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("#"))
						continue;
					outWriter.write(p2s.toSif(line) + "\n");
					count++;
				}
				zis.closeEntry();
				outWriter.close();
				this.sourceMap.put(createFileName(entryName), outFile.toURI()
						.toURL());
				logger.debug("Entries: " + count);
			}

		} finally {
			if (zis != null)
				zis.close();
			zis = null;
		}
	}

	private String createFileName(final String originalFileName) {
		final String sourceName = DB_NAME + ": "
				+ originalFileName.split("-")[2] + " Interactome";
		return sourceName;
	}

	@Override
	public boolean isLatest() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Map<String, URL> getDataSourceMap() {
		return sourceMap;
	}

}
