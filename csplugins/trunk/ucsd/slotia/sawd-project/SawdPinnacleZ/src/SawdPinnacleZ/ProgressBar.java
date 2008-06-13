package SawdPinnacleZ;

public class ProgressBar
{
	int totalBars, total, current = 0, barsWritten = 0;

	public ProgressBar(int total)
	{
		this(25, total);
	}

	public ProgressBar(int totalBars, int total)
	{
		this.totalBars = totalBars;
		this.total = total;
	}

	public void start()
	{
		System.out.print("  |");
		for (int i = 0; i < totalBars; i++)
			System.out.print('-');
		System.out.println('|');
		System.out.print("   ");
		System.out.flush();
	}

	public synchronized void increment()
	{
		if (current < total)
		{
			current++;
			update();
		}
	}

	private void update()
	{
		int bars = totalBars * current / total;	
		while (barsWritten < bars)
		{
			System.out.print(">");
			System.out.flush();
			barsWritten++;
		}
		if (barsWritten == totalBars)
			System.out.println();
	}
}
