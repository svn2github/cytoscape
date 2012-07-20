package org.cytoscape.cytobridge.json;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

import org.cytoscape.cytobridge.NetworkManager;

import com.google.gson.Gson;
	
public class MyJSON implements Runnable{

		private NetworkManager myManager;
	
		public MyJSON(NetworkManager myManager) {
			this.myManager = myManager;
		}
		
		public void run() {
			try {
			/** True while this PostListener should listen. */
			boolean run = true;

			// Try to connect to the port.
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(4444);
			} catch (IOException e) {
				System.err.println("Could not listen on port: 4444.");
				System.exit(1);
			}

			Socket clientSocket = null;
			BufferedReader in = null;

			// Listen for Clients.
			while(run) {
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					System.err.println("Accept failed.");
					System.exit(1);
				}

				System.out.println("waiting");
				in = new BufferedReader(
						new InputStreamReader(
								clientSocket.getInputStream()));
				System.out.println("waiting2");
				String line;
				Gson gson = new Gson();
				while ((line = in.readLine()) != null) {
					System.out.println("waiting3");
					if (line.equals("die")) {
						System.out.println("Got kill signal!");
						run = false;
						break;
					}
					
					Helper test = gson.fromJson(line,Helper.class);
					myManager.pushNetwork(test.network_name, test.node_cytobridge_ids, test.edge_cytobridge_ids, test.edge_source_cytobridge_ids, test.edge_target_cytobridge_ids);
				}
			}

			// Clean up
			System.out.println("Terminating Server...");
			in.close();
			clientSocket.close();
			serverSocket.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
}
