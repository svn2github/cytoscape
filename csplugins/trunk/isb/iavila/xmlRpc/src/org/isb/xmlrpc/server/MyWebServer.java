package org.isb.xmlrpc.server;

import java.io.IOException;
import java.util.*;
import org.apache.xmlrpc.WebServer;

/**
 * Class <code>MyWebServer</code>, keeps hashes of services (service name to
 * handler), users (username to password), and user levels (username to
 * Integers).
 * <p>
 * It also contains a <code>org.apache.xmlrpc.WebServer</code> that knows what
 * service goes to what handler.
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 */

// TODO:
// - if not path given for xmlrpc_props, then see
// XmlRpcUtils.FindXmlRpcPropsFile?
public class MyWebServer {

	public static final String SERVICE_NAME = "server";

	/**
	 * The name of the file that contais information about services and their
	 * handlers the format of this file is as follows:<br>
	 * 
	 * handler.<service name> = <handler fully described class><br>
	 * handler.<service name>.<handler argument> = <argument><br>
	 * For example:<br>
	 * handler.interaction = org.isb.xmlrpc.handlers.InteractionHandler<br>
	 * handler.interaction.url=jdbc:mysql:mysql://biounder.kaist.ac.kr<br>
	 */
	public static final String DEFAULT_PROPS_FILE = "xmlrpc.props";

	protected WebServer webserver;

	protected Hashtable services, users, levels;

	/**
	 * Starts this server on the port given at args[0] and with an optional
	 * xmlrpc.props file given at args[1]
	 */
	public static void main(String args[]) {

		if (args.length < 1) {
			System.err
					.println("Usage: java MyWebServer <port> <optional: xmlrpc.props file>");
			System.exit(-1);
		}

		try {
			new MyWebServer(args);
		} catch (IOException e) {
			System.err.println("Could not start server: " + e.getMessage());
		}
	}

	/**
	 * Starts this server on the given port, adds itself as a handler for the
	 * SERVICE_NAME service.
	 * 
	 * @param port
	 *            the port that the web-server uses to listen to XML-RPC
	 *            requests
	 * @param xmlrpc_props
	 *            the xmlrpc.props file that contains information on available
	 *            services and their handlers (possibly null, in which case the
	 *            default xmlrpc.props file is searched for)
	 * @throws IOException
	 *             if the web-server can't start on the specified port
	 */
	public MyWebServer(int port, String xmlrpc_props) throws IOException {

		// XmlRpc.setDebug( true );

		System.out.print("Attempting to start XML-RPC server on port " + port
				+ "...");
		webserver = new WebServer(port);
		System.out.println("...success!");

		System.out.print("Registering the server as a handler (service \""
				+ SERVICE_NAME + "\")...");
		webserver.addHandler(SERVICE_NAME, this);
		System.out.println("...success!");

		services = new Hashtable();
		services.put(SERVICE_NAME, this);

		if (xmlrpc_props == null) {
			xmlrpc_props = XmlRpcUtils.FindPropsFile(DEFAULT_PROPS_FILE);
		}
		if (xmlrpc_props != null) {
			addHandlersFromProps(xmlrpc_props);
		}

		webserver.start();
	}

	/**
	 * If args.length == 2, calls this(args[0], args[1]), else it calls
	 * this(args[0], null)
	 */
	public MyWebServer(String args[]) throws IOException {
		if (args.length == 2) {
			this(Integer.parseInt(args[0]), args[1]);
		} else {
			this(Integer.parseInt(args[0], null));
		}
	}

