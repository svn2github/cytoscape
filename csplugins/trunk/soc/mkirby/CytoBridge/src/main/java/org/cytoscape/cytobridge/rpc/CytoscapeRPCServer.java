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

import java.io.IOException;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.webserver.*;
import org.cytoscape.cytobridge.NetworkManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.create.NewEmptyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;


/**
* XML-RPC server class for CytoscapeRPC.
* Manages the XML-RPC server.
* @author Jan Bot
*/
public class CytoscapeRPCServer {

   private int port = 9000;
   private boolean localOnly = false;
   private WebServer webServer = null;
   private XmlRpcServer xmlRpcServer = null;

   /**
    * Instanciate the CytoscapeRPCServer
    * @param port port number to listen on.
    * @param localOnly whether the plug-in should listen for local traffic
    * only.
    * @throws XmlRpcException
    * @throws IOException
    */
   public CytoscapeRPCServer(int port, boolean localOnly)
           throws XmlRpcException, IOException {
       this.port = port;
       this.localOnly = localOnly;
       webServer = new WebServer(port);
   }

   /**
    * Starts the XML-RPC server.
    * @throws XmlRpcException When the port can not be allocated.
    * @throws IOException
    */
   public void startXmlServer(NetworkManager myManager) throws XmlRpcException, IOException{
       if(localOnly) {
           webServer.setParanoid(true);
           webServer.acceptClient("127.0.0.1");
           webServer.acceptClient("0.0.0.0");
       }

       xmlRpcServer = webServer.getXmlRpcServer();
       PropertyHandlerMapping phm = new PropertyHandlerMapping();
       CytoscapeRPCCallHandler handler = new CytoscapeRPCCallHandler(myManager);
       phm.setRequestProcessorFactoryFactory(new CytoBridgeRequestProcessorFactoryFactory(handler));
       phm.setVoidMethodEnabled(true);
       phm.addHandler("Cytoscape", CytoscapeRPCCallHandler.class);

       xmlRpcServer.setHandlerMapping(phm);
       
       XmlRpcServerConfigImpl serverConfig =
               (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
       serverConfig.setEnabledForExtensions(true);
       serverConfig.setContentLengthOptional(false);
       
       webServer.start();
       System.out.println("Starting now!");
   }

   /**
    * Stops the XML-RPC server.
    */
   public void stop(){
       webServer.shutdown();
       xmlRpcServer = null;
       //webServer = null;
   }

   /**
    * Main method to test if the server works (stand-alone).
    * @param args command line arguments
    * @throws Exception when anything goes wrong in the XML-RPC server.
    */
   public static void main(String[] args) throws Exception {
       CytoscapeRPCServer s = new CytoscapeRPCServer(9000, false);
   }
}
