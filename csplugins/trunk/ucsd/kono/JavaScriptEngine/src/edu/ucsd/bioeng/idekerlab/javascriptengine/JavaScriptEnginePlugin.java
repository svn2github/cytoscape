package edu.ucsd.bioeng.idekerlab.javascriptengine;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.bsf.BSFManager;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManagerPlugin;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;

public class JavaScriptEnginePlugin extends CytoscapePlugin implements ScriptingEngine {
	private static final String ENGINE_NAME = "javascript";
	private static final String ENGINE_DISPLAY_NAME = "JavaScript Engine (based on Rhino 1.7.2)";
	private static final Icon ICON = new ImageIcon(JavaScriptEnginePlugin.class.getResource("/images/rhino32.png"));
	
	private static final JavaScriptEnginePlugin engine = new JavaScriptEnginePlugin();
	/**
	 * Creates a new RubyEnginePlugin object.
	 */
	public JavaScriptEnginePlugin() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return ENGINE_DISPLAY_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return ICON;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return ENGINE_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public static void register() {
		BSFManager.registerScriptingEngine(ENGINE_NAME, "org.apache.bsf.engines.javascript.JavaScriptEngine",
		                                   new String[] { ENGINE_NAME });

		System.out.println("*Rhino JavaScript engine loaded!");

		try {
			final Class engineClass = Class.forName("edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManager");
			Method method = engineClass.getMethod("registerEngine",
			                                      new Class[] { String.class, ScriptingEngine.class });
			Object ret = method.invoke(null, new Object[] { ENGINE_NAME, engine });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if(ScriptEngineManagerPlugin.getManager().getEngine(ENGINE_NAME) != null)
			return;
		
		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			// Register this to ScriptEngineManager.
			register();
		}
	}
}
