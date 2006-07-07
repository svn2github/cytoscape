/*$Id$*/


package linkout;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import java.net.URL;
import java.util.*;
import giny.view.*;
import giny.view.NodeView;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
* Generates links to external web pages specified in the cytoscape.props file.
* Nodes can be linked to external web pages by specifying web resources in the linkout.properties file
* The format for a weblink is in the form of a <key> = <value> pair where <key> is the name of the
* website (e.g. NCBI, E!, PubMed, SGD,etc) and <value> is the URL. The key name must be preceded by the keyword "url." to distinguish
* this property from other properties.
* In the URL string the placeholder %ID% will be replaced with the node identifier. It is the users responsibility
* to ensure that the URL is correct and the node's name will match the required query value.
* Examples:
*	url.NCBI=http\://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd\=Search&db\=Protein&term\=%ID%&doptcmdl\=GenPept
*	url.SGD=http\://db.yeastgenome.org/cgi-bin/locus.pl?locus\=%ID%
*	url.E\!Ensamble=http\://www.ensembl.org/Homo_sapiens/textview?species\=all&idx\=All&q\=%ID%
*	url.Pubmed=http\://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd\=Search&db\=PubMed&term\=%ID%
*
*/
public class LinkOut {

    //keyword that marks properties that should be added to LinkOut
    private final String marker="linkouturl.";

    private Properties props;

    //null constractor
    public LinkOut(){

    }

    /**
    * Fills the URL hash map with the <key> = <values> from cytoscape.props file
    * @param node the NodeView.
    * @return none
    */
    public JMenuItem addLinks(NodeView node){


        System.out.println("linkout.addLinks called with node "+((NodeView)node).getLabel().getText());
        //System.out.println("linkout.addLinks called with node "+node.getClass().getName());
        readProperties();

        JMenu top_menu=new JMenu("LinkOut");

        //iterate through properties list
        try{
            for (Enumeration e=props.propertyNames(); e.hasMoreElements();){
                String propKey=(String)e.nextElement();
                int p=propKey.lastIndexOf(marker);
                if(p== -1) continue;
                p=p+ marker.length();

                //the URL
                String url=props.getProperty(propKey);
                if (url==null){
                    url="<html><small><i>empty- no links<br> See documentation</i></small></html>"+
                            "http://www.cytoscape.org/";
                }

                //add Node label to the URL
                String nodelabel;

                final NodeView mynode=(NodeView)node;

                //node label
                nodelabel=mynode.getLabel().getText();
                if(nodelabel==null || 0==nodelabel.length()){
                    CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
                    nodelabel=mynode.getNode().getIdentifier();
                }
                //Replace %ID% mark with the node label
                final String fUrl=url.replaceFirst("%ID%",nodelabel);


                //the link name
                String[] temp=((String)propKey.substring(p)).split("\\.");
                ArrayList keys=new ArrayList (Arrays.asList(temp));

                //Generate the menu path
                generateLinks(keys, top_menu, fUrl);
            }

            //if no links specified insert a default message
            if (top_menu.getMenuComponentCount()==0){
                String url="<html><small><i>empty- no links<br> See documentation</i></small></html>"+
                        "http://www.cytoscape.org/";
                top_menu.add(new JMenuItem(url));
            }

            /* For debugging */
            // printMenu(top_menu);
        }

        catch (NullPointerException e) {
            String url="<html><small><i>empty- no links<br> See documentation</i></small></html>"+
                    "http://www.cytoscape.org/";
            top_menu.add(new JMenuItem(url));
            System.err.println("NullPointerException: " + e.getMessage());
        }


        return top_menu;
    }


