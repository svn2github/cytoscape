package edu.ucsd.bioeng.kono.keggbrowser.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import cytoscape.CytoscapeInit;
import cytoscape.actions.LoadNetworkTask;
import edu.ucsd.bioeng.kono.keggbrowser.KEGGBrowserPlugin;

public class KEGGDataFetcher {

	private Map<String, String> pathwaynameMap = new HashMap<String, String>();

	FTPClient client = new FTPClient();

	private static KEGGDataFetcher dataReader;
	
	static {
		dataReader = new KEGGDataFetcher();
	}
	
	public static Map<String, String> getFiles( String species) {
		return dataReader.getFileList(species);
	}
	
	
	private KEGGDataFetcher() {
		try {
			loadPathwayMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, String> getFileList(final String species) {

		Map<String, String> returnMap = new HashMap<String, String>();

		try {

			System.out.println("===========Start");
			int reply;
			client.connect("ftp.genome.jp");
			client.login("anonymous", "testuser@bar.org");

			System.out.println("SystemName:" + client.getSystemName());

			System.out.println("Reply: " + client.getReplyString());

			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = client.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
			client.changeWorkingDirectory("pub/db/community/biopax/" + species);
			System.out.println("WD: " + client.printWorkingDirectory());
		
			
			System.out.println("Trying to get file list=====================");

			FTPFile[] files = client.listFiles();
			System.out.println("Reply: " + client.getReplyString());
			System.out.println("GOT: " + files.length + "  files");
			
			
			String bioPaxFileName = null;
			String key = null;
			for (int i = 0; i < files.length; i++) {
				bioPaxFileName = files[i].getName();

				key = bioPaxFileName.split("\\.")[0].substring(3);
				System.out.println("------------File = "
						+ pathwaynameMap.get(bioPaxFileName.split("\\.")[0]
								.substring(3)));
				if (key != null && pathwaynameMap.get(key) != null) {
					returnMap.put(key, pathwaynameMap.get(key));
				}

			}

			
			client.logout();
			
		} catch (Exception e) {

		}
		

		return returnMap;
	}

	private void loadPathwayMap() throws IOException {

		URL resource = KEGGBrowserPlugin.class.getResource("/map_title.tab");
		BufferedReader in = new BufferedReader(new InputStreamReader(resource
				.openStream()));

		// BufferedReader in = new BufferedReader(new InputStreamReader(new
		// ImputStream(KEGGBrowserPlugin.class.getResource("map_title.tab")));

		String inputLine = null;

		while ((inputLine = in.readLine()) != null) {

			System.out.println("Line = " + inputLine);
			String[] parts = inputLine.split("\t");
			this.pathwaynameMap.put(parts[0], parts[1]);

		}

		in.close();
	}

	public void loadPathway(String fileName) throws IOException {
		
		System.out.println("------------------- Load: " + fileName);
		
		
		File tmpFile = new File(CytoscapeInit.getMRUD() + "/" + fileName);

		// now write the temp file
		BufferedWriter out = null;
		BufferedReader in = null;

		out = new BufferedWriter(new FileWriter(tmpFile));
		in = new BufferedReader(new InputStreamReader(client
				.retrieveFileStream(fileName)));

		String inputLine = null;

		while ((inputLine = in.readLine()) != null) {

			System.out.println("Line = " + inputLine);

			out.write(inputLine);
			out.newLine();
		}

		in.close();
		out.close();

		LoadNetworkTask.loadFile(tmpFile, false);

	}

}
