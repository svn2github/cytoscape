package csplugins.isb.dreiss.httpdata.xmlrpc;

/**
 * Class <code>ObjectHandler</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 0.6 (Tue Sep 02 11:12:29 PDT 2003)
 */

public class ObjectHandler {
   public String print( java.util.Hashtable tab ) {
      Object obj = XmlRpcUtils.GetObjectFromStruct( tab );
      String out = obj.toString();
      System.out.println( "RECEIVED OBJECT " + obj.getClass().getName() + ": " + out );
      return out;
   }
}
