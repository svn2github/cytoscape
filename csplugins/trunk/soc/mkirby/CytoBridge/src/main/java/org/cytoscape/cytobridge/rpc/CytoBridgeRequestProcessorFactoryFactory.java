package org.cytoscape.cytobridge.rpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

public class CytoBridgeRequestProcessorFactoryFactory implements
      RequestProcessorFactoryFactory {
	
    private final RequestProcessorFactory factory =
      new CytoBridgeRequestProcessorFactory();
    
    private final CytoscapeRPCCallHandler handler;

    public CytoBridgeRequestProcessorFactoryFactory(CytoscapeRPCCallHandler handler) {
      this.handler = handler;
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class aClass)
         throws XmlRpcException {
      return factory;
    }

    private class CytoBridgeRequestProcessorFactory implements RequestProcessorFactory {
      public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest)
          throws XmlRpcException {
        return handler;
      }
    }
  }
