package bugreport;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;


public class SubmitBugReportUtil {
	
	public static String submitBugReport(BugReport pBugReport) throws IOException{
		String BUG_REPORT_URL = "http://tocai.ucsd.edu/mantis/cy_bug_report_auto.php";
	
		ClientHttpRequest httpReq = new ClientHttpRequest(BUG_REPORT_URL);
		
		httpReq.setParameter("user", pBugReport.getBugReporter());
		//testReq.setParameter("category", "asdf"); 
		//httpReq.setParameter("severity", pBugReport.getS);
		httpReq.setParameter("summary", pBugReport.getSummary());
		httpReq.setParameter("description", pBugReport.getDescription());
		httpReq.setParameter("cytoscape_version", pBugReport.getCytoscapeVersion());
		
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
}
