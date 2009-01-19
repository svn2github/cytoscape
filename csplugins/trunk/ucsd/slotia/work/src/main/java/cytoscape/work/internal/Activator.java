package cytoscape.work.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import cytoscape.work.TaskManager;

import cytoscape.work.*;

public class Activator implements BundleActivator
{
	public void start(BundleContext context)
	{
		TaskManager consoleTaskManager = new ConsoleTaskManager(System.out, new java.util.Locale("ur", "PK"));
		context.registerService(
			ConsoleTaskManager.class.getName(),
			consoleTaskManager,
			new Hashtable());
		TaskManager swingTaskManager = new SwingTaskManager();
		context.registerService(
			SwingTaskManager.class.getName(),
			swingTaskManager,
			new Hashtable());

		//swingTaskManager.execute(new MyTask());
		swingTaskManager.execute(new SuperTask("Example SuperTask", new MyTask(), new MyTask(), new MyTask(), new MyTask()));
	}

	public void stop(BundleContext context)
	{
	}

	class MyTask implements Task, Progressable
	{
		double progress = 0.0;
		String statusMessage = "";
		boolean cancel = false;

		public void run() throws Exception
		{
			System.out.println("MyTask started!");

			statusMessage = "Starting first task";
			somethingComplicated();
			if (cancel) return;
			progress = 0.25;

			statusMessage = "Starting second task";
			somethingComplicated();
			if (cancel) return;
			progress = 0.50;

			statusMessage = "Starting third task";
			somethingComplicated();
			if (cancel) return;
			progress = 0.75;

			statusMessage = "Starting forth task";
			somethingComplicated();
			progress = 1.0;

			System.out.println("MyTask finished!");
		}

		void somethingComplicated()
		{
			for (double i = 0.0; i < 10000.0; i += 0.001)
			{
				Math.sin(Math.tan(Math.sin(Math.tan(Math.sin(Math.tan(i))))));
			}
		}

		public String getTitle()
		{
			return "MyTask";
		}

		public String getStatusMessage()
		{
			return statusMessage;
		}

		public double getProgress()
		{
			return progress;
		}

		public void cancel()
		{
			cancel = true;
		}
	}
}
