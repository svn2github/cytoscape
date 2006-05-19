package linkout;

import ding.view.NodeContextMenuListener;
import javax.swing.*;
import java.awt.*;

/**
 * LinkOutContextMenuListener implements NodeContextMenuListener
 * When a node is selected it calls LinkOut that adds the linkout menu to the node's popup menu
 */
public class LinkOutContextMenuListener implements NodeContextMenuListener {

    public void LinkOutContextMenuListener(){
        //System.out.println("[LinkOutContextMenuListener]: Constructor called");
    }

    /**
     *
     * @param pt Point coordinates on the canvas
     * @param nodeView The clicked NodeView
     * @param menu popup menu to add the LinkOut menu
     */
    public void addNodeContextMenuItems (Point pt, Object nodeView, JPopupMenu menu){
        //System.out.println("[LinkOutContextMenuListener]: addNodeContextMenuItem called");

        
        LinkOut lo= new LinkOut();
        if(menu==null){
            menu=new JPopupMenu();
        }
        menu.add(lo.AddLinks(nodeView));
    }

}
