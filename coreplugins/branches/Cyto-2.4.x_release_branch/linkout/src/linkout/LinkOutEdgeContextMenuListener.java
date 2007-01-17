/*$Id$*/
package linkout;

import ding.view.*;
import javax.swing.*;
import giny.view.*;

/**
 * Created by IntelliJ IDEA.
 * User: doron
 * Date: Oct 12, 2006
 * Time: 4:47:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkOutEdgeContextMenuListener implements EdgeContextMenuListener {
    public LinkOutEdgeContextMenuListener(){
        //System.out.println("[LinkOutContextMenuListener]: Constructor called");
    }

    /**
     * @param edgeView The clicked NodeView
     * @param menu popup menu to add the LinkOut menu
     */
    public void addEdgeContextMenuItems (EdgeView edgeView, JPopupMenu menu){
        //System.out.println("[LinkOutContextMenuListener]: addNodeContextMenuItem called");


        LinkOut lo= new LinkOut();
        if(menu==null){
            menu=new JPopupMenu();
        }
        menu.add(lo.addLinks(edgeView));
    }

}
