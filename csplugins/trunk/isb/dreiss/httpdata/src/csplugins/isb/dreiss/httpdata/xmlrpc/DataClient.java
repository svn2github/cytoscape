package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.util.*;
import java.io.IOException;
import org.apache.xmlrpc.*;

/**
 * Class <code>DataClient</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public abstract class DataClient {
   public String SERVICE_NAME = "unknown";
   
   Vector args = new Vector();
   XmlRpcClient client;

   int nCallsPerBatch = 0; 
   Vector batchVector = null; 
   boolean startedSystemHandler = false;

   public DataClient( String url ) throws XmlRpcException,
					  java.net.MalformedURLException {
      client = new XmlRpcClient( url );
   }

   public synchronized Object execute( String method, Vector params ) throws XmlRpcException, IOException {
      Object out = null;
      if ( nCallsPerBatch > 0 ) out = doMultiCall( method, params );
      else out = client.execute( method, params );
      return out;
   }

   public synchronized void setMultiCall( int ncalls ) throws XmlRpcException, IOException {
      nCallsPerBatch = ncalls;
      if ( batchVector == null ) batchVector = new Vector();
      else batchVector.clear();
      if ( ! startedSystemHandler ) {
	 startedSystemHandler = true;
	 client.execute( "server.addMultiCallService", batchVector );
      }
   }

   public synchronized Vector endMultiCall() throws XmlRpcException, IOException {
      Vector out = null;
      if ( batchVector.size() > 0 ) 
	 out = (Vector) client.execute( "system.multicall", batchVector );
      else out = batchVector;
      batchVector.clear();
      nCallsPerBatch = 0;
      return out;
   }

   public synchronized Vector doMultiCall( String method, Vector params ) throws XmlRpcException, IOException {
      Hashtable tab = new Hashtable();
      tab.put( "methodName", method );
      tab.put( "params", params.clone() );
      batchVector.add( tab );
      if ( nCallsPerBatch > 0 && batchVector.size() > 0 &&
	   batchVector.size() % nCallsPerBatch == 0 ) {
	 Vector out = (Vector) client.execute( "system.multicall", batchVector );
	 batchVector.clear();
	 return out;
      }
      return null;
   }

   public synchronized Object call( String method ) throws XmlRpcException, IOException {
      args.clear();
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1, Object arg2 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      args.add( arg2 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1, Object arg2, Object arg3 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      args.add( arg2 );
      args.add( arg3 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      args.add( arg2 );
      args.add( arg3 );
      args.add( arg4 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      args.add( arg2 );
      args.add( arg3 );
      args.add( arg4 );
      args.add( arg5 );
      return execute( method, args );
   }

   public synchronized Object call( String method, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6 ) throws XmlRpcException, IOException {
      args.clear();
      args.add( arg0 );
      args.add( arg1 );
      args.add( arg2 );
      args.add( arg3 );
      args.add( arg4 );
      args.add( arg5 );
      args.add( arg6 );
      return execute( method, args );
   }

   public Object query( String arg0 ) throws XmlRpcException, IOException {
      Object out = call( SERVICE_NAME + ".query", arg0 );
      return (java.lang.Object) csplugins.isb.dreiss.httpdata.xmlrpc.XmlRpcUtils.GetObjectFromStruct( (Hashtable) out );
   }

   public boolean setDebug( String arg0 ) throws XmlRpcException, IOException {
      Object out = call( SERVICE_NAME + ".setDebug" );
      return ( (Boolean) out ).booleanValue();
   }

   public XmlRpcClient getClient() {
      return client;
   }

   public abstract void test() throws Exception;
}
