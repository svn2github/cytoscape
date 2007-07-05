package edu.ucsd.bioeng.kono.rucy;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;

public class Rucy {

	private static final String ENGINE_NAME = "ruby";

	public static void execute(final String scriptFileName, final Map<String, String> arguments) throws BSFException, IOException {

		final BSFManager bsfmgr = new BSFManager();
		BSFManager.registerScriptingEngine(ENGINE_NAME,
				"org.jruby.javasupport.bsf.JRubyEngine",
				new String[] { ENGINE_NAME });


//		final Object obj = bsfmgr.eval(ENGINE_NAME, scriptFileName, 1, 1, IOUtils
//				.getStringFromReader(new FileReader(scriptFileName)));
		
		bsfmgr.declareBean("arguments", arguments, HashMap.class);
		final Object returnVal = bsfmgr.eval(ENGINE_NAME, scriptFileName, 1, 1, IOUtils
				.getStringFromReader(new FileReader(scriptFileName)));

		if (returnVal != null) {
			System.out.println("Return Val = [" + returnVal + "]");
		}

	}

}
