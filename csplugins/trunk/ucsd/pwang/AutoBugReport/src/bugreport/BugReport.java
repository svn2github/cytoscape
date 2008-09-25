package bugreport;

import java.io.File;

class BugReport {
	private String bugReporter = "guest";		
	private String summary = "unknown";
	private String description = "none";
	private String cytoscape_version = "3.x";
	private File[] attachedFiles = null;
	//private String summary = "unknown";
		
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

	public void setAttachedFiles(File[] pAttachedFiles){
		attachedFiles = pAttachedFiles;
	}
		
	//
	public String getBugReporter(){
		return bugReporter;
	}

	public String getCytoscapeVersion(){
		return cytoscape_version;
	}
		
	public String getDescription() {
		return description;
	}
	public String getSummary(){
		return summary;
	}
		
	public File[] getAttachedFiles(){
		return attachedFiles;
	}				
}
