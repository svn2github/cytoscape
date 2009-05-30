package csplugins.mcode;

import java.util.Collection;

import cytoscape.CyNetwork;
import cytoscape.groups.CyGroup;

public interface ModuleFinderService {
	
	public Collection<CyGroup> findModules(CyNetwork targetNetwork);

}
