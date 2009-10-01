package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.UIManager;
import org.eclipse.equinox.internal.p2.ui.model.AvailableIUElement;
import org.eclipse.equinox.internal.p2.ui.model.CategoryElement;
import org.eclipse.equinox.internal.p2.ui.model.ProvElement;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui.IUPropertyUtils;
import org.eclipse.equinox.internal.provisional.p2.ui.model.InstalledIUElement;

public class RepoCellRenderer extends DefaultTreeCellRenderer {
	//DefaultMutableTreeNode theNode;

	public RepoCellRenderer(){	
	}
	
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
    		boolean selected, boolean expanded, 
    		boolean leaf, int row, 
    		boolean hasFocus) {
    	DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
    	  	
    	if(selected) {
			setForeground(Color.blue);
			//setBackground(Color.red);
    		//setForeground(UIManager.getColor("Tree.selectionForeground"));
			setBackground(UIManager.getColor("Tree.selectionBackground"));
		}
		else {
			setForeground(UIManager.getColor("Tree.textForeground"));
			setBackground(UIManager.getColor("Tree.textBackground"));
		}

    	if (treeNode.getUserObject() instanceof ProvElement){
    		ProvElement provElement = (ProvElement) treeNode.getUserObject();
    		
    		ImageIcon theIcon = new ImageIcon((Image)provElement.getImage(null));
    		this.setIcon(theIcon);
    		
    		if (provElement instanceof CategoryElement){
    			CategoryElement cat_element = (CategoryElement)provElement;
    			// TODO -- we should have a name for each category
    			this.setText(cat_element.getLabel(null));
    		}
    		else if (provElement instanceof AvailableIUElement){
    			AvailableIUElement avail_element = (AvailableIUElement)provElement;
    			IInstallableUnit installUnit =avail_element.getIU();
    			String version = installUnit.getVersion().toString();
    			String name = IUPropertyUtils.getIUProperty(installUnit, IInstallableUnit.PROP_NAME);
    			this.setText(name + " -- " + version);
    		}
    		else if (provElement instanceof InstalledIUElement){
    			InstalledIUElement installed_element = (InstalledIUElement)provElement;
    			IInstallableUnit installUnit =installed_element.getIU();
    			String version = installUnit.getVersion().toString();
    			String name = IUPropertyUtils.getIUProperty(installUnit, IInstallableUnit.PROP_NAME);
    			this.setText(name + " -- " + version);
    		}
    		else {
        		this.setText("Unknown");    			
    		}
    	}
    	else {
    		//System.out.println("treeNode.getUserObject() NOT instanceof ProvElement");
    	}
    	
    	return this;
    }
}
