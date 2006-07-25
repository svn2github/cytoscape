/**
 * 
 */
package org.isb.bionet;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.*;
import cytoscape.plugin.*;

import org.isb.xmlrpc.client.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.bionet.gui.ServerConnectionDialog;
import org.isb.bionet.gui.wizard.*;
import org.isb.iavila.ontology.xmlrpc.*;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class BioNetPlugin extends CytoscapePlugin {
    
    public static final String VERSION = "BETA";
    public static final String PROPS_FILE_KEY_SHORT = "xr";
    public static final String PROPS_FILE_KEY_LONG = "xml-rpc-props";
    protected InteractionDataClient interactionsClient;
    protected GOClient goClient;
    protected SynonymsClient synClient;

    protected NetworkBuilderWizard wizard;
    protected ServerConnectionDialog connectionDialog;
    protected String currentHost;
    /**
     * Constructor
     */
    public BioNetPlugin() {
        
        boolean statusOK = true;
        
        parsePluginArgs();
        
        this.currentHost = DataClientFactory.DEFAULT_HOST;
        this.connectionDialog = new ServerConnectionDialog(this.currentHost);
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
                new AbstractAction("Set BioNetBuilder Connection...") {
                    public void actionPerformed(ActionEvent e) {
                        connectionDialog.pack();
                        connectionDialog.setLocationRelativeTo(Cytoscape.getDesktop());
                        connectionDialog.setVisible(true);
                    }// actionPerformed
                });
        
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
                new AbstractAction ("BioNetBuilder Wizard..."){ 
                    public void actionPerformed (ActionEvent event){ prepareAndPopUpWizard(); }
                }
        );
        
    }
    
    /**
     * Tries to find plugin arguments from the Cytoscape command line and handles them appropriately
     */
    protected void parsePluginArgs (){
        
        Properties props = CytoscapeInit.getProperties();
        String xmlRpcPropsFilePath = null;
        if(props.containsKey(PROPS_FILE_KEY_LONG)){
            xmlRpcPropsFilePath = (String)props.get(PROPS_FILE_KEY_LONG);
        }else if(props.containsKey(PROPS_FILE_KEY_SHORT)){
            xmlRpcPropsFilePath = (String)props.get(PROPS_FILE_KEY_SHORT);
        }
        if(xmlRpcPropsFilePath != null){
            System.out.println("Reading properties from " + xmlRpcPropsFilePath);
            DataClientFactory.readProperties(xmlRpcPropsFilePath);
        }
        
//        String [] args = CytoscapeInit.getArgs();
//        for(int i = 0; i < args.length; i++){
//            if((args[i].equals(PROPS_FILE_ARG_LONG) || args[i].equals(PROPS_FILE_ARG_SHORT)) && i+1 < args.length){
//                String xmlRpcPropsFilePath = args[i+1];
//                System.out.println("Reading properties from " + xmlRpcPropsFilePath);
//                DataClientFactory.readProperties(xmlRpcPropsFilePath);
//            }
//        }//for i
    }
    
    /**
     * Prepares and pops-up wizard (duh!)
     *
     */
    protected void prepareAndPopUpWizard (){
      
       if(!this.currentHost.equals(DataClientFactory.STATIC_HOST) || this.wizard == null){
          this.currentHost = DataClientFactory.STATIC_HOST;
          boolean statusOK = setClients(); 
          if(!statusOK){
              JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"<html>There were errors while starting the BioNetBuilder plugin.<br>"+
                      "This probably means that your xmlrpc.props file is not located in the correct directory.<br>"+
                      "Make sure it is located in the same directory from which you started Cytoscape.</html>","Error",JOptionPane.ERROR_MESSAGE);
              return;
          }
       
          try{
              
              this.interactionsClient.addSource(ProlinksInteractionsSource.class.getName());
              this.interactionsClient.addSource(KeggInteractionsSource.class.getName());
              this.interactionsClient.addSource(BindInteractionsSource.class.getName());
              this.interactionsClient.addSource(DipInteractionsSource.class.getName());
              this.interactionsClient.addSource(HPRDInteractionsSource.class.getName());
              this.interactionsClient.addSource(BioGridInteractionsSource.class.getName());
              
          } catch (IOException ioex) {
              ioex.printStackTrace();
              System.out.println("Cause:"+ioex.getCause());
              System.out.println("Localized message:"+ ioex.getLocalizedMessage());
              JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"<html>There were errors while starting the BioNetBuilder plugin.<br>"+
                      "This probably means that the server at "+ this.currentHost +"<br>could not be found or is not running.<br></html>","Error",JOptionPane.ERROR_MESSAGE);
              statusOK = false;
          } catch (org.apache.xmlrpc.XmlRpcException xmlex){
              xmlex.printStackTrace();
              System.out.println("Cause:"+xmlex.getCause());
              System.out.println("Localized message"+xmlex.getLocalizedMessage());
              JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"<html>There were errors while starting the BioNetBuilder plugin.<br>"+
                      "The server has returned a fault message.<br>"+
                      "Send email to iavila@systemsbiology.org with any console error messages that you have attached.</html>","Error",JOptionPane.ERROR_MESSAGE);
              statusOK = false;
          
          }catch (Exception ex){
              ex.printStackTrace();
              System.out.println("Cause:"+ ex.getCause());
              System.out.println("Localized message:"+ ex.getLocalizedMessage());
              JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"<html>There were errors while starting the BioNetBuilder plugin.<br>"+
                      "This probably means that the server at "+ this.currentHost +"<br>could not be found or is not running.<br></html>","Error",JOptionPane.ERROR_MESSAGE);
              statusOK = false; 
          }
          
          if(!statusOK) return;
          this.wizard = new NetworkBuilderWizard(this.synClient, this.interactionsClient, this.goClient);
       }
       
       this.wizard.startWizard();
                
    }
    
    /**
     * 
     * @return false if the status is not ok
     */
    protected boolean setClients (){
        boolean statusOK = true;
        try {

            this.interactionsClient = 
                (InteractionDataClient) DataClientFactory.getClient("interactions");
            if(this.interactionsClient == null){
                statusOK = false;
                System.out.println("Could not get an InteractionDataClient!!!");
            }
            
            this.goClient = (GOClient)DataClientFactory.getClient("geneOntology");
            if(this.goClient == null){
                statusOK = false;
                System.out.println("Could not get a GOClient!!!");
            }
            
            this.synClient = (SynonymsClient)DataClientFactory.getClient("synonyms");
            if(this.synClient == null){
                statusOK = false;
                System.out.println("Could not get a SynonymsClient!!!");
            }
        
        }catch (Exception ex){
            ex.printStackTrace();    
            statusOK = false;
            
        }
        
        return statusOK;
    }

}// BioNetPlugin
