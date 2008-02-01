//package csplugins.mskcc.doron;
package linkout;

import cytoscape.*;

import cytoscape.init.*;

//import cytoscape.browsers.*;
import cytoscape.util.*;

import cytoscape.view.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;

import giny.model.*;

import giny.view.*;

import phoebe.*;

import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 *
 */
public class LinkOutTester {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		LinkOut lo = new LinkOut();

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
