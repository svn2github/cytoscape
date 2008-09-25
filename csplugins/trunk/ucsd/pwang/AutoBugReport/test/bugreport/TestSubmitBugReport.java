package bugreport;
import java.io.IOException;
import java.io.File;


public class TestSubmitBugReport {
	
	public static void main(String[] argv) {
		
		// create a bug report
		BugReport bugReport = new BugReport();
		
		bugReport.setBugReporter("guest");
		bugReport.setCytoscapeVersion("3.0");
		bugReport.setSummary("Summary text");
		bugReport.setDescription("user description");
		bugReport.setAttachedFiles(null);

		// submit the bug report to Mantis bug tracker
		String retStr = "";
		try {
			retStr = SubmitBugReportUtil.submitBugReport(bugReport);
		}
		catch (IOException ioe) {
			System.out.println("Caught IOEXception\n");
			ioe.printStackTrace();
		}
		
		// Check the returned string from the Mantis server
		if (retStr.equals("Done")) {
			System.out.println("Report bug -- success");				
		}
		else {
			System.out.println("Report bug -- failed");
		}
	}
}
