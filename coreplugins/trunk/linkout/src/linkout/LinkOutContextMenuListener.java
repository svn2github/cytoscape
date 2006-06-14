/*$Id$*/
package linkout;

import ding.view.*;
import javax.swing.*;
import giny.view.NodeView;
import java.awt.*;

/**
 * LinkOutContextMenuListener implements NodeContextMenuListener
 * When a node is selected it calls LinkOut that adds the linkout menu to the node's popup menu
 */
public class LinkOutContextMenuListener implements NodeContextMenuListener {

    public LinkOutContextMenuListener(){
        //System.out.println("[LinkOutContextMenuListener]: Constructor called");
    }

    /**
     *
     * @param nodeView The clicked NodeView
     * @param menu popup menu to add the LinkOut menu
     */
    public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu){
        //System.out.println("[LinkOutContextMenuListener]: addNodeContextMenuItem called");

        
        LinkOut lo= new LinkOut();
        if(menu==null){
            menu=new JPopupMenu();
        }
        menu.add(lo.AddLinks(nodeView));
    }

}

/*$Log$
 *Revision 1.1  2006/06/14 18:12:46  mes
 *updated project to actually compile and work with ant
 *
/*Revision 1.2  2006/05/23 20:39:42  betel
/*Changes for compatibility with latest ding library
/**/