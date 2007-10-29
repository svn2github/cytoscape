/**
 * 
 */
package org.systemsbiology.cytoscape.script;

/**
 * @author skillcoy
 *
 */
public enum Command
	{
	HIDE("hideSelection"), INVERT("invertSelection"), CLEAR("clearSelection");
	
	private String cmd;
	
	private Command(String arg)
		{
		cmd = arg;
		}
	
	public String getCommand()
		{
		return cmd;
		}
	
	}
