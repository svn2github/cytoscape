package SawdPinnacleZ;

class Setup
{
	public static void setup(SawdClient client, Settings settings)
	{
		client.set_global_attribute(States.SETUP, States.SETUP_RUNNING);

		for (int i = 0; i < settings.numOfTrials + 1; i++)
			client.set_global_attribute(States.ITERATION + Integer.toString(i), States.ITERATION_EMPTY);
		
		client.set_global_attribute(States.SETUP, States.SETUP_DONE);
	}
}
