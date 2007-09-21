package pinnaclez;

/**
 * Standard deviation scoring
 */
public class TScore extends AbstractActivityScore
{
	public TScore(final ExpressionMatrix matrix)
	{
		super(matrix);
	}

	public double scoreVector(final double[] values, final int[] classes)
	{
		final int class1 = classes[0];
		int num1=0, num2=0;
		double mean1=0, mean2=0, sf1=0, sf2=0;
		double t;
        
		for(int i=0; i < values.length; i++)
		{
			if(classes[i] == class1)
			{
				mean1 += values[i];
				num1++;
			}
			else
			{
				mean2 += values[i];
				num2++;
			}
		}
        
		mean1 = mean1/num1;
		mean2 = mean2/num2;

		for(int i = 0; i < values.length; i++)
		{
			if(classes[i] == class1)
			{
				final double delta = values[i] - mean1;
				sf1 += delta * delta;
			}
			else
			{
				final double delta = values[i] - mean2;
				sf2 += delta * delta;
			}
		}

		sf1 = sf1/(num1*(num1-1));
		sf2 = sf2/(num2*(num2-1));

		t = (mean1-mean2)/Math.sqrt(sf1+sf2);

		return Math.abs(t);
	}
}
