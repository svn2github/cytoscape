
package bundle;

import java.awt.event.ActionEvent;
import cytoscape.util.CytoscapeAction;

public class ExampleAction extends CytoscapeAction {
	
	public ExampleAction() {
		super("Example");
		setPreferredMenu("Edit");
	}

	public void actionPerformed(ActionEvent ae) {
		System.out.println("Example Action Performed!");
	}
}