	/**
	 * Parses the given xmlrpc.props file for services and their handlers, and
	 * adds the handlers to the service, the file specifies services and their
	 * handlers as follows
	 * <p>
	 * 
	 * handler.<service name> = <handler fully described class><br>
	 * handler.<service name>.<handler argument> = <argument><br>
	 * For example:<br>
	 * handler.interaction = org.isb.xmlrpc.handlers.InteractionHandler<br>
	 * handler.interaction.url=jdbc:mysql:mysql://biounder.kaist.ac.kr<br>
	 * If existent, the handler arguments and their values will be added as
	 * entries in a Hashtable and then addService(serviceName,className,
	 * hashtable) will be called
	 */
	protected void addHandlersFromProps(String xmlrpc_props) throws IOException {

		Properties props = MyUtils.readProperties(xmlrpc_props);

		Enumeration propertyNames = props.propertyNames();
		HashMap serviceToClass = new HashMap();
		HashMap serviceToArgs = new HashMap();
		while (propertyNames.hasMoreElements()) {

			String name = (String) propertyNames.nextElement();

			if (name.startsWith("handler.")) {

				String[] split = name.split(".");

				if (split.length == 3) {
					// this is an argument for the service
					String serviceName = split[1];
					Hashtable table = (Hashtable) serviceToArgs
							.get(serviceName);
					if (table == null) {
						table = new Hashtable();
						serviceToArgs.put(serviceName, table);
					}
					table.put(split[2], props.getProperty(name));

				} else if (split.length == 2) {
					String handlerClass = props.getProperty(name);
					serviceToClass.put(split[1], handlerClass);
				} else {
					System.out.println("Unrecognized property in file:" + name);
				}

			}// name starts with "handler"

		}// while propertyNames

		// Now actually add the handlers:
		Iterator it = serviceToClass.keySet().iterator();
		while (it.hasNext()) {
			String serviceName = (String) it.next();
			String className = (String) serviceToClass.get(serviceName);
			Hashtable args = (Hashtable) serviceToArgs.get(serviceName);
			if (args == null)
				addService(serviceName, className);
			else
				addService(serviceName, className, args);
		}// while it

	}

	/**
	 * @return the <code>org.apache.xmlrpc.WebServer</code> wrapped by this
	 *         <code>MyWebServer</code>
	 */
	public WebServer getServer() {
		return webserver;
	}

	/**
	 * Adds the given ip as an accepted client, getServer().setParanoid(true) is
	 * called so no other ips will be accepted after this call
	 */
	public boolean addAllowedClientIP(String ip) {
		getServer().setParanoid(true);
		getServer().acceptClient(ip);
		return true;
	}

	/**
	 * Adds the given ip as a denied client, getServer().setParanoid(true) is
	 * called so no ips will be accepted after this call (see
	 * addAllowedClientIP)
	 */
	public boolean addDeniededClientIP(String ip) {
		getServer().setParanoid(true);
		getServer().denyClient(ip);
		return true;
	}

	/**
	 * For the given service, it returns a Vector of Strings of the available
	 * methods for that service
	 */
	public Vector listServiceCommands(String service) {

		Vector out = new Vector();

		if (services.get(service) == null)
			return out;

		Object obj = services.get(service);

		if (obj instanceof AuthenticatedInvoker)
			obj = ((AuthenticatedInvoker) obj).getTarget();

		java.lang.reflect.Method methods[] = obj.getClass().getMethods();

		String cname = obj.getClass().getName();
		String sname = obj.getClass().getSuperclass().getName();

		for (int i = 0; i < methods.length; i++) {

			String mname = methods[i].toString();

			if (mname.indexOf("java.lang.Object.") > 0)
				continue;

			mname = mname.replaceAll("public ", "");
			mname = mname.replaceAll("static ", "");
			mname = mname.replaceAll("native ", "");
			mname = mname.replaceAll("final ", "");
			mname = mname.replaceAll("java.lang.", "");
			mname = mname.replaceAll("java.util.", "");
			mname = mname.replaceAll(cname + ".", "");
			mname = mname.replaceAll(sname + ".", "");

			if (mname.indexOf("throws ") > 0)
				mname = mname.substring(0, mname.indexOf("throws ") - 1);

			if (mname.startsWith("void "))
				continue;

			out.add(mname);
		}
		return out;
	}

	/**
	 * @return Vector of Strings that contains the names of the services offered
	 */
	public Vector listServices() {
		Vector out = new Vector();
		if (services != null)
			out.addAll(services.keySet());
		return out;
	}

