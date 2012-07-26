package org.cytoscape.rest.internal.net.server;

import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;

import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.rest.internal.net.server.LocalHttpServer.Response;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for handling POST requests received by the local HTTP server.
 */
public class CommandGetResponder implements LocalHttpServer.GetResponder{

	private final CommandExecutorTaskFactory cetf;
	private final SynchronousTaskManager stm;
	private final static Logger logger = LoggerFactory.getLogger(CommandGetResponder.class);
	
	public CommandGetResponder(CommandExecutorTaskFactory cetf, SynchronousTaskManager stm) {
		this.cetf = cetf;
		this.stm = stm;
	}

	@Override
	public boolean canRespondTo(String url) throws Exception {
		return (url.indexOf("cytoscape") >= 0);
	}

	@Override
	public Response respond(String url) throws Exception {
		System.out.println("GET url     : " + url);
        String[] full  = url.split("/");
        int i = 0;
        while (i<full.length) {
            if (full[i].equals("cytoscape"))
                break;
            i++;
        }

        String namespace = full[++i];
        String command = full[++i];
        String args = "";
        while (i<full.length-1)
            args = args + full[++i] + "/";
        args = args.replaceAll("&"," ");
	
		String fullCommand = namespace + " " + command + " " + args;

		System.out.println("GET command : " + fullCommand);

		stm.execute(cetf.createTaskIterator(fullCommand));

		Map<String, String> responseData = new HashMap<String, String>();

		responseData.put("namespace",namespace);
		responseData.put("command",command);
		responseData.put("args",args);
		responseData.put("status","success");

		JSONObject jsonObject = new JSONObject(responseData);

		String responseBody = jsonObject.toString();
		responseBody += "\n";
		LocalHttpServer.Response response = new LocalHttpServer.Response(responseBody, "application/json");
		return response;
	}
}
