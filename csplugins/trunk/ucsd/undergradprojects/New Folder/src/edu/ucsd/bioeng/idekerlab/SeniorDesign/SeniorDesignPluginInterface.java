package edu.ucsd.bioeng.idekerlab.SeniorDesign;

public interface SeniorDesignPluginInterface extends CytoscapePlugin {
	private void getRightClick(Node NodeInfo); 
	private void setUpPopup(); 
	private void userStartButtonPress();
	private void setUpDefaults();
	private void searchThread(Node NodeInfo, String SpeciesName, DatabaseName);
	private void initializeDatabase(DatabaseName);
	private void updateNetwork();
}