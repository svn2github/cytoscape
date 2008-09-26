package bugreport;
import java.io.IOException;
import java.io.File;


public class TestBugReport {
	
	public TestBugReport() {
		
		// CASE 1 -- Submit a bugReport without attached file to Mantis bug tracker	
		
		// create a bug report without attached file
		BugReport bugReport1 = createTestBugReport1();
		if (!BugReportUtil.isValidBugReport(bugReport1)) {
			System.out.println("Invalid bug report!");
			System.exit(1);
		}
		
		String retStr = "";
		try {
			retStr = BugReportUtil.submitBugReport(bugReport1);
		}
		catch (IOException ioe) {
			System.out.println("Error to submit the report\n");
			ioe.printStackTrace();
		}
		
		if (!retStr.equals("")) {
			System.out.println("Received message from Mantis server\n");
			System.out.println(retStr);
		}

		// CASE 2 Submit a duplicated bug report (same summary -- title), Mantis should add a new note appended to the 
		// existing one, instead of create a new issue for this one
		
		
		// CASE 3 -- submit a BugReport with attached file
		//BugReport bugReport2 = createTestBugReport2();
		//if (!BugReportUtil.isValidBugReport(bugReport2)) {
		//	System.out.println("Invalid bug report!");
		//	System.exit(1);
		//}
		
	}
	
	// Create a bugReport without attached file
	private BugReport createTestBugReport1(){
		BugReport bugReport = new BugReport();
		
		bugReport.setBugReporter("guest");
		bugReport.setCytoscapeVersion("3.0");
		bugReport.setOS("WIN");
		bugReport.setOSVersion("XP");
		bugReport.setSummary("test bug report 1 -- without attached file");
		bugReport.setDescription("user description");		
		
		return bugReport;
	}

	// Create a bugReport with attached file
	private BugReport createTestBugReport2(){
		BugReport bugReport = new BugReport();
		
		bugReport.setBugReporter("guest");
		bugReport.setCytoscapeVersion("3.0");
		bugReport.setOS("WIN");
		bugReport.setOSVersion("XP");
		bugReport.setSummary("test bug report 2 -- with attached session file");
		bugReport.setDescription("user description");		
		// create a tmpFile
		
		//bugReport.setAttachedFile(null);
		
		// Delete the tmpFile after submission

		return bugReport;
	}

	// Entry point to run the tests
	public static void main(String[] argv) {
		new TestBugReport();
	}
}
