package org.cytoscape.cytobridge.json;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

import org.cytoscape.cytobridge.NetworkManager;

import com.google.gson.Gson;
	
public class MyJSON implements Runnable{

		private static final String PACKAGE = "org.cytoscape.cytobridge.json.";
		
		private NetworkManager myManager;
	
		public MyJSON(NetworkManager myManager) {
			this.myManager = myManager;
		}
		
		public void run() {
			try {
			/** True while this should listen. */
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

				in = new BufferedReader(
						new InputStreamReader(
								clientSocket.getInputStream()));

				String line;
				Gson gson = new Gson();
				while ((line = in.readLine()) != null) {
					if (line.equals("die")) {
						System.out.println("Got kill signal!");
						run = false;
						break;
					}
					Helper helper = gson.fromJson(line, Helper.class);
					
					JSONCommand jcom = (JSONCommand)gson.fromJson(line,Class.forName(PACKAGE+helper.getName()));
					jcom.run(myManager);
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
