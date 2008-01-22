package edu.ucsd.bioeng.idekerlab.SeniorDesign;

public interface SeniorDesignPluginInterface extends CytoscapePlugin {

		void getRightClick(Node NodeInfo); 
		void setUpPopup(); 
		void userStartButtonPress();
		void setUpDefaults();
		void searchThread(Node NodeInfo, String SpeciesName, DatabaseName);
		void initializeDatabase(DatabaseName);
		void updateNetwork();
}