	/**
	 * @return Hashtable with String keys (the sevice name) and String values
	 *         (the name of the class that handles that service)
	 */
	public Hashtable getServices() {
		Hashtable out = new Hashtable();
		java.util.Enumeration e = services.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object obj = services.get(key);
			if (obj instanceof AuthenticatedInvoker)
				obj = ((AuthenticatedInvoker) obj).getTarget();
			out.put(key, obj.getClass().getName());
		}
		return out;
	}

	public boolean hasService(String service) {
		return services.get(service) != null;
	}

	/**
	 * Level refers to the access level to DBs, if level <= 0, the user,level
	 * entry is not saved in "levels"
	 */
	public boolean addUserNamePassword(String user, String pass, int level) {
		System.err.println("USERNAME = " + user + "; PASSWORD = " + pass
				+ "; LEVEL = " + level);
		if (users == null)
			users = new Hashtable();
		users.put(user, pass);
		if (level > 0) {
			if (levels == null)
				levels = new Hashtable();
			levels.put(user, new Integer(level));
		}
		return true;
	}

	/**
	 * Calls addUserNamePassword(uname,passwd, -1)
	 */
	public void addUserNamePassword(String uname, String passwd) {
		addUserNamePassword(uname, passwd, -1);
	}

	/**
	 * @return -1 if the user is not added in this server, or if the user level
	 *         is <= 0
	 */
	public int getUserLevel(String uname) {
		return levels.containsKey(uname) ? ((Integer) levels.get(uname))
				.intValue() : -1;
	}

	/**
	 * If the handler for the service is already added, it does not add the new
	 * handler
	 * 
	 * @param service
	 *            String describing the service
	 * @param className
	 *            the fully specified class name of the handler for the service
	 */
	public boolean addService(String service, String className)
			throws Exception {
		Object obj = services.get(service);
		if (obj == null)
			return addService(service, Class.forName(className).newInstance());
		return false;
	}

	/**
	 * If the handler for the service is already added, it does not add the new
	 * handler
	 * 
	 * @param service
	 *            String describing the service
	 * @param className
	 *            the fully specified class name of the handler for the service
	 * @param args
	 *            a table of Strings to Strings,Integers,Boolean, or Doubles
	 */
	public boolean addService(String service, String className, Hashtable args)
			throws Exception {

		Object obj = services.get(service);

		if (obj == null) {
			try {
				Class handlerClass = Class.forName(className);

				// Look for a constructor that takes as a unique argument a
				// Hashtable
				Class[] types = new Class[1];
				types[0] = args.getClass();
				java.lang.reflect.Constructor constr = handlerClass
						.getDeclaredConstructor(args.getClass());

				if (constr != null) {
					Object handler = constr.newInstance(args);
					return addService(service, handler);
				} else {
					System.err
							.println("ERROR: REQUESTED CONSTRUCTOR NOT FOUND IN CLASS "
									+ className);
				}
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Make multiple XML-RPC calls in one request and receive multiple
	 * responses.
	 */
	public boolean addMultiCallService() throws Exception {
		boolean out = false;
		Object obj = services.get(SERVICE_NAME);
		if (obj == null) {
			SystemHandler system = new SystemHandler();
			system.addDefaultSystemHandlers();
			addService(SERVICE_NAME, system);
			return true;
		}
		return false;
	}

	/**
	 * @return false for now
	 * @param service
	 *            name of the service
	 * @param className
	 *            the class of the handler for the service
	 * @param handlerArgs
	 *            Vector of Strings
	 */
	public boolean addService(String service, String className,
			Vector handlerArgs) throws Exception {
		return false; // for now
	}

	/**
	 * If the handler for the service is already added, it does not add the new
	 * handler
	 * 
	 * @param service
	 *            name of the service
	 * @param handler
	 *            the handler for service
	 */
	public boolean addService(String service, Object handler) throws Exception {
		Object obj = services.get(service);
		if (obj == null) {
			String className = handler.getClass().getName();
			System.out.print("Registering a " + className
					+ " as a handler (service \"" + service + "\")...");
			if (users == null) {
				webserver.addHandler(service, handler);
				services.put(service, handler);
			} else {
				AuthenticatedInvoker ai = new AuthenticatedInvoker(handler);
				for (Iterator it = users.keySet().iterator(); it.hasNext();) {
					String user = (String) it.next();
					int level = levels.containsKey(user) ? ((Integer) levels
							.get(user)).intValue() : -1;
					ai.addUserNamePassword(user, (String) users.get(user),
							level);
				}
				webserver.addHandler(service, ai);
				services.put(service, ai);
			}

			System.out.println("...success!");
			return true;
		}
		return false;
	}

	/**
	 * @param service
	 *            the service to remove
	 */
	public boolean removeService(String service) {
		Object obj = services.get(service);
		if (obj != null) {
			System.out.println("Removing service \"" + service + "\".");
			services.remove(service);
			webserver.removeHandler(service);
			return true;
		}
		return false;
	}

	/**
	 * Shutdown this server
	 */
	public boolean shutdown() {
		System.out.println("Shutting down XML-RPC server.");
		webserver.shutdown();
		return true;
	}

	public boolean setDebug(String deb) {
		return debug(Boolean.valueOf(deb).booleanValue());
	}

	public boolean setDebug(boolean deb) {
		return debug(deb);
	}

	public boolean debug(boolean deb) {
		XmlRpc.setDebug(deb);
		return deb;
	}

	/**
	 * @return true always
	 */
	public boolean status() {
		return true;
	}

	/**
	 * Shutdown and return true
	 */
	public boolean exit() {
		webserver.shutdown();
		return true;
	}
}
