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
public class CommandPostResponder implements LocalHttpServer.PostResponder{

	private final CommandExecutorTaskFactory cetf;
	private final SynchronousTaskManager stm;
	private final static Logger logger = LoggerFactory.getLogger(CommandPostResponder.class);
	
	public CommandPostResponder(CommandExecutorTaskFactory cetf, SynchronousTaskManager stm) {
		this.cetf = cetf;
		this.stm = stm;
	}

	@Override
	public boolean canRespondTo(String url) throws Exception {
		return (url.indexOf("cytoscape") >= 0);
	}

	@Override
	public Response respond(String url, String body) throws Exception {
		logger.debug("POST url     : " + url);
		logger.debug("POST body    : " + body);
		String[] chunk = url.substring( url.indexOf("cytoscape") + 9 ).split("/"); 

		String namespace = chunk[1];
		String command = chunk[2];
		String args = URLDecoder.decode(body.substring(5)); // trims off "data=" from body string
	
		String fullCommand = namespace + " " + command + " " + args;

		logger.debug("POST command : " + fullCommand);

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
