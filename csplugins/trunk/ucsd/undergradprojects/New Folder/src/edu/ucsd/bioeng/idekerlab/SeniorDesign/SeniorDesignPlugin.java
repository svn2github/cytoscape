package edu.ucsd.bioeng.idekerlab.SeniorDesign;

public class SeniorDesignPlugin implements SeniorDesignPluginInterface
{
	
	void getRightClick(Node NodeInfo)
	{
		setUpPopup();
	}

	void setUpPopup()
	{
		intializeSwingGui();
		setUpDefaults();
	
		//wait for user input
		while(true)
		{
			userCheckBoxes();
			userDropDown();
			userStartButtonPress();
		}
	}

	void userStartButtonPress()
	{
		//start downloads
		for(each checked database checkbox)
		{
			new SearchThread(Node NodeInfo, String SpeciesName, DatabaseName)
		}
	}

	void setUpDefaults()
	{
		loadDatabaseNames();
		scanSpecies();
	}

	void searchThread(Node NodeInfo, String SpeciesName, DatabaseName) extends Thread
	{
		initializeOurWebServiceClient(DatabaseName);
		getData();

	}

	void initializeOurWebServiceClient(DatabaseName)
	{
		//use polymorphism to define getData function
	}
	
	void updateNetwork()
	{
		//update using CyNetwork / CyNode
	}
	}
}