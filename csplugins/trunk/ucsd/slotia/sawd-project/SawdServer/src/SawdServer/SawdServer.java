package SawdServer;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * A class that starts a server socket and starts
 * Servicer threads to handle incoming client connections.
 **/
public class SawdServer
{
	static final int DEFAULT_PORT = 2626;
	static final String DEFAULT_SERIALIZE_FILE_PATH = ".SawdServer.persist";

	static PrintStream logger = null;
	static DateFormat dateFormatter = null;

	public static void main(String[] args)
	{
		// Read port info
		int port = DEFAULT_PORT;
		if (args.length >= 1)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e)
			{
				System.err.println("Could not read port number.");
				printUsageAndExit();
			}
		}

		// Setup logger
		dateFormatter = new SimpleDateFormat("MMM d HH:mm:ss:SSS");
		logger = System.err;

		// Start the server
		SawdServer server = new SawdServer(DEFAULT_SERIALIZE_FILE_PATH);
		server.start(port);
	}

	private static void printUsageAndExit()
	{
		System.err.println("Usage: SuadServer [port=2626]");
		System.exit(1);
	}

	public static synchronized void log(String message)
	{
		logger.println("[" + dateFormatter.format(new Date()) + "] " + message);
	}

	Graphs graphs = null;
	Selector selector = null;
	List<Servicer> servicers = new LinkedList<Servicer>();
	String filePath;

	public SawdServer(String filePath)
	{
		this.filePath = filePath;

		// Deserialize graphs object
		try
		{
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			graphs = (Graphs) ois.readObject();
			ois.close();
		}
		catch (Exception e)
		{
			log("Persist file not used: " + e.getMessage());
			graphs = new Graphs();
		}
	}

	public void start(int port)
	{
		// Setup shutdown handler
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHandler()));

		// Create listening socket
		ServerSocketChannel serverChannel = null;
		try
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch (IOException e)
		{
			log("Failed to start server: could not create listening socket: " + e.getMessage());
			System.exit(2);
		}

		log("Server started on port " + port);

		// Start accepting connections
		while (true)
		{
			try
			{
				if (selector.select() == 0) break;
			}
			catch (IOException e)
			{
				log("Failed to select for events: " + e.getMessage());
				continue;
			}
			
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext())
			{
				SelectionKey selectionKey = iterator.next();
				if (!selectionKey.isAcceptable()) continue;
				iterator.remove();

				SocketChannel clientChannel = null;
				try
				{
					clientChannel = serverChannel.accept();
				}
				catch (IOException e)
				{
					log("Failed to initiate communication with incoming connection from client: " + e.getMessage());
				}
				catch (SecurityException e)
				{
					log("No permission to initiate communication with incoming connection from client: " + e.getMessage());
					System.exit(2);
				}

				Servicer servicer = new Servicer(clientChannel.socket(), graphs);
				servicers.add(servicer);
				Thread servicerThread = new Thread(servicer);
				servicerThread.start();
			}
		}

		log("Stopped listening for incoming connections");
	}

	public void stop()
	{
		if (selector != null)
			selector.wakeup();
	}

	class ShutdownHandler implements Runnable
	{
		public void run()
		{
			// Stop the threads
			SawdServer.this.stop();
			for (Servicer servicer : servicers)
				servicer.stop();
			//thaw();

			// Serialize graphs
			try
			{
				FileOutputStream fos = new FileOutputStream(filePath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(graphs);
				oos.close();
			}
			catch (IOException e)
			{
				log(String.format("Failed to write persist file \'%s\': %s", filePath, e.getMessage()));
			}
		}
	}

	static Servicer owner = null;
	static Object lock = new Object();

	public static void freeze(Servicer newOwner)
	{
		owner = newOwner;
	}

	public static void thaw()
	{
		owner = null;
		lock.notifyAll();
	}

	public static void enter(Servicer servicer)
	{
		if (owner != null && owner != servicer)
		{
			try
			{
				lock.wait();
			}
			catch (InterruptedException e) {}
		}
	}
}
