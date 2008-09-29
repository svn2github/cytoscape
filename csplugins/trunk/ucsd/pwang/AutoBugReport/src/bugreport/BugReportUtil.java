package bugreport;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;


public class BugReportUtil {
	
	/**
	 * Submit bug report to Cytoscape's Mantis bug tracker
	 * @param URLStr URL string to Cytoscape bug tracker. 
	 * @param BugReport An instance of BugReport. 
	 * @return String returned from Mantis bug tracker to indicate whether the submission success or fail.
	 */
	public static String submitBugReport(String URLStr, BugReport pBugReport) throws IOException{
	
		ClientHttpRequest httpReq = new ClientHttpRequest(URLStr);
		
		httpReq.setParameter("user", pBugReport.getBugReporter());
		httpReq.setParameter("summary", pBugReport.getSummary());
		httpReq.setParameter("description", pBugReport.getDescription());
		httpReq.setParameter("cytoscape_version", pBugReport.getCytoscapeVersion());
		httpReq.setParameter("os", pBugReport.getOS());
		httpReq.setParameter("os_build", pBugReport.getOSVersion());
		httpReq.setParameter("steps_to_reproduce", pBugReport.getStepsToReproduce());
		httpReq.setParameter("additional_info", pBugReport.getAdditionalInfo());
		if (pBugReport.getAttachedFile() != null) {
			httpReq.setParameter("file", pBugReport.getAttachedFile());			
		}
		
		// send the form POST request
		InputStream is = httpReq.post(); 
		
		// After the bug report is submitted, the bug tracker returns an InputStream to
		// show the status of the report. Turn the stream into a String 
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
		   buffer.append(line);
		}
		return buffer.toString();
	}
	
	/**
	 * Validate a bug report
	 * @param BugReport An instance of BugReport. 
	 * @return boolean Return true if all required fields in bug report are set, otherwise, false.
	 */	
	public static boolean isValidBugReport(BugReport pBugReport) {
		if (pBugReport == null){
			return false;
		}
		if (pBugReport.getCytoscapeVersion().equals("")){
			return false;
		}
		if (pBugReport.getDescription().trim().equals("")){
			return false;
		}
		if (pBugReport.getSummary().trim().equals("")){
			return false;
		}
		if (pBugReport.getOS().trim().equals("")){
			return false;
		}
		if (pBugReport.getOSVersion().trim().equals("")){
			return false;
		}
		
		return true;
	}
}
