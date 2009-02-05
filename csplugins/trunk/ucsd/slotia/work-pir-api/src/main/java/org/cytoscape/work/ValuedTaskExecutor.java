package org.cytoscape.work;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

/**
 * A class for having <code>ValuedTask</code>s to be executed
 * by <code>TaskManager</code>s. After the <code>ValuedTask</code>
 * has completed execution, one can retrieve the result by
 * calling the <code>get()</code> method.
 *
 * This class is analogous to <code>FutureTask</code>, but with one
 * crucial difference: this class does not allow different threads
 * to cancel the <code>ValuedTask</code>, only the <code>TaskManager</code>
 * may cancel it. This limitation was a conscious decision, since it
 * greatly simplifies the complexity of this API.
 *
 * Here is an example of how it can be used:
 * <br>
 * <code>
 * ValuedTask&lt;Integer&gt; myValuedTask = ...;<br>
 * TaskMonitor taskMonitor = ...;<br>
 * ValuedTaskExecutor&lt;Integer&gt; myValuedTaskExecutor = new ValuedTaskExecutor&lt;Integer&gt;(myValuedTask);<br>
 * taskMonitor.execute(taskMonitor);<br>
 * ...<br>
 * Integer result = myValuedTaskExecutor.get();<br>
 * </code>
 */
public class ValuedTaskExecutor<V> implements Task
{
	/**
	 * Describes the state the <code>ValuedTask</code> is in.
	 */
	public static enum State
	{
		/**
		 * The <code>ValuedTask</code> is ready to be executed.
		 */
		READY,

		/**
		 * The <code>ValuedTask</code> is being executed.
		 */
		RUNNING,

		/**
		 * The <code>ValuedTask</code> has finished execution.
		 */
		COMPLETED,

		/**
		 * The <code>ValuedTask</code> was cancelled by the user.
		 */
		CANCELLED,

		/**
		 * The <code>ValuedTask</code> has terminated execution
		 * by throwing an exception.
		 */
		EXCEPTION_THROWN;
	}

	final ValuedTask<V> valuedTask;

	V result = null;
	State state = State.READY;
	Exception exception = null;

	public ValuedTaskExecutor(ValuedTask<V> valuedTask)
	{
		this.valuedTask = valuedTask;
	}

	/**
	 * Do <i>not</i> call this method!
	 * This will be called by the <code>TaskManager</code>.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		state = State.RUNNING;
		try
		{
			result = valuedTask.run(taskMonitor);
			if (state == State.RUNNING)
				state = State.COMPLETED;
		}
		catch (Exception exception)
		{
			this.exception = exception;
			state = State.EXCEPTION_THROWN;
			throw exception;
		}
		finally
		{
			synchronized(this)
			{
				this.notifyAll();
			}
		}
	}

	/**
	 * Do <i>not</i> call this method!
	 * This will be called by the <code>TaskManager</code>.
	 */
	public void cancel()
	{
		state = State.CANCELLED;
		valuedTask.cancel();
	}

	/**
	 * Retrieves the result produced by the <code>ValuedTask</code> if it
	 * has finished execution, otherwise it waits until it
	 * finishes execution.
	 *
	 * This method will block until the <code>ValuedTask</code> has
	 * finished--that is, its state is no longer
	 * <code>READY</code> or <code>RUNNING</code>.
	 *
	 * @return The result of the <code>ValuedTask</code>.
	 *
	 * @throws InterruptedException if the current thread was interrupted
	 * while waiting
	 * @throws ExecutionException if the <code>ValueTask</code> threw an
	 * exception
	 * @throws CancellationException if the user cancelled the
	 * <code>ValueTask</code>
	 */
	public V get()	
		throws	InterruptedException,
			ExecutionException,
			CancellationException
	{
		if (state == State.READY || state == State.RUNNING)
		{
			synchronized(this)
			{
				this.wait();
			}
		}

		if (state == State.CANCELLED)
			throw new CancellationException();
		else if (state == State.EXCEPTION_THROWN)
			throw new ExecutionException(exception);

		return result;
	}

	/**
	 * Retrieves the result produced by the <code>ValuedTask</code> if it
	 * has finished execution, otherwise it waits the specified amount of
	 * time until it finishes execution.
	 *
	 * This method will block until the <code>ValuedTask</code> has
	 * finished--that is, its state is no longer
	 * <code>READY</code> or <code>RUNNING</code>--or the specified
	 * wait has timed out.
	 *
	 * @return The result of the <code>ValuedTask</code>.
	 *
	 * @throws InterruptedException if the current thread was interrupted
	 * while waiting
	 * @throws ExecutionException if the <code>ValueTask</code> threw an
	 * exception
	 * @throws CancellationException if the user cancelled the
	 * <code>ValueTask</code>
	 * @throws TimeoutException if the wait period specified timed out
	 */
	public V get(long timeout, TimeUnit unit)
		throws	InterruptedException,
			ExecutionException,
			CancellationException,
			TimeoutException
	{
		if (state == State.READY || state == State.RUNNING)
		{
			synchronized(this)
			{
				unit.timedWait(this, timeout);
			}
		}

		if (state == State.CANCELLED)
			throw new CancellationException();
		else if (state == State.EXCEPTION_THROWN)
			throw new ExecutionException(exception);

		return result;
	}

	/**
	 * Retrieves the current state of the <code>ValuedTask</code>.
	 */
	public State getState()
	{
		return state;
	}
}
