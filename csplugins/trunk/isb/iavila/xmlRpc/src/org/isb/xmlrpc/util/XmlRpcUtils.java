package org.isb.xmlrpc.util;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import org.isb.xmlrpc.server.*;

import org.apache.xmlrpc.*;

/**
 * Class <code>XmlRpcUtils</code>
 * 
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @version 0.6 (Tue Sep 02 11:12:29 PDT 2003)
 */

public class XmlRpcUtils {

	
	/**
	 * Looks for a file named file_name:<br>
	 * - First in the user's home directory<br>
	 * - User's current directory (where the java command was invoked)<br>
	 * 
	 * @return the absolute path of the file, null if not found
	 */
	public static String FindPropsFile(String file_name) {
		
		boolean found = false;
		File file = null;
		
		// 1. Try the users home
		try {
			file = new File(System.getProperty("user.home"), file_name);
			if(file.exists()){
				System.out.println(file_name + " found at: " + file);
				found = true;
			}
		} catch (Exception e) {
			// not found in the current directory
			found = false;
		}

		// 2. Try the current working directory (where the Java command was invoked)
		if(!found){
			try {
				file = new File(System.getProperty("user.dir"), file_name);
				if(file.exists()){
					System.out.println(file_name + " found at: " + file);
					found = true;
				}
			} catch (Exception e) {
				// not found in the current directory
				found = false;
			}
		}

		if (!found)
			return null;

		return file.getAbsolutePath();
	}// FindPropsFile

