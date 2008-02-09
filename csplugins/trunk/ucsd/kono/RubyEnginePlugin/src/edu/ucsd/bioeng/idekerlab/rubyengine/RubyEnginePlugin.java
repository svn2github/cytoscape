package edu.ucsd.bioeng.idekerlab.rubyengine;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;

import org.apache.bsf.BSFManager;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

public class RubyEnginePlugin extends CytoscapePlugin {
	
	private static final String ENGINE_NAME = "ruby";
	
	public RubyEnginePlugin() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}
	
	public static String getEngineName() {
		return ENGINE_NAME;
	}
	
	public static void register() {
		BSFManager.registerScriptingEngine(ENGINE_NAME,
				"org.jruby.javasupport.bsf.JRubyEngine",
				new String[] { ENGINE_NAME });

		System.out.println("*Ruby script engine loaded!");
		
		try {
			final Class engineClass = Class.forName("edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManager");
			Method method = engineClass.getMethod("registerName", new Class[] {String.class, String.class});
			Object ret = method.invoke(null, new Object[] {ENGINE_NAME, "JRuby Script Engine"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			// Register this to ScriptEngineManager.
			register();
			
		}
	}
}
