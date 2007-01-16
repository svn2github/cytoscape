//package csplugins.mskcc.doron;
package linkout;

import cytoscape.*;
import cytoscape.view.*;
//import cytoscape.browsers.*;
import cytoscape.util.*;
import cytoscape.init.*;
import java.util.*;
import giny.model.*;
import giny.view.*;
import phoebe.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import javax.swing.*;
import java.awt.event.*;

public class LinkOutTester {
	public static void main (String [] args){
		LinkOut lo= new LinkOut();

        //JMenuItem jmi=lo.AddLinks();

/*
        for(Iterator entry=lo.GetMap().keySet().iterator(); entry.hasNext();){
            String[] temp=((String)entry.next()).split("\\.");

            //System.out.println("entering menu "+temp[0]);

            ArrayList keys=new ArrayList (Arrays.asList(temp));
            lo.GenerateLinks(keys, link_menu);
        }
*/

        //lo.PrintMenu((JMenu)jmi);
    }
}