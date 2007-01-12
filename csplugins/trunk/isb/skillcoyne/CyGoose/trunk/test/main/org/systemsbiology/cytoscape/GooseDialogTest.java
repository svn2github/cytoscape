/**
 * 
 */
package org.systemsbiology.cytoscape;

import junit.framework.TestCase;
import org.systemsbiology.cytoscape.*;
/**
 * @author skillcoy
 *
 */
public class GooseDialogTest extends TestCase
	{

	public void testGetDialog() throws Exception
		{
		GooseDialog GD = new GooseDialog();
		GD.createDialog();
		GD.setVisible(true);
		}
	
	}
