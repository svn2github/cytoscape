package edu.ucsd.bioeng.idekerlab.SeniorDesign;

public class SeniorDesignPlugin implements SeniorDesignPluginInterface
{

        private void getRightClick(Node NodeInfo)
        {
                setUpPopup();
        }

        private void setUpPopup()
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

        private void userStartButtonPress()
        {
                //start downloads
                for(each checked database checkbox)
                {
                        new SearchThread(Node NodeInfo, String SpeciesName, DatabaseName)
                }
        }

        private void setUpDefaults()
        {
                loadDatabaseNames();
                scanSpecies();
        }

        private void searchThread(Node NodeInfo, String SpeciesName, DatabaseName) extends Thread
        {
                initializeOurWebServiceClient(DatabaseName);
                getData();

        }

        private void initializeOurWebServiceClient(DatabaseName)
        {
                //use polymorphism to define getData function
        }

        private void updateNetwork()
        {
                //update using CyNetwork / CyNode
        }
        }
}
