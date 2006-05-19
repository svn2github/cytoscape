/*$Id$*/

//package csplugins.mskcc.doron;
package linkout;

import cytoscape.*;
import cytoscape.util.*;
import java.util.*;
import giny.view.*;
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
    static private final String marker="url.";

    static private Properties props;

    static private String propertiesLocation;

    //null constractor
    public LinkOut(){

    }

    /**
    * Fills the URL hash map with the <key> = <values> from cytoscape.props file
    * @param Object object NodeView object
    * @return none
    */
//    public static JMenuItem AddLinks(Object[] args, PNode node){
    public static JMenuItem AddLinks(Object node){


        System.out.println("linkout.AddLinks called with node "+((NodeView)node).getLabel().getText());
        //System.out.println("linkout.AddLinks called with node "+node.getClass().getName());
        ReadProperties(propertiesLocation);

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

                //final PNode mynode=node;
                final NodeView mynode=(NodeView)node;

                //node label
                nodelabel=mynode.getLabel().getText();

                //Replace %ID% mark with the node label
                final String fUrl=url.replaceFirst("%ID%",nodelabel);


                //the link name
                String[] temp=((String)propKey.substring(p)).split("\\.");
                ArrayList keys=new ArrayList (Arrays.asList(temp));

                //Generate the menu path
                GenerateLinks(keys, top_menu, fUrl);
            }

            //if no links specified insert a default message
            if (top_menu.getMenuComponentCount()==0){
                String url="<html><small><i>empty- no links<br> See documentation</i></small></html>"+
                        "http://www.cytoscape.org/";
                top_menu.add(new JMenuItem(url));
            }

            /* For debugging */
            // PrintMenu(top_menu);
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
    public static void GenerateLinks(ArrayList keys, JMenu j, final String url) {

        //Get the sub-menu
        JMenuItem jmi=GetMenuItem((String)keys.get(0), j);

        //if its null and this is the last key generate a new JMenuItem
        if(jmi==null && keys.size()==1){
            final String s=(String)keys.get(0);
            JMenuItem new_jmi=new JMenuItem (new AbstractAction((String)keys.get(0)) {
                public void actionPerformed (ActionEvent e){
                    SwingUtilities.invokeLater( new Runnable ()  {
                        public void run() {
                            StartWebLink(url);
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
        // remove key from the keys ArrayList and call GenerateLinks
        }else if(jmi==null) {
            JMenu new_jm=new JMenu ((String)keys.get(0));

            keys.remove(0);
            GenerateLinks(keys,new_jm, url);
            j.add(new_jm);

            return;

        //Remove key from top of the list and call GenerateLinks with new JMenu
        } else {
            keys.remove(0);

            GenerateLinks(keys, (JMenu)jmi, url);
        }
        return;
   }


    /**
     * Search for an existing JmenuItem that is nested within a higher level JMenu
     * @param name String the name of the jmenu item
     * @param menu JMenu the parent JMenu to search in
     * @return JMenuItem if found, null otherwise
     * */
    private static JMenuItem GetMenuItem(String name, JMenu menu) {
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
    * Launch a web page
    * @param s	Webpage URL
    **/
    public static void StartWebLink(String s){
        OpenBrowser.openURL(s);
    }

/*Print menu items - for debugging*/
    public static void PrintMenu(JMenu jm){
        int count=jm.getMenuComponentCount();

        for(int i=0; i<count; i++){
            if( jm.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")){
                System.out.println(jm.getItem(i).getText());
                continue;
            }

            else{
            System.out.println(jm.getItem(i).getText()+ "--");
            PrintMenu((JMenu)jm.getItem(i));
            }
        }


    }

    /**
     * Read properties values from linkout.props file
     * Search for linkout.props file in four locations in the following order:
     * 1. specified by fileLoc
     * 2. current working directory
     * 3. $CYTOSCAPE_HOME
     * 4. ~/.cytoscape
     *
     * @param fileLoc file name - can also be an absolute path
     *
     * **/
    public static void ReadProperties(String fileLoc){
        boolean propsFound = false;
        props = new Properties();
        File file=null;

        //1. Try the specified location for a props file
        if ( fileLoc != null ) {
            file = new File( fileLoc );
            propertiesLocation = fileLoc;
        }
        //2. Try the current working directory
        if ( !propsFound ) {

            file = new File( System.getProperty ("user.dir"), "linkout.props" );
            propertiesLocation = file.toString();
            propsFound=true;
        }

        //3. Try CYTOSCAPE_HOME
        if ( !propsFound ) {
            file = new File( System.getProperty ("CYTOSCAPE_HOME"), "linkout.props" );
            propertiesLocation = file.toString();
            propsFound=true;
        }

        //4. Try ~/.cytoscape
        //TODO- this section ain't working properly
        if ( !propsFound ) {
            file = CytoscapeInit.getConfigFile( "linkout.props" );
            propertiesLocation = file.toString();
            propsFound=true;
        }

        try {
            if(file != null){
                props.load( new FileInputStream( file ) );

            } else {
                propsFound=false;
            }

        } catch ( Exception e ) {

            // error
            propsFound = false;
        }

        if (!propsFound){
            System.out.println("Couldn't find linkout.props file in "+System.getProperty ("user.dir") +
                    ", "+  System.getProperty ("CYTOSCAPE_HOME") +
                    " or in ~/.cytoscape");
        }

    }


};

/*
$Log$
Revision 1.3  2006/05/19 21:51:29  betel
New implementation of LinkOut with network-view listener

Revision 1.1  2006/05/11 22:42:28  betel
Initial deposit of linkout to pre-coreplugins

Revision 1.2  2006/05/09 22:32:47  betel
New implementation of LinkOutPlugin with new context menu interface and addition of linkout.props

Revision 1.1  2006/05/08 17:15:22  betel
Initial deposit of linkout source code

*/