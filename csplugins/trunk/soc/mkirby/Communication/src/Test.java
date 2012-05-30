import java.net.*;
import java.io.*;

public class Test {
	public static void main(String[] args) throws IOException {

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
			while ((line = in.readLine()) != null) {
				System.out.println(line); 
				if ( line.endsWith("endRSend") ) {
					System.out.println("Got data!");
					break;
				}
				if ( line.equals("die") ) {
					System.out.println("Got kill signal!");
					run = false;
				}
			}
		}

		System.out.println("Terminating Server...");

		// Clean up
		in.close();
		clientSocket.close();
		serverSocket.close();
	}
}