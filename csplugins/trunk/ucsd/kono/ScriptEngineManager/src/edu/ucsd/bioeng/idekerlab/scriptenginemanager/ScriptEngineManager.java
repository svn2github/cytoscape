package edu.ucsd.bioeng.idekerlab.scriptenginemanager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;

import cytoscape.Cytoscape;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ui.SelectScriptDialog;


public class ScriptEngineManager implements PropertyChangeListener {
	
	private static final BSFManager manager;
	
	private static final Map<String, ScriptingEngine> registeredNames = new ConcurrentHashMap<String, ScriptingEngine>();
	
	private static JMenu menu;
	
	static {
		manager = new BSFManager();
	}
	
	public ScriptEngineManager() {
		menu = new JMenu("Execute Scripts...");
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(menu);
		
	}
	
	
	protected static BSFManager getManager() {
		return manager;
	}
	
	public static void registerEngine(final String id, final ScriptingEngine engine) {
		registeredNames.put(id, engine);
		
		menu.add(new JMenuItem(new AbstractAction(engine.getDisplayName()) {
				public void actionPerformed(ActionEvent e) {
					SelectScriptDialog.showDialog(id);
				}
			}));
	}
	
	public ScriptingEngine getEngine(String engineID) {
		return registeredNames.get(engineID);
	}


	public static void execute(final String engineName, final String scriptFileName, final Map<String, String> arguments) throws BSFException, IOException {
		
		if(BSFManager.isLanguageRegistered(engineName) == false) {
			// Register Engine
			
			System.out.println("Error: Can't find " + engineName);
			return;

		}
		
		manager.terminate();
		
		final Object returnVal = manager.eval(engineName, scriptFileName, 1, 1, IOUtils
				.getStringFromReader(new FileReader(scriptFileName)));

		if (returnVal != null) {
			System.out.println("Return Val = [" + returnVal + "]");
		}

	}


	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
