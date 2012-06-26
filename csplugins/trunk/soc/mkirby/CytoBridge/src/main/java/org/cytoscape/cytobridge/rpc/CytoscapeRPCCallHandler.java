package org.cytoscape.cytobridge.rpc;

/*
Copyright (c) 2010 Delft University of Technology (www.tudelft.nl)

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 3.0 of the License, or
any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
documentation provided hereunder is on an "as is" basis, and the  Delft
University of Technology has no obligations to provide  maintenance,
support, updates, enhancements or modifications.
In no event shall the Delft University of Technology  be liable to any
party for direct, indirect, special,  incidental or consequential
damages, including lost profits, arising  out of the use of this
software and its documentation, even if the  Delft University of
Technology have been advised of the possibility  of such damage. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License  along with this library; if not, write to the Free Software
Foundation,  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
USA.
*/

import java.lang.reflect.Method;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcException;
import org.cytoscape.cytobridge.NetworkManager;
import org.cytoscape.task.create.NewEmptyNetworkViewFactory;

/**
* This class implements all the supported methods for the Cytoscape XMLRPC
* server. It acts as a basic translator of XMLRPC calls to native Cytoscape
* functions with some additional error checking.
*
* @author Jan Bot
* @version 1.6 Scooter
*/
public class CytoscapeRPCCallHandler {

	private NetworkManager myManager;
	
   /**
    * Constructs a CytoscapeRPCCallHandler.
    */
   public CytoscapeRPCCallHandler(NetworkManager myManager) {
       System.out.println("Instanciating CytoscapeRPCCallHandler");
       this.myManager = myManager;
   }

   /*************************************************************
    * Helper functions
    * Functions which make working with CytoscapeRPC easier.
    *************************************************************/

   /**
    * Minimal method to test if the connection is working.
    * @return The string "It works!".
    */
   
   public String test(Vector<String> nodes) {
	   System.out.println("Got "+nodes.size()+" nodes!");
       return "Thanks";
   }
   
   public String pushNetwork(String name, Vector<Integer> nodes, Vector<Double> edgeFrom, Vector<Double> edgeTo) throws XmlRpcException {
	   if (edgeFrom.size()!=edgeTo.size()) {
		   throw new XmlRpcException("Edge Communication error!");
	   }
	   System.out.println("Got "+nodes.size()+" nodes.");
	   
	   myManager.pushNetwork(name, nodes, edgeFrom, edgeTo);
	   
	   return "Pushed to Cytoscape.";
   }
   

   /**
    * Returns a string representing the version of the plugin.
    * @return
    */
   public String version() {
       return "1.8";
   }

   /*************************************************************
    * Internal classes
    * Implement classes which are used internally for e.g.
    * comparisons.
    *************************************************************/
   private class MethodCompare implements Comparator<Method> {

       public int compare(Method o1, Method o2) {
           return o1.getName().compareTo(o2.getName());
       }
   }

   /**
    * Creates a hashmap out of a list of keys and a list of values
    */
   protected static <A, B> HashMap<A,B> arraysToMap(A[] keys, B[] values)
           throws XmlRpcException {
       if (keys.length != values.length)
           throw new XmlRpcException("Sizes of input lists do not match.");

       HashMap<A,B> mapping = new HashMap<A,B>();
       for(int i=0;i<keys.length;i++)
           mapping.put(keys[i],values[i]);
       return mapping;
   }

}