	/**
	 * @return a Hashtable with the given object's class fields as keys
	 *         (Strings) and their values as one of the XML-RPC accepted values
	 *         as Java complex types (so if it is an int, in the Hash it will be
	 *         an Integer) it also adds ("className",obj.getClass().getName())
	 *         entry to the hash
	 */
	public static Hashtable GetObjectAsStruct(Object obj) {
		Hashtable out = new Hashtable();
		try {
			Class c = obj.getClass();

			Field fields[] = c.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
				out.put(fields[i].getName(), translateField(
						fields[i].getName(), fields[i].get(obj)));

			out.put("className", c.getName());

		} catch (Exception e) {
			out.clear();

			try { // as a last ditch effort, encode it into a byte array
				// (xml-rpc can handle this)
				out.put("encoded", GetObjectAsBytes(obj));
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return out;
	}

	/**
	 * Recreates the object that the given hash represents, the hash contains
	 * the fields of the object's class mapped to their Java complex type values
	 * (which as "untranslated" to create the object) and also it should contain
	 * an ("className",obj.getClass().getName()) entry so that the object can be
	 * recreated
	 */
	public static Object GetObjectFromStruct(Hashtable tab) {
		Object out = null;
		try {
			String className = (String) tab.get("className");
			Class c = Class.forName(className);

			Constructor constr = c.getDeclaredConstructor(null);
			if (constr != null)
				out = constr.newInstance(null);
			else
				throw new Exception("Could not create new object of class "
						+ className);

			Field fields[] = c.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
				fields[i].set(out, untranslateField(fields[i].getName(), tab
						.get(fields[i].getName()), fields[i].getType()));

		} catch (Exception e) {
			out = null;

			try { // as a last ditch effort, see if it was encoded into a byte
					// array
				// (xml-rpc can handle this)
				out = GetObjectFromBytes((byte[]) tab.get("encoded"));
			} catch (Exception ee) {
				System.err.println(ee.toString());
			}
		}
		return out;
	}

	/**
	 * @param fname
	 *            field name (used to throw an informative exception if the
	 *            inobj is not of one of the supported XML-RPC type)
	 * @param inobj
	 *            the object to be transtaled to a Java complex type (String,
	 *            Boolean, Integer, Double, Hashtable, Vector, or
	 *            java.util.Date)
	 * @return the translated object
	 */
	protected static Object translateField(String fname, Object inobj)
			throws Exception {
		if (inobj instanceof String || inobj instanceof Boolean
				|| inobj instanceof Integer || inobj instanceof Double
				|| inobj instanceof Hashtable || inobj instanceof Vector
				|| inobj instanceof java.util.Date)
			return inobj;
		if (inobj instanceof Float)
			return new Double((double) ((Float) inobj).floatValue());
		else if (inobj instanceof Long)
			return new Integer((int) ((Long) inobj).longValue());
		else if (inobj instanceof Short)
			return new Integer((int) ((Short) inobj).shortValue());
		throw new Exception("Object type " + inobj.getClass() + " for field "
				+ fname + " not supported by XML-RPC.");
	}

	/**
	 * @param fname
	 *            the field name for informative exception message
	 * @param inobj
	 *            the object to untranslate
	 * @param toType
	 *            the type to which to convert inobj
	 * @return an object with one of the types that XML-RPC accepts as a simple
	 *         Java type (String, boolean, int, double, Hashtable, Vector,
	 *         java.util.Date)
	 */
	protected static Object untranslateField(String fname, Object inobj,
			Class toType) throws Exception {
		if (toType.equals(String.class) || toType.equals(boolean.class)
				|| toType.equals(int.class) || toType.equals(double.class)
				|| toType.equals(Hashtable.class)
				|| toType.equals(Vector.class)
				|| toType.equals(java.util.Date.class))
			return inobj;
		if (toType.equals(float.class))
			return new Float((float) ((Double) inobj).doubleValue());
		else if (toType.equals(long.class))
			return new Long((long) ((Integer) inobj).intValue());
		else if (toType.equals(short.class))
			return new Short((short) ((Integer) inobj).intValue());
		throw new Exception("Object type " + toType + " for field " + fname
				+ " not supported by XML-RPC.");
	}

	public static byte[] GetObjectAsBytes(Object obj) {
		return GetObjectAsBytes(obj, false);
	}

	public static byte[] GetObjectAsBytes(Object obj, boolean gzip) {
		if (!(obj instanceof Serializable))
			return new byte[0];
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			java.util.zip.GZIPOutputStream gos = gzip ? new java.util.zip.GZIPOutputStream(
					bos)
					: null;
			ObjectOutputStream out = gzip ? new ObjectOutputStream(gos)
					: new ObjectOutputStream(bos);
			out.writeObject(obj);
			bos.flush();
			if (gos != null) {
				gos.finish();
				gos.flush();
			}
			out.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	public static Object GetObjectFromBytes(byte bytes[]) {
		try {
			InputStream bis = new BufferedInputStream(new ByteArrayInputStream(
					bytes));
			ObjectInputStream in = new ObjectInputStream(
					new java.util.zip.GZIPInputStream(bis));
			return in.readObject();
		} catch (Exception e) {
			try {
				InputStream bis = new BufferedInputStream(
						new ByteArrayInputStream(bytes));
				ObjectInputStream in = new ObjectInputStream(bis);
				return in.readObject();
			} catch (Exception ee) {
				return null;
			}
		}
	}

	public static boolean isServiceRunning(String service, String hostURL) {
		boolean running = false;
		try {
			XmlRpcClient client = new XmlRpcClient(hostURL);
			Boolean out = (Boolean) client.execute("server.hasService",
					new Vector());
			running = out.booleanValue();
		} catch (Exception e) {
			running = false;
		}
		return running;
	}

	/**
	 * If the service is not running, it adds it to the server at hostURL by
	 * calling addService(service,className,args)
	 */
	// TODO: Redo this? It is called in DataClientFactory
	public static boolean startService(String service, String hostURL,
			String className, String[] args) {
		
		if (isServiceRunning(service, hostURL))
			return true;
		
		boolean out = false;
		
		try {
			XmlRpcClient client = new XmlRpcClient(hostURL);
			Vector argv = new Vector();
			argv.add(service);
			argv.add(className);
			if (args != null) {
				Vector v = new Vector();
				argv.add(v);
				for (int i = 0; i < args.length; i++)
					v.add(args[i]);
			}
			Boolean outb = (Boolean) client.execute("server.addService", argv);
			out = outb.booleanValue();
		} catch (Exception e) {
			out = false;
		}
		return out;
	}

	/**
	 * If the local wMyXmlRpcServer is not running locally, it starts it on the
	 * given port
	 */
	public static boolean startWebServerIfNeeded(int port) {
		if (isLocalWebServerRunning(port))
			return true;
		boolean out = false;
		try {
			MyWebServer server = new MyWebServer(port);
			if (server != null)
				out = true;
		} catch (Exception e) {
			out = false;
		}
		return out;
	}

	public static boolean isLocalWebServerRunning(int port) {
		boolean running = false;
		try {
			XmlRpcClient client = new XmlRpcClient("http://localhost:" + port);
			Boolean out = (Boolean) client.execute("server.status",
					new Vector());
			running = out.booleanValue();
		} catch (Exception e) {
			running = false;
		}
		return running;
	}

	/**
	 * Testing
	 */
	public static void main(String args[]) {
		Object testObj = new Serializable() {
			public int int1 = 3;

			public double d1 = 6.4;

			public long l1 = 13498753;

			public float f1 = (float) 1.234;

			public String name = "myname";

			// double makeItFail[] = new double[] { 4.5, 6.7, 8.9 };
			public String toString() {
				return int1 + " " + d1 + " " + l1 + " " + f1 + " " + name;
			}
		};

		System.out.println(testObj);
		Hashtable tab = GetObjectAsStruct(testObj);
		System.out.println(tab);

		Object obj = GetObjectFromStruct(tab);
		System.out.println(obj.toString());
	}
}
