package cytoscape.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

// Provides some utility methods for the BDS classes.
//
public class BioDataServerUtil {

	private static final String NCBI_TAXON_SERVER = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=";

	private static final String TAXON_FILE = "tax_report.txt";

	// Constructor
	public BioDataServerUtil() {
	}

	/*
	 * Takes readers (for tax_report and gene_association) and returns species
	 * name in the GA file.
	 */
	public String getSpecies(final BufferedReader taxRd,
			final BufferedReader gaRd) throws IOException {
		String sp = null;
		String curLine = null;

		while (null != (curLine = gaRd.readLine().trim())) {
			// Skip comment
			if (curLine.startsWith("!")) {
				// do nothing
				// System.out.println("Comment: " + curLine);
			} else {
				StringTokenizer st = new StringTokenizer(curLine, "\t");
				while (st.hasMoreTokens()) {
					String curToken = st.nextToken();
					if (curToken.startsWith("taxon")) {
						st = new StringTokenizer(curToken, ":");
						st.nextToken();
						curToken = st.nextToken();
						st = new StringTokenizer(curToken, "|");
						curToken = st.nextToken();
						// System.out.println("Taxon ID found: " + curToken);
						sp = curToken;
						sp = taxIdToName(sp, taxRd);
						taxRd.close();
						gaRd.close();
						return sp;
					}
				}
			}

		}

		taxRd.close();
		gaRd.close();
		return sp;
	}

	// Convert taxonomy ID number to species name.
	// taxId is an NCBI taxon ID
	// All info about taxonomy ID is availabe at:
	// http://www.ncbi.nlm.nih.gov/Taxonomy/TaxIdentifier/tax_identifier.cgi
	//
	public String taxIdToName(String taxId, final BufferedReader taxRd)
			throws IOException {
		String name = null;
		String curLine = null;

		taxRd.readLine();

		while (null != (curLine = taxRd.readLine().trim())) {
			StringTokenizer st = new StringTokenizer(curLine, "|");
			String[] oneEntry = new String[st.countTokens()];
			int counter = 0;

			while (st.hasMoreTokens()) {
				String curToken = st.nextToken().trim();
				oneEntry[counter] = curToken;
				counter++;
				if (curToken.equals(taxId)) {
					name = oneEntry[1];
					return name;
				}
			}
		}
		return name;
	}

	public String checkSpecies(BufferedReader gaReader,
			BufferedReader taxonFileReader) throws IOException {

		String txName = null;
		// Get taxon name

		txName = getSpecies(taxonFileReader, gaReader);
		if (txName == null) {
			System.out
					.println("Warning: Cannot recognized speices.  Speices field is set to \"unknown.\"");
			System.out
					.println("Warning: Please check your tax_report.txt file.");
			txName = "unknown";
		}

		return txName;
	}

	// Returns taxon Map
	public HashMap getTaxonMap(File taxonFile) throws IOException {
		HashMap taxonMap = null;

		String name = null;
		String curLine = null;

		if (taxonFile.canRead() == true) {
			final BufferedReader taxonFileRd = new BufferedReader(
					new FileReader(taxonFile));

			taxonFileRd.readLine();

			while (null != (curLine = taxonFileRd.readLine().trim())) {
				StringTokenizer st = new StringTokenizer(curLine, "|");
				String[] oneEntry = new String[st.countTokens()];
				int counter = 0;

				while (st.hasMoreTokens()) {
					String curToken = st.nextToken().trim();
					oneEntry[counter] = curToken;
					counter++;
					name = oneEntry[1];
					taxonMap.put(curToken, name);
				}
			}

		}

		return taxonMap;
	}

	/*
	 * For a given taxon ID, returns species name. This method connects to
	 * NCBI's Taxonomy server, so Internet connection is required.
	 * 
	 * Kei
	 */
	protected String getTaxonFromNCBI(String id) throws MalformedURLException {
		String txName = null;
		URL taxonURL = null;
		BufferedReader htmlPageReader = null;
		String curLine = null;

		String targetId = id + "&lvl=0";

		taxonURL = new URL(NCBI_TAXON_SERVER + targetId);
		try {
			htmlPageReader = new BufferedReader(new InputStreamReader(taxonURL
					.openStream()));
			while ((curLine = htmlPageReader.readLine().trim()) != null) {
				// System.out.println("HTML:" + curLine);
				if (curLine.startsWith("<title>Taxonomy")) {
					System.out.println("HTML:" + curLine);
					StringTokenizer st = new StringTokenizer(curLine, "(");
					st.nextToken();
					curLine = st.nextToken();
					st = new StringTokenizer(curLine, ")");
					txName = st.nextToken().trim();
					System.out.println("Fetch result: NCBI code " + id + " is "
							+ txName);
					return txName;
				}

			}
			htmlPageReader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txName;
	}

}
