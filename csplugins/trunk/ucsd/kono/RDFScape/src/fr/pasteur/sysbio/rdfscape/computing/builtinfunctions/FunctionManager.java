/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.computing.builtinfunctions;

import java.util.Hashtable;

import fr.pasteur.sysbio.rdfscape.RDFScape;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FunctionManager {
	
	Hashtable functions;
	/**
	 * 
	 */
	public FunctionManager() {
		super();
		
		functions=new Hashtable();
		loadFunctions();
		
	}
	
	public boolean isBuiltInFunction(String s) {
		if(functions.get(s)==null) return false;
		else return true;
	}
	
	public boolean preComputeFunctions() {
		return true;
	}

	public PatternDataFunction getFunction(String f) {
		
		return (PatternDataFunction) functions.get(f);
		
	}
	
	private boolean loadFunctions() {
		functions.put("VARIANCE", new Variance());
		functions.put("CORRELATION",new Correlation());
		functions.put("FASTVARIANCE",new FastVariance());
		functions.put("FASTCORRELATION",new FastCorrelation());
		return true;
	}
	
}
