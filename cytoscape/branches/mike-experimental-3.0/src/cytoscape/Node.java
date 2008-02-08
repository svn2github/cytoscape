package cytoscape;

import java.util.*;
import cytoscape.groups.*;

public interface Node extends GraphObject {
	public void addToGroup(CyGroup group); 
	public void removeFromGroup(CyGroup group); 
	public List<CyGroup> getGroups(); 
	public boolean inGroup(CyGroup group); 
	public boolean isaGroup(); 
	public String toString(); 
} // interface Node
