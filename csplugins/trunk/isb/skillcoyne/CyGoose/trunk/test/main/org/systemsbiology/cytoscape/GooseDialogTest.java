/**
 * 
 */
package org.systemsbiology.cytoscape;

import junit.framework.TestCase;
import org.systemsbiology.cytoscape.*;
import org.systemsbiology.cytoscape.dialog.GooseDialog;
/**
 * @author skillcoy
 *
 */
public class GooseDialogTest extends TestCase
	{

	public void testGetDialog() throws Exception
		{
		GooseDialog GD = new GooseDialog();
		this.assertNotNull(GD);

		// this is not really the ideal way to get to these buttons for for now it's the easiest
		assertNotNull(GD.bcastHashMapButton);
		assertNotNull(GD.bcastListButton);
		assertNotNull(GD.bcastMatrixButton);
		assertNotNull(GD.bcastNetButton);
		assertNotNull(GD.gagglePluginPanel);
		assertNotNull(GD.gooseChooser);
		assertNotNull(GD.hideButton);
		assertNotNull(GD.registerButton);
		assertNotNull(GD.setIdButton);
		assertNotNull(GD.showButton);
		assertNotNull(GD.updateButton);
		}
	
	}
