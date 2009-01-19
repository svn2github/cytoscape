package cytoscape.work;

import cytoscape.work.internal.util.Interfaces;

/**
 * Provides a means for nested subtasks to be
 * grouped together under one <code>SuperTask</code>.
 *
 * <p><code>SuperTask</code> has the following behavior:</p>
 * <ul>
 *
 * <li><code>SuperTask</code> executes all of its subtasks
 * in the same thread provided by <code>TaskManager</code>.</li>
 *
 * <li><code>SuperTask</code>'s title is what is given in the
 * constructor.</li>
 *
 * <li>If <code>SuperTask</code> is canceled, it will call the
 * currently executing <code>Task</code>'s <code>cancel</code> method.
 * It will not execute any <code>Task</code>s waiting to be executed.</li>
 *
 * <li>If the currently executing subtask implements <code>Progressable</code>,
 * <code>SuperTask</code> will set its status message to
 * "<i>subtask title</i><code>:</code><i>subtask status message</i>".
 * However, if the subtask does not implement <code>Progressable</code>
 * <code>SuperTask</code> will set its status message to the subtask's
 * title.</li>
 *
 * <li><code>SuperTask</code> divides its progress bar equally between
 * each of its subtasks. For example, if there are four subtasks: when
 * the first subtask is executing, it will set its progress to 0%; when
 * the second subtask is executing, it will set its progress to 25%;
 * and so on.</li>
 * </ul>
 */
public class SuperTask implements Task, Progressable
{
	Task[] subtasks;
	String title;
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
		this.subtasks = subtasks;
		this.title = title;
	}

	public void run() throws Exception
	{
		for (currentTaskIndex = 0; currentTaskIndex < subtasks.length; currentTaskIndex++)
		{
			subtasks[currentTaskIndex].run();
			if (cancel) break;
		}
	}

	public String getTitle()
	{
		return title;
	}

	/*
	 In the methods getProgress(), getStatusMessage(), and cancel(),
	 one finds the statement "final int index = this.currentTaskIndex".
	 This statement is used to prevent the situation where run() increments
	 currentTaskIndex while these methods are using it.
	 
	 For example, let's assume there are 5 tasks, and currentTaskIndex is 4.
	 Thread 1 is executing run(), and Thread 2 is executing cancel(). Let's
	 examine an unlikely, yet possible, situation:

	 Thread 2: if (0 <= currentTaskIndex && currentTaskIndexindex < subtasks.length)
	           -- this evaluates to true
	 Thread 1: currentTaskIndex++
	           -- currentTaskIndex is now = 5
	 Thread 2: subtasks[currentTaskIndex].cancel()
	           -- this causes an index out of bounds exception
	
	 We could have used locks to prevent this type of situation, but it would've
	 been messy and arduous. Instead, we save currentTaskIndex by storing it into
	 index at the beginning of the methods.
	 */

	public void cancel()
	{
		final int index = this.currentTaskIndex;
		if (0 <= index && index < subtasks.length)
			subtasks[index].cancel();
		cancel = true;
	}

	public double getProgress()
	{
		final int index = this.currentTaskIndex;
		if (!(0 <= index && index < subtasks.length))
			return 0.0;

		Task task = subtasks[index];
		if (Interfaces.implementsProgressable(task))
		{
			Progressable progressable = (Progressable) task;
			return (progressable.getProgress() + index) / ((double) subtasks.length);
		}
		else
		{
			return index / ((double) subtasks.length);
		}
	}

	public String getStatusMessage()
	{
		final int index = this.currentTaskIndex;
		if (!(0 <= index && index < subtasks.length))
			return "";

		Task task = subtasks[index];
		if (Interfaces.implementsProgressable(task))
		{
			Progressable progressable = (Progressable) task;
			return String.format("%s: %s", task.getTitle(), progressable.getStatusMessage());
		}
		else
		{
			return task.getTitle();
		}
	}
}