    /**
     * Recursive method that expands the current menu list
     * The list of keys mark the current path of sub-menus
     * @param keys ArrayList
     * @param j JMenu the curren JMenu object
     * @param url String the url to link the node
     **/
    private void generateLinks(ArrayList keys, JMenu j, final String url) {

        //Get the sub-menu
        JMenuItem jmi=getMenuItem((String)keys.get(0), j);

        //if its null and this is the last key generate a new JMenuItem
        if(jmi==null && keys.size()==1){
            final String s=(String)keys.get(0);
            JMenuItem new_jmi=new JMenuItem (new AbstractAction((String)keys.get(0)) {
                public void actionPerformed (ActionEvent e){
                    SwingUtilities.invokeLater( new Runnable ()  {
                        public void run() {
                            OpenBrowser.openURL(url);
                        }
                    });
                }
            }	);//end of AbstractAction class


            j.add(new_jmi);
            return;

        //if its a JMenuItem and this is the last key then there
        //is a duplicate of keys in the file. i.e two url with the exact same manu path
        }else if (jmi instanceof JMenuItem && keys.size()==1){
            System.out.println("Duplicate URL specified for "+(String)keys.get(0));
            return;

        //if not null create a new JMenu  with current key
        // remove key from the keys ArrayList and call generateLinks
        }else if(jmi==null) {
            JMenu new_jm=new JMenu ((String)keys.get(0));

            keys.remove(0);
            generateLinks(keys,new_jm, url);
            j.add(new_jm);

            return;

        //Remove key from top of the list and call generateLinks with new JMenu
        } else {
            keys.remove(0);

            generateLinks(keys, (JMenu)jmi, url);
        }
        return;
   }


    /**
     * Search for an existing JmenuItem that is nested within a higher level JMenu
     * @param name String the name of the jmenu item
     * @param menu JMenu the parent JMenu to search in
     * @return JMenuItem if found, null otherwise
     * */
    private JMenuItem getMenuItem(String name, JMenu menu) {
        int count=menu.getMenuComponentCount();
        if(count==0){
            return null;
        }

        //Skip over all JMenu components that are not JMenu or JMenuItem
        for (int i=0; i<count; i++ ){
            if( !menu.getItem(i).getClass().getName().equals("javax.swing.JMenu") && !menu.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")){
                continue;
            }

            JMenuItem jmi=menu.getItem(i);
            if(jmi.getText().equalsIgnoreCase(name)) {

                return jmi;
            }
        }
        return null;
   }


    /** 
     * Print menu items - for debugging
     */
    private void printMenu(JMenu jm){
        int count=jm.getMenuComponentCount();

        for(int i=0; i<count; i++){
            if( jm.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")){
                System.out.println(jm.getItem(i).getText());
                continue;
            }

            else{
            System.out.println(jm.getItem(i).getText()+ "--");
            printMenu((JMenu)jm.getItem(i));
            }
        }


    }

    /**
     * Read properties values from linkout.props file included in the 
     * linkout.jar file and apply those properties to the base Cytoscape
     * properties.  Only apply the properties from the jar file if 
     * NO linkout properties already exist. 
     * This allows linkout properties to be specified  on the command line,
     * editted in the preferences dialog, and to be saved with other 
     * properties.
     */
    private void readProperties() {

    	// Set the properties to be Cytoscape's properties. 
	// This allows the linkout properties to be edited in
	// the preferences editor.
        props = CytoscapeInit.getProperties(); 

	// Loop over the default props and see if any
	// linkout urls have been specified.  We don't want to
	// override or even supplement what was set from the 
	// command line. Only use the defaults if nothing
	// else can be found.
	boolean linkoutFound = false;
	Enumeration names = props.propertyNames();
	while ( names.hasMoreElements() ) {
		String name = (String)names.nextElement();
                int p=name.lastIndexOf(marker);
                if (p != -1) { 
			linkoutFound = true;
			break;
		}
	}

	// If we don't have any linkout properties, load the defaults.
	if ( !linkoutFound ) {
		try {
			System.out.println("loading defaults");
			ClassLoader cl = LinkOut.class.getClassLoader(); 
			props.load(cl.getResource("linkout.props").openStream());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't load default linkout props");
		}
	}
    }
};
