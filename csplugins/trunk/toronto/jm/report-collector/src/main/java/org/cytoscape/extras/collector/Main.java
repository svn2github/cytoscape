package org.cytoscape.extras.collector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class Main {
	Object mutex = new Object();
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			printUsage();
			return;
		}
		
		File reportPath = new File(args[0]);
		if (!reportPath.isDirectory()) {
			throw new Exception("Report path does not exist: " + args[0]);
		}
		
		int port;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			throw new Exception("Port must be an integer: " + args[1]);
		}
		
		Main main = new Main(reportPath, port);
		main.start();
	}

	private static void printUsage() {
		System.err.printf("Usage: %s report-path  port\n", Main.class.getName());
	}

	private File reportPath;
	private int port;

	public Main(File reportPath, int port) {
		this.reportPath = reportPath;
		this.port = port;
	}

	private void start() throws Exception {
		final Pattern versionPattern = Pattern.compile("\\d+([.]\\d+([.]\\d+(-[0-9A-Za-z_-]+)?)?)?");
		final Pattern requestPattern = Pattern.compile("/log-performance/(.*)");
		
		Server server = new Server(port);
		server.setHandler(new AbstractHandler() {
			public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				Matcher targetMatcher = requestPattern.matcher(target);
				if (!targetMatcher.matches()) {
					return;
				}
				
				String method = request.getMethod();
				if (!"PUT".equals(method)) {
					return;
				}
				
				String version = targetMatcher.group(1);
				Matcher versionMatcher = versionPattern.matcher(version);
				if (!versionMatcher.matches()) {
					return;
				}
				
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/plain");
				
				// Store reports in "reportPath/year-month-day/version-timestamp.txt"
				Calendar calendar = Calendar.getInstance();
				String date = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
				File reportSubPath = new File(reportPath, date);
				reportSubPath.mkdirs();
				
				synchronized (mutex) {
					String filename = String.format("%s-%d.txt", version, System.currentTimeMillis());
					FileWriter fileWriter = new FileWriter(new File(reportSubPath, filename));
					PrintWriter writer = new PrintWriter(new BufferedWriter(fileWriter));
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
						try {
							String line = reader.readLine();
							while (line != null) {
								writer.println(line);
								line = reader.readLine();
							}
						} finally {
							reader.close();
						}
					} finally {
						writer.close();
					}
				}
				baseRequest.setHandled(true);
			}
		});
		server.start();
		server.join();
	}
}
