/**
 * Date: November 15, 2004
 * Description: A Cytoscape plugin that runs the Factor Graph algorithm for
 * explaining expression changes due to single-gene knockouts.
 */

package fgraph.plugin;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

import javax.swing.*;

/**
 *
 * @author Craig Mak
 */
public class FactorGraphPlugin extends CytoscapePlugin
{
    public FactorGraphPlugin()
    {
        //set-up menu options in plugins menu
        JMenu menu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
        JMenuItem item;

        //Main submenu
        JMenu submenu = new JMenu("Explain knockout data");
        item = new JMenuItem("Run algorithm");
        item.addActionListener(new RunAction());
        submenu.add(item);

        item.doClick();
        
        /*
        item = new JMenuItem("Set parameters");
        item.addActionListener(new MCODEParameterChangeAction());
        submenu.add(item);
        */

        //About box
        item = new JMenuItem("About");
        item.addActionListener(new AboutAction());
        submenu.add(item);
        
        menu.add(submenu);

        

    }
    
    /**
     * Describes the plug in.
     * @return short plug in description.
     */
    public String describe() {
        return new String("Explain mRNA expression changes due to single-gene knockouts using the factor graph algorithm");
    }
}
