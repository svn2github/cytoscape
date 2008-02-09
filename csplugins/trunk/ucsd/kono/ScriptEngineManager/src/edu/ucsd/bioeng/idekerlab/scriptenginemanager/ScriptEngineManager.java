package edu.ucsd.bioeng.idekerlab.scriptenginemanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;


public class ScriptEngineManager implements PropertyChangeListener {
	
	private static final BSFManager manager;
	
	private static final Map<String, String> registeredNames = new ConcurrentHashMap<String, String>();
	
	
	
	static {
		manager = new BSFManager();
	}
	
	protected static BSFManager getManager() {
		return manager;
	}
	
	public static void registerName(String id, String dispName) {
		registeredNames.put(id, dispName);
	}


	public static void execute(final String engineName, final String scriptFileName, final Map<String, String> arguments) throws BSFException, IOException {

		System.out.println("Registered: " + registeredNames.keySet().size());
		
		if(BSFManager.isLanguageRegistered(engineName) == false) {
			// Register Engine
			
			System.out.println("Error: Can't find " + engineName);
			return;
//			
//			Method method;
//			try {
//				Class engineClass = Class.forName("edu.ucsd.bioeng.idekerlab.rubyengine.RubyEnginePlugin");
//				method = engineClass.getMethod("register", null);
//				Object ret = method.invoke(null, null);
//			
//			
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		
		manager.declareBean("arguments", arguments, HashMap.class);
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
