package org.isb.xmlrpc.client;

import java.util.*;
import java.io.IOException;
import org.apache.xmlrpc.*;
import org.isb.xmlrpc.util.*;

/**
 * Class <code>MyDataClient</code>, wraps an XmlRpcClient which makes
 * requests to the server through the execute method
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public abstract class MyDataClient {

	/**
	 * The name of the service that this client uses
	 */
	public String serviceName = "unknown";
    /**
     * Arguments to the method to be called
     */
	protected Vector args = new Vector();
    /**
     * The actual client that sends requests to the server
     */
	protected XmlRpcClient client;

	/**
	 * For multi-call settings
	 */
	protected int nCallsPerBatch = 0;
	protected Vector batchVector = null;
	protected boolean startedSystemHandler = false;

	/**
	 * @param server_url
	 *            the server URL to which this client connects
	 */
	public MyDataClient(String server_url) throws XmlRpcException,
			java.net.MalformedURLException {
		client = new XmlRpcClient(server_url);
        System.out.println("Successfully created a client to server with URL = " + client.getURL());
	}

	/**
	 * 
	 * @param method a String of the form "<service name>.<method name>" for example, "interactions.getSources"
	 * @param params the parameters for the method
	 * @return the object the method returns
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public synchronized Object execute(String method, Vector params)
			throws XmlRpcException, IOException {

		Object out = null;
		
		System.out.println("MyDataClient.execute(" + method + ", params = " + params + ")");
		
		if (nCallsPerBatch > 0)
			out = doMultiCall(method, params);
		else
			out = client.execute(method, params);
		return out;
	}

	/**
	 * If you're writing an XML-RPC client which makes lots of small function
	 * calls, you may discover that your round-trip time is fairly high, thanks
	 * to Internet backbone latency. Some servers allow you to batch up multiple
	 * requests using the following function.
	 */
	public synchronized void setMultiCall(int ncalls) throws XmlRpcException,
			IOException {
		nCallsPerBatch = ncalls;
		if (batchVector == null)
			batchVector = new Vector();
		else
			batchVector.clear();
		if (!startedSystemHandler) {
			startedSystemHandler = true;
			client.execute("server.addMultiCallService", batchVector);
		}
	}

	/**
	 * Executes any accumulated calls in the batchVector, and then clears it and
	 * sets nCallsPerBatch to 0
	 */
	public synchronized Vector endMultiCall() throws XmlRpcException,
			IOException {
		Vector out = null;
		if (batchVector.size() > 0)
			out = (Vector) client.execute("system.multicall", batchVector);
		else
			out = batchVector;
		batchVector.clear();
		nCallsPerBatch = 0;
		return out;
	}

	/**
	 * Adds to the vector of batch calls the given method and parameters and
	 * then does a multicall using that batch vector
	 */
	public synchronized Vector doMultiCall(String method, Vector params)
			throws XmlRpcException, IOException {
		Hashtable tab = new Hashtable();
		tab.put("methodName", method);
		tab.put("params", params.clone());
		batchVector.add(tab);
		if (nCallsPerBatch > 0 && batchVector.size() > 0
				&& batchVector.size() % nCallsPerBatch == 0) {
			Vector out = (Vector) client.execute("system.multicall",
					batchVector);
			batchVector.clear();
			return out;
		}
		return null;
	}

	/**
	 * 
	 * @param method a String of the form "<service name>.<method name>" for example, "interactions.getSources"
	 * @return the Object returned by the remote call
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public synchronized Object call(String method) throws XmlRpcException,
			IOException {
		args.clear();
		return execute(method, args);
	}

	/**
	 * 
	 * @param method a String of the form "<service name>.<method name>" for example, "interactions.getSources"
	 * @param arg0 an argument of the call
	 * @return the Object returned by the remote call
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public synchronized Object call(String method, Object arg0)
			throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1)
			throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1,
			Object arg2) throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		args.add(arg2);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1,
			Object arg2, Object arg3) throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1,
			Object arg2, Object arg3, Object arg4) throws XmlRpcException,
			IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1,
			Object arg2, Object arg3, Object arg4, Object arg5)
			throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		return execute(method, args);
	}

	public synchronized Object call(String method, Object arg0, Object arg1,
			Object arg2, Object arg3, Object arg4, Object arg5, Object arg6)
			throws XmlRpcException, IOException {
		args.clear();
		args.add(arg0);
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		args.add(arg6);
		return execute(method, args);
	}

	/**
	 * Requests this.serviceName.query with the given argument and then returns the
	 * result
	 */
	public Object query(String arg0) throws XmlRpcException, IOException {
		Object out = call(this.serviceName + ".query", arg0);
		return (java.lang.Object) XmlRpcUtils
				.GetObjectFromStruct((Hashtable) out);
	}

	/**
	 *  Calls this.serviceName.setDebug
	 * @param arg0
	 * @return
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public boolean setDebug(String arg0) throws XmlRpcException, IOException {
		Object out = call(this.serviceName + ".setDebug", arg0);
		return ((Boolean) out).booleanValue();
	}

	public XmlRpcClient getClient() {
		return client;
	}

	/**
	 * Not implemented in MyDataClient (to be implemented by implementing
	 * classes)
	 */
	public abstract void test() throws Exception;

}
