package org.cytoscape.work;

/**
 * Provides a means for nested subtasks to be
 * grouped together under one <code>SuperTask</code>.
 *
 * <p><code>SuperTask</code> behaves as follows:</p>
 * <ul>
 *
 * <li><b>Threads </b><code>SuperTask</code> executes all of its subtasks
 * in the same thread provided by <code>TaskManager</code>.</li>
 *
 * <li><b>Execution Order </b><code>SuperTask</code> executes all of its subtasks
 * in the same order as given in its constructor.</li>
 *
 * <li><b>Cancelling </b>If <code>SuperTask</code> is canceled, it will call the
 * currently executing <code>Task</code>'s <code>cancel</code> method.
 * It will not execute any following <code>Task</code>s waiting to be executed.</li>
 *
 * <li><b>Title </b><code>SuperTask</code>'s title is what is given in the
 * constructor.</li>
 *
 * <li><b>Status Message </b>If the currently executing subtask sets a title that is not null and is
 * not an empty string, <code>SuperTask</code> will set its status message to
 * "<i>subtask title</i><code>:</code><i>subtask status message</i>".
 * However, if the subtask's title is not set, is null, or is an empty string,
 * <code>SuperTask</code> will set its status message to the subtask's status
 * message.</li>
 *
 * <li><b>Progress </b><code>SuperTask</code> divides its progress equally between
 * each of its subtasks unless weights are specified. For example, if there are four subtasks: when
 * the first subtask is executing, it will set its progress to 0%; when
 * the second subtask is executing, it will set its progress to 25%;
 * and so on. This behavior can be modified by specifying weights.</li>

 * <li><b>Exceptions </b>If a subtask throws an exception, subtasks that follow will not be
 * executed.</li>
 * </ul>
 *
 * @author Pasteur
 */
public class SuperTask implements Task
{
	final Task[] subtasks;
	final double[] weights;
	double weightSum = 0.0;

	String title;
	boolean cancel = false;
	int currentTaskIndex = -1;
	double partialSum = 0.0;

	/**
	 * Constructs a <code>SuperTask</code> with a given list of
	 * subtasks and a title.
	 *
	 * <p>The constructor can take an array of <code>Task</code>s:</p>
	 * <p><pre><code>
	 * Task[] tasks = {
	 *  new MyTask1(),
	 *  new MyTask2(),
	 *  new MyTask3()
	 * };
	 * SuperTask superTask = new SuperTask("Example", tasks);
	 * </code></pre></p>
	 *
	 * <p>This constructor is also a convenience that employs Java's variable
	 * arguments syntactic sugar:</p>
	 *
	 * <p><code>new SuperTask("Example", new MyTask1(), new MyTask2(),
	 * new MyTask3());</code></p>
	 *
	 * @param title The title of the <code>SuperTask</code> that describes
	 * succinctly what the <code>SuperTask</code> does.
	 * @param subtasks The subtasks to be grouped together by
	 * <code>SuperTask</code>. The order of <code>subtasks</code> is the
	 * order of execution. Each subtask has an equal amount of the progress bar.
	 */
	public SuperTask(String title, Task ... subtasks)
	{
		this.title = title;
		this.subtasks = subtasks;
		this.weights = new double[subtasks.length];
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = 1.0;
			weightSum += weights[i];
		}
	}

	/**
	 * Constructs a <code>SuperTask</code> with a given list of
	 * subtasks, a title, and weights for each subtask.
	 *
	 * This constructor allows one to specify the weights of each
	 * subtask. Weights specify how much of the progress bar
	 * is given to each subtask. A weight can be any positive number,
	 * as the proportion of the progress bar is measured against
	 * the weight's ratio to the total sum of all weights.
	 * To allocate 25% of the progress bar to the first task, 50% to the second,
	 * and 25% to the third, one may do the following:
	 *
	 * <p><pre><code>
	 * Task[] tasks = {
	 *  new MyTask1(),
	 *  new MyTask2(),
	 *  new MyTask3()
	 * };
	 *
	 * double[] weights = {
	 *  2.0,
	 *  4.0,
	 *  2.0
	 * };
	 *
	 * SuperTask superTask = new SuperTask("Example", tasks, weights);
	 * </code></pre></p>
	 *
	 * @param title The title of the <code>SuperTask</code> that describes
	 * succinctly what the <code>SuperTask</code> does.
	 * @param subtasks The subtasks to be grouped together by
	 * <code>SuperTask</code>. The order of <code>subtasks</code> is the
	 * order of execution.
	 * @param weights The weights allotted to each subtask. All weights
	 * must be a positive number.
	 * @throws IllegalArgumentException if the length of <code>weights</code>
	 * and <code>subtasks</code> are not equal or if any of the weights are less than 0.0.
	 */
	public SuperTask(String title, Task[] subtasks, double[] weights)
	{
		this.title = title;
		this.subtasks = subtasks;
		this.weights = weights;

		if (weights.length != subtasks.length)
			throw new IllegalArgumentException("weights and subtasks must have the same length");
		for (int i = 0; i < weights.length; i++)
		{
			if (weights[i] < 0.0)
				throw new IllegalArgumentException(String.format("weight[%d] cannot be less than 0.0", i));
			weightSum += weights[i];
		}
	}

	public void run(TaskMonitor superTaskMonitor) throws Exception
	{
		superTaskMonitor.setTitle(title);
		superTaskMonitor.setProgress(0.0);
		final TaskMonitor subTaskMonitor = new SubTaskMonitor(superTaskMonitor);
		for (currentTaskIndex = 0; (currentTaskIndex < subtasks.length) && (!cancel); currentTaskIndex++)
		{
			subtasks[currentTaskIndex].run(subTaskMonitor);
			partialSum += weights[currentTaskIndex];
		}

		cancel = false;
		currentTaskIndex = -1;
		partialSum = 0.0;
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
			superTaskMonitor.setProgress((partialSum + weights[currentTaskIndex] * subprogress) / weightSum);
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
