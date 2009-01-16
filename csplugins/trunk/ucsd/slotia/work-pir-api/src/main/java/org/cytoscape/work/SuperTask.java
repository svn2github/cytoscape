package org.cytoscape.work;

/**
 * Provides a means for nested subtasks to be
 * grouped together under one <code>SuperTask</code>.
 *
 * <p><code>SuperTask</code> has the following behavior:</p>
 * <ul>
 *
 * <li><code>SuperTask</code>'s title is what is given in the
 * constructor.</li>
 *
 * <li>If <code>SuperTask</code> is canceled, it will call the
 * currently executing <code>Task</code>'s <code>cancel</code> method.
 * It will not execute any <code>Task</code>s waiting to be executed.</li>
 *
 * <li>If the currently executing subtask sets a title that is not null and is
 * not an empty string, <code>SuperTask</code> will set its status message to
 * "<i>subtask title</i><code>:</code><i>subtask status message</i>".
 * However, if the subtask's title is not set, is null, or is an empty string,
 * <code>SuperTask</code> will set its status message to the subtask's status
 * message.</li>
 * </ul>
 */
public class SuperTask implements Task
{
	final Task[] subtasks;
	final String title;
	boolean cancel = false;
	int currentTaskIndex = -1;

	/**
	 * This is a convenience constructor that employs Java's variable
	 * arguments syntactic sugar.
	 * For example, this constructor can be used as follows:
	 *
	 * <p><code>new SuperTask("Example", new MyTask1(), new MyTask2(),
	 * new MyTask3());</code></p>
	 *
	 * @param title The title of the <code>SuperTask</code> that describes
	 * succinctly what the <code>SuperTask</code> does.
	 * @param subtasks The subtasks to be grouped together by
	 * <code>SuperTask</code>. The order of <code>subtasks</code> is the
	 * order of execution.
	 */
	public SuperTask(String title, Task ... subtasks)
	{
		//this(title, (Task[]) subtasks);
		this.title = title;
		this.subtasks = subtasks;
	}

	/**
	 * @param title The title of the <code>SuperTask</code> that describes
	 * succinctly what the <code>SuperTask</code> does.
	 * @param subtasks The subtasks to be grouped together by
	 * <code>SuperTask</code>. The order of <code>subtasks</code> is the
	 * order of execution.
	 */
	/*
	For some reason, this constructor won't work with the same constructor
	using variable arguments.
	public SuperTask(String title, Task[] subtasks)
	{
		this.title = title;
		this.subtasks = subtasks;
	}
	*/

	public void run(TaskMonitor superTaskMonitor) throws Exception
	{
		superTaskMonitor.setTitle(title);
		superTaskMonitor.setProgress(0.0);
		final TaskMonitor subTaskMonitor = new SubTaskMonitor(superTaskMonitor);
		for (currentTaskIndex = 0; currentTaskIndex < subtasks.length; currentTaskIndex++)
		{
			subtasks[currentTaskIndex].run(subTaskMonitor);
			if (cancel) break;
		}
	}

	public void cancel()
	{
		// currentTaskIndex is copied into another variable
		// in order to prevent the situation where currentTaskIndex is
		// being incremented while another thread is executing cancel().
		final int index = currentTaskIndex;
		cancel = true;
		if (index >= 0 && index < subtasks.length)
			subtasks[index].cancel();
	}

	class SubTaskMonitor implements TaskMonitor
	{
		final TaskMonitor superTaskMonitor;
		String subtitle = null;

		public SubTaskMonitor(TaskMonitor superTaskMonitor)
		{
			this.superTaskMonitor = superTaskMonitor;
		}

		public void setTitle(String subtitle)
		{
			this.subtitle = subtitle;
		}

		public void setProgress(double subprogress)
		{
			superTaskMonitor.setProgress((currentTaskIndex + subprogress) / ((double) subtasks.length));
		}

		public void setStatusMessage(String subStatusMessage)
		{
			if (subtitle == null || subtitle.length() == 0)
				superTaskMonitor.setStatusMessage(subStatusMessage);
			else
				superTaskMonitor.setStatusMessage(String.format("%s: %s", subtitle, subStatusMessage));
		}
	}
}
