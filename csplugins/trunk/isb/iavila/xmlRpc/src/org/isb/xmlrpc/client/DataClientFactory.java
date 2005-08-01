package org.isb.xmlrpc.db.client;

import java.util.Properties;
import java.util.Vector;

import org.isb.xmlrpc.db.server.*;
import org.isb.xmlrpc.db.handlers.*;
import utils.*;

/**
 * Class <code>DataClientFactory</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @version 1.0
 */

// TODO:
// -getClient method needs to be less table dependant

public class DataClientFactory {
  
  public static String DEFAULT_HOST, STATIC_HOST;
  public static String USERNAME = null, PASSWORD = null;
  public static Vector dataClients = new Vector();
  public static Properties properties;
  
  static {
    
    String propertiesFilePath = XmlRpcUtils.FindXmlRpcPropsFile();
    properties = MyUtils.readProperties(propertiesFilePath);
    
    try { 
      DEFAULT_HOST = (String)properties.get("host"); }
    catch ( Exception ee ) { 
      ee.printStackTrace(); 
      DEFAULT_HOST = "local"; 
    }
    STATIC_HOST = DEFAULT_HOST;
  }
  
  /**
   * @return the MyDataClient for the given service
   */
  public static MyDataClient getClient (String service) throws Exception {
    
    // Iliana: needs to not be hard-coded
    // Properties props = MyUtils.readProperties( "csplugins/isb/dreiss/httpdata.properties" );
    
    if(USERNAME == null){
      String auth = "true";
      try { 
        auth = (String)properties.get("auth"); 
      }catch (Exception ee) { 
        auth = "false"; 
      }
      if ("true".equals(auth)) 
        askForUserNamePassword();
      else 
        USERNAME = PASSWORD = ""; 
      
    }
    
    return getClient (service, properties);
  }

  public static void setUserNamePassword (String user, String pass){
    USERNAME = user;
    PASSWORD = pass;
    for(int i = 0; i < dataClients.size(); i ++){
      MyDataClient dc = (MyDataClient)dataClients.get( i );
      if(dc instanceof AuthenticatedDataClient)
        if(!"".equals(PASSWORD)) 
          ((AuthenticatedDataClient)dc).setUserNamePassword(USERNAME,PASSWORD);
    }
  }

  public static void askForUserNamePassword() {
    SetUsernamePassword sunp = new SetUsernamePassword(null);
    sunp.setUsernameAndPassword(null);
  }

  
  // Iliana comments: this method contains a bunch of Strings that should be user params


  /**
   * @param props contains xmlrpc.props settings
   * @return a MyDataClient for the given service
   *
   * xmlrpc.props file sample for this method:
   * 
   * host = http://localhost:8081
   * #host = local # This is for a local hsqldb-based server
   * client.favoriteHomolog = csplugins.httpdata.client.FavoriteHomologClient
   * client.sequence = csplugins.httpdata.client.SequenceClient
   * client.synonym = csplugins.httpdata.client.SynonymClient
   * client.interaction = csplugins.httpdata.client.InteractionClient
   * client.homolog = csplugins.httpdata.client.HomologClient
   * client.blast = csplugins.httpdata.client.BlastClient
   * # You only need these props if you're running the servers locally (within a cytoscape instance):
   * #client.blast.pathToExe=/local/dreiss/packages/biostuff/blast-2.2.6/blastall
   * #client.blast.dataDir=/data/seqdb/blastformat/
   * #client.blast.useSGDBlast=false
   *
   * Then call, e.g.:
   * InteractionClient interactionFetcher = 
   *       (InteractionClient)DataClientFactory.getClient("interaction", props);
   */
  public static synchronized MyDataClient getClient (String service, Properties props) 
    throws Exception {
    
    String host = STATIC_HOST;
    if(host == null || host.length() == 0) 
      host = DEFAULT_HOST;
    
    System.err.println("HOST = " + host);

    String cName = props.getProperty("client." + service);
    if(cName == null || cName.length() <= 0) 
      throw new IllegalArgumentException("DataClientFactory could not find class for service " + 
                                         service);
      
    if(host.toLowerCase().startsWith("http://")){
      
      Class cls = Class.forName(cName);
      java.lang.reflect.Constructor constr = cls.getDeclaredConstructor(new Class[]{ String.class });
      MyDataClient dc = (MyDataClient)constr.newInstance( new String[] {host});
    
      if(dc instanceof AuthenticatedDataClient) 
        if (!"".equals( PASSWORD ) )((AuthenticatedDataClient)dc).setUserNamePassword(USERNAME,
                                                                                      PASSWORD);
      dataClients.add(dc);
    
      return dc;
    
    }else if(host.toLowerCase().startsWith("local")){
      /* Locally-running database-based handler. Need to do this:
         0. check if we have a running xml-rpc web server running on localhost:8081
         (assume we're always using MyWebServer and call "server.status")
         1. if not, start one up and cache the fact that we started one (port=8081?)
         2. check if the requested client's handler is registered with the server
         (call "server.hasService")
         3. if it's not, register one and cache the fact that we did it
         4. return a new client that is setup to talk to localhost:8081 */
      int localPort = 8081;
      XmlRpcUtils.startWebServerIfNeeded(localPort);
      Thread.sleep(1000);
      String localhost = "http://localhost:" + localPort;
      boolean running = XmlRpcUtils.isServiceRunning(service, localhost);
      boolean okay = false;
      if(!running){
        if(service.equalsIgnoreCase("blast")){
          // For now, we are not using BLAST (Iliana)
          System.err.println("blast client will not be returned, we don't support that service yet");
          
          // Iliana : this needs to be user parameters
          //String blastExe = "/local/dreiss/packages/biostuff/blast-2.2.6/blastall";
          //String sequenceDataDir = "/data/seqdb/blastformat/";
          //String useSGDblast = "false"; 
          //if ( props != null ) {
          //blastExe = props.getProperty( "fetchers.blast.pathToExe", blastExe );
          //sequenceDataDir = props.getProperty( "fetchers.blast.dataDir",
          //                                     sequenceDataDir );
          //useSGDblast = props.getProperty( "fetchers.blast.useSGDBlast", useSGDblast );
          //}
          //if ( "false".equals( useSGDblast ) ) 
          //XmlRpcUtils.startService( "blast", localhost, cName,
          //                          new String[] { blastExe, sequenceDataDir } );
          //else XmlRpcUtils.startService( "blast", localhost, cName, null );
          //okay = true;
          //Thread.sleep( 1000 );
        }else{ 
          // Not blast
          // start, e.g. "sequence" handler reading table "sequences"
          // Iliana: this is dependant on the table names!!! we will need to have
          // static variables with table names
          XmlRpcUtils.startService(service, localhost, cName, new String[]{ service + "s" });
          okay = true;
        }
      }

      if(okay){
        Class cls = Class.forName(cName);
        java.lang.reflect.Constructor constr = cls.getDeclaredConstructor(new Class[]{String.class});
        MyDataClient dc = (MyDataClient)constr.newInstance(new String[]{localhost});
        
        if(dc instanceof AuthenticatedDataClient)
          if(!"".equals(PASSWORD)) 
            ( (AuthenticatedDataClient) dc ).setUserNamePassword(USERNAME, PASSWORD);
        
        dataClients.add( dc );
        
        return dc;
      }

    }else throw new IllegalArgumentException( "DataClientFactory could not parse uri '" + host + "'");
    return null;
  }
}
