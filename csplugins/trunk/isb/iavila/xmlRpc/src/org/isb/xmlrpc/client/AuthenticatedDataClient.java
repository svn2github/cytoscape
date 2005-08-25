package org.isb.xmlrpc.client;

import java.util.*;
import java.io.IOException;
import org.apache.xmlrpc.*;

/**
 * Class <code>AuthenticatedDataClient</code> holds the username and the
 * password for a user, adds a user name and a password as the 1st two elements
 * in the parameter vector for a remote call to the server before calling MyDataClient.execute
 * 
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public abstract class AuthenticatedDataClient extends MyDataClient {

	protected String username = null, password = null;

	/**
	 * @param server_url
	 *            the URL for the client
	 */
	public AuthenticatedDataClient(String server_url) throws XmlRpcException,
			java.net.MalformedURLException {
		super(server_url);
	}

	public AuthenticatedDataClient(String server_url, String username,
			String password) throws XmlRpcException,
			java.net.MalformedURLException {
		super(server_url);
		setUserNamePassword(username, password);
	}

	public void setUserNamePassword(String uname, String passwd) {
		username = uname;
		password = passwd;
	}

	/**
	 * Asks the server for the user level for this user.
	 * 
	 * @return -1 if the username and password have not been set
	 */
	public synchronized int getUserLevel() throws XmlRpcException, IOException {
		if (username == null)
			return -1;
		Vector v = new Vector();
		v.add(username);
		return ((Integer) super.execute("server.getUserLevel", v)).intValue();
	}

	/**
	 * Inserts the username and the password (if not null) into params and then
	 * executes the method
	 */
	public synchronized Object execute(String method, Vector params)
			throws XmlRpcException, IOException {
		if (password != null) {
			params.insertElementAt(password, 0);
		}
		if (username != null) {
			params.insertElementAt(username, 0);
		}
		return super.execute(method, params);
	}

}
