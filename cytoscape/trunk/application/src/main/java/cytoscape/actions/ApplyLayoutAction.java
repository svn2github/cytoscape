package cytoscape.actions;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import cytoscape.layout.LayoutTask;
import java.util.List;
import java.util.Iterator;

public class ApplyLayoutAction extends CytoscapeAction implements PropertyChangeListener {

	private static final long serialVersionUID = -576925581582912345L;
	
	private String layoutName;
	private CyLayoutAlgorithm alg;
	private JButton applyLayoutButton;
	
	public ApplyLayoutAction(JButton button){
		applyLayoutButton = button;

		layoutName = CytoscapeInit.getProperties().get("defaultLayoutAlgorithm").toString();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.PREFERENCES_UPDATED,this);
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		
		List<CyNetworkView> viewList = Cytoscape.getSelectedNetworkViews();
		if (viewList.isEmpty()|| viewList.size() == 0 || Cytoscape.getCurrentNetworkView() == Cytoscape.getNullNetworkView()){
			return;
		}
		
		alg = CyLayouts.getLayout(layoutName);
		
		if (alg ==null){
			// Can not find the layout algorithm specified
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Unknown layout algorithm -- "+layoutName,"Warning", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Iterator<CyNetworkView> it = viewList.iterator();
		
		while (it.hasNext()){
			CyNetworkView view = it.next();
			
			// Create Task
			LayoutTask task = new LayoutTask(alg, view);
						
			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.displayCancelButton(false);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}
	
	public boolean isInMenuBar(){
		return false;
	}
	
	public boolean isInToolBar(){
		return false;
	}
	
	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.PREFERENCES_UPDATED)){
			String newLayoutName = CytoscapeInit.getProperties().get("defaultLayoutAlgorithm").toString();

			if (!newLayoutName.equalsIgnoreCase(layoutName)){
				layoutName = newLayoutName;
				applyLayoutButton.setToolTipText("Apply "+layoutName+" layout");
			}
		}
	}

}
