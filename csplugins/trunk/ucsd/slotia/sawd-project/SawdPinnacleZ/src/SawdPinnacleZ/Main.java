package SawdPinnacleZ;

import org.apache.commons.cli.HelpFormatter;

public class Main
{
	public static void main(String[] args)
	{
		Settings settings = null;
		try
		{
			settings = new Settings(args);
		}
		catch (Settings.SettingsParseException e)
		{
			if (e.getMessage() != null)
			{
				System.err.println(e.getMessage());
			}
			else
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("pinnaclez [OPTIONS] server port class_file matrix_file network_file", e.getOptions());
			}
			return;
		}

		SawdClient client = new SawdClient(settings.server, Integer.parseInt(settings.port));
		if (settings.reset)
		{
			client.set_global_attribute(States.SETUP, "empty");
			client.set_global_attribute(States.FILTERING, "empty");
		}

		Iteration.setup(client, settings);
		doSetup(client, settings);
		doRealIteration(client, settings);

		boolean run = true;
		while (run)
			run = Iteration.computeRandom(client, settings);
		
		System.err.print("Waiting for iterations to finish..."); System.err.flush();
		while (!iterationsFinished(client, settings))
		{
			try { Thread.sleep(1000); }
			catch (InterruptedException e) {}
			System.err.print("."); System.err.flush();
		}
		System.err.println(" done.");

		doFiltering(client, settings);
		client.close();
	}

	private static void doSetup(SawdClient client, Settings settings)
	{
		String status = client.get_global_attribute(States.SETUP);
		if (status.equals(States.SETUP_DONE))
		{
			System.err.println("Setup already complete.");
			return;
		}
		else if (status.equals(States.SETUP_RUNNING))
		{
			System.err.print("Waiting for setup to complete..."); System.err.flush();
			while (status.equals(States.SETUP_RUNNING))
			{
				try { Thread.sleep(500); }
				catch (InterruptedException e) {}
				status = client.get_global_attribute(States.SETUP);

				if (settings.verbose)
				{
					System.err.print("."); System.err.flush();
				}
			}
			System.err.println(" done.");
		}
		else
		{
			System.err.print("Setting up..."); System.err.flush();
			Setup.setup(client, settings);
			System.err.println(" done.");
		}
	}

	private static void doRealIteration(SawdClient client, Settings settings)
	{
		String status = client.get_global_attribute(States.ITERATION + "real");
		if (status.equals(States.ITERATION_DONE))
		{
			if (settings.verbose)
				System.err.println("Real iteration already complete.");
			return;
		}
		else if (status.equals(States.ITERATION_RUNNING))
		{
			if (settings.verbose)
			{
				System.err.print("Waiting for real iteration to complete..."); System.err.flush();
			}
			while (status.equals(States.ITERATION_RUNNING))
			{
				try { Thread.sleep(1000); }
				catch (InterruptedException e) {}
				status = client.get_global_attribute(States.ITERATION + "real");

				if (settings.verbose)
				{
					System.err.print("."); System.err.flush();
				}
			}
			if (settings.verbose)
				System.err.println(" done.");
			
		}
		else
			Iteration.computeReal(client, settings);
	}

	private static void doFiltering(SawdClient client, Settings settings)
	{
		String status = client.get_global_attribute(States.FILTERING);
		if (status.equals(States.FILTERING_DONE))
			System.err.println("Filtering already done.");
		else if (status.equals(States.FILTERING_RUNNING))
			System.err.println("Filtering is being done.");
		else
		{
			client.set_global_attribute(States.FILTERING, States.FILTERING_RUNNING);
			Filter.filterST1(client, settings);
			Filter.filterST2(client, settings);
			client.set_global_attribute(States.FILTERING, States.FILTERING_DONE);
		}
	}

	private static boolean iterationsFinished(SawdClient client, Settings settings)
	{
		for (int i = 0; i < settings.numOfTrials; i++)
                {
                        String status = client.get_global_attribute(States.ITERATION + Integer.toString(i));
                        if (!status.equals(States.ITERATION_DONE))
                                return false;
                }
		return true;
	}
}
