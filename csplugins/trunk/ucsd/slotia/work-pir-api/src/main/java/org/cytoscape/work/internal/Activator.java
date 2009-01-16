package org.cytoscape.work.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.cytoscape.work.*;

public class Activator implements BundleActivator
{
	public void start(BundleContext context)
	{
		TaskManager consoleTaskManager = new ConsoleTaskManager(System.out);
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

	class MyTask implements Task
	{
		boolean cancel = false;
		public void run(TaskMonitor taskMonitor) throws java.io.IOException, java.io.FileNotFoundException
		{
			taskMonitor.setTitle("MyTask");
			taskMonitor.setStatusMessage("Starting first task");
			somethingComplicated();
			if (cancel) return;
			taskMonitor.setProgress(0.25);

			taskMonitor.setStatusMessage("Starting second task");
			somethingComplicated();
			if (cancel) return;
			taskMonitor.setProgress(0.50);

			//if (!cancel) throw new java.io.IOException("BLAH");

			taskMonitor.setStatusMessage("Starting third task");
			somethingComplicated();
			if (cancel) return;
			taskMonitor.setProgress(0.75);

			taskMonitor.setProgress(1.00);
			taskMonitor.setStatusMessage("Starting forth task");
			somethingComplicated();

			System.out.println("MyTask finished!");
		}

		void somethingComplicated()
		{
			for (double i = 0.0; i < 5000.0; i += 0.001)
			{
				Math.sin(Math.tan(Math.sin(Math.tan(Math.sin(Math.tan(i))))));
			}
		}

		public void cancel()
		{
			cancel = true;
		}
	}
}
