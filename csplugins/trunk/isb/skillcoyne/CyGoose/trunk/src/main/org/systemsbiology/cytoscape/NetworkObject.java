/**
 * 
 */
package org.systemsbiology.cytoscape;

/**
 * @author skillcoy
 *
 */
public enum NetworkObject
	{
	NODE(), EDGE();
	
	
	private NetworkObject()
		{		}
	
	public String toString()
		{
		return this.name();
		}
	
	}
