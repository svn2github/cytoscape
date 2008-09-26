package bugreport;

import java.io.File;

class BugReport {
	private String bugReporter = "guest";// AutomaticBugReporter		
	private String summary = "";
	private String description = "";
	private String cytoscape_version = "";
	private File attachedFile = null;
	private String os = ""; // WIN/LINUX/MAC/OTHER
	private String os_version = "";
	private String additionalInfo = "";
	private String stepsToReproduce = "";
		
	public void setBugReporter(String pReporter){
		bugReporter = pReporter;
	}
		
	public void setCytoscapeVersion(String pVersion){
		cytoscape_version = pVersion;
	}
		
	public void setDescription(String pDescription){
		description = pDescription;
	}

	public void setSummary(String pSummary){
		summary = pSummary;
	}

	public void setOS(String pOS){
		os = pOS;
	}

	public void setOSVersion(String pVersion){
		os_version = pVersion;
	}

	public void setStepsToReproduce(String pStepsToReproduce){
		stepsToReproduce = pStepsToReproduce;
	}

	public void setAdditionalInfo(String pInfo){
		additionalInfo = pInfo;
	}

	public void setAttachedFile(File pAttachedFile){
		attachedFile = pAttachedFile;
	}
		
	/**
	 * @return String The reporter account on Cytoscape's Mantis bug tracker.
	 */
	public String getBugReporter(){
		return bugReporter;
	}

	public String getCytoscapeVersion(){
		return cytoscape_version;
	}

	/**
	 * @return String The stack trace of the exception.
	 */		
	public String getDescription() {
		return description;
	}

	/**
	 * @return String Summary (title) of the bug report.
	 */		
	public String getSummary(){
		return summary;
	}

	/**
	 * @return String Operating system -- WIN/LINUX/MAC/OTHER.
	 */		
	public String getOS(){
		return os;
	}

	/**
	 * @return String Version of Operating System.
	 */		
	public String getOSVersion(){
		return os_version;
	}
	
	/**
	 * @return String User's description -- What were you doing when this problem happened?
	 */
	public String getAdditionalInfo(){
		return additionalInfo;
	}

	/**
	 * @return String The command list (history) to reproduce the problem
	 */	
	public String getStepsToReproduce(){
		return stepsToReproduce;
	}

	/**
	 * @return File A session file.
	 */
	public File getAttachedFile(){ // max size: 5M (Mantis 1.1.1)
		return attachedFile;
	}				
}
