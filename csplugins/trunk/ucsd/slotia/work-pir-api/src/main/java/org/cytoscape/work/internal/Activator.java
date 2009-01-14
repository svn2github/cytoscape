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

		swingTaskManager.execute(new MyTask());
	}

	public void stop(BundleContext context)
	{
	}

	class MyTask implements Task
	{
		public void run(TaskMonitor taskMonitor)
		{
			System.out.println("MyTask started!");

			if (taskMonitor.needsToCancel()) return;
			taskMonitor.setStatusMessage("Starting first task");
			somethingComplicated();
			taskMonitor.setProgress(0.25);

			if (taskMonitor.needsToCancel()) return;
			taskMonitor.setStatusMessage("Starting second task");
			somethingComplicated();
			taskMonitor.setProgress(0.50);

			//taskMonitor.setException(new Throwable("Yadda"));
			if (taskMonitor.needsToCancel()) return;
			taskMonitor.setStatusMessage("Starting third task");
			somethingComplicated();
			taskMonitor.setProgress(0.75);

			if (taskMonitor.needsToCancel()) return;
			taskMonitor.setStatusMessage("Starting forth task");
			somethingComplicated();

			System.out.println("MyTask finished!");
		}

		void somethingComplicated()
		{
			for (double i = 0.0; i < 10000.0; i += 0.001)
			{
				Math.sin(Math.tan(Math.sin(Math.tan(Math.sin(Math.tan(i))))));
			}
		}
	}
}
