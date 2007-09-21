package pinnaclez.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import oiler.util.IntIntHashMap;

import pinnaclez.ExpressionMatrix;
import pinnaclez.Statistics;

public class ExpressionMatrixReader
{
	protected ExpressionMatrixReader() {}

	/**
	 * Reads an expression matrix file.
	 * @param classMap A class map.
	 * @param expressionMatrixFileName The name of the file containing
	 * the expression matrix.
	 * @return An expression matrix after being Z-scored.
	 * @throws ParsingException if opening or reading the file failed.
	 */
	public static ExpressionMatrix read(final Map<String,Integer> classMap,
					final String expressionMatrixFileName)
					throws ParsingException
	{
		FileReader fileReader = null;
		try
		{
			fileReader = new FileReader(expressionMatrixFileName);
		}
		catch (FileNotFoundException e)
		{
			throw new ParsingException("Expression matrix", e.getMessage());
		}

		return read(classMap, new BufferedReader(fileReader));
	}

	/**
	 * Reads an expression matrix file.
	 * @param classMap A class map.
	 * @param expressionDataReader A buffered stream containing
	 * the expression matrix.
	 * @return An expression matrix after being Z-scored.
	 * @throws ParsingException if opening or reading the file failed.
	 */
	public static ExpressionMatrix read(Map<String,Integer> classMap,
					BufferedReader expressionDataReader)
					throws ParsingException
	{
		List<String> experiments = new ArrayList<String>();
		List<Integer> classes = new ArrayList<Integer>();

		int lineCount = 1;
		int columnCount = 1;

		// Read the first line
		String line = null;
		try
		{
			line = expressionDataReader.readLine();
		}
		catch (IOException e)
		{
			throw new ParsingException("Expression matrix", "Failed to read first line: " + e.getMessage());
		}
		
		// Skip lines with '#' at the beginning
		while (line.matches("[ \\t]*#.*"))
		{
			try
			{
				line = expressionDataReader.readLine();
			}
			catch (IOException e)
			{
				throw new ParsingException("Expression matrix", lineCount, e.getMessage());
			}
			lineCount++;
		}

		
		StringTokenizer tokenizer = new StringTokenizer(line);

		try
		{
			tokenizer.nextToken(); // Skip the first token
		}
		catch (NoSuchElementException e)
		{
			throw new ParsingException("Expression matrix", "first line is empty");
		}
		
		while(tokenizer.hasMoreTokens())
		{
			columnCount++;
			String experiment = tokenizer.nextToken();
			experiments.add(experiment);

			Integer classLabel = classMap.get(experiment);
			if (classLabel == null)
				throw new ParsingException("Expression matrix", lineCount, columnCount,
							"class number is not specified in the class file");
			classes.add(classLabel);
		}

		List<String> genes = new ArrayList<String>();
		List<List<Double>> matrix = new ArrayList<List<Double>>();

		// Begin reading each line in the matrix
		while (true)
		{
			try
			{
				line = expressionDataReader.readLine();
			}
			catch (IOException e)
			{
				throw new ParsingException("Expression matrix", lineCount, e.getMessage());
			}

			if (line == null)
				break;
			
			lineCount++;
			
			// Skip lines with '#' at the beginning
			if (line.matches("[ \\t]*#.*"))
				continue;
				
			columnCount = 1;
			List<Double> geneValues = new ArrayList<Double>();
			matrix.add(geneValues);
			tokenizer = new StringTokenizer(line);

			try
			{
				genes.add(tokenizer.nextToken());
			}
			catch (NoSuchElementException e)
			{
				throw new ParsingException("Expression matrix", lineCount, "no gene tag");
			}

			if (tokenizer.countTokens() != experiments.size())
				throw new ParsingException("Expression matrix", lineCount,
								"not enough columns of experiment data");

			int index = 0;
			while (tokenizer.hasMoreTokens())
			{
				columnCount++;

				Double value;
				try
				{
					value = new Double(tokenizer.nextToken());
				}
				catch (NumberFormatException e)
				{
					throw new ParsingException("Expression matrix", lineCount, columnCount,
								"could not read expression value");
				}
				geneValues.add(value);
			}
		}

		// Copy data we've read into ExpressionMatrix
		ExpressionMatrix result = new ExpressionMatrix(genes.size(), experiments.size());
		for (int y = 0; y < matrix.size(); y++)
			for (int x = 0; x < matrix.get(y).size(); x++)
				result.matrix[y][x] = matrix.get(y).get(x).doubleValue();

		for (int y = 0; y < genes.size(); y++)
			result.genes[y] = genes.get(y);

		for (int x = 0; x < experiments.size(); x++)
		{
			result.experiments[x] = experiments.get(x);
			result.classes[x] = classes.get(x).intValue();
		}

		transform(result);
		return result;
	}

	/**
	 * Performs Z-transform/Z-score of each row in an ExpressionMatrix.
	 * Z-scoring entails changing the values of a random variable such that
	 * its mean becomes 0 and its standard deviation becomes 1.
	 *
	 * Formula (in TeX):
	 * Given values $x_i$, where $1 \leq i \leq n$:
	 * $x'_i=\frac{x_i - \mu}{\sigma}$
	 */
	private static final void transform(final ExpressionMatrix matrix)
	{
		for (int y = 0; y < matrix.matrix.length; y++)
		{
			final int N = matrix.matrix[y].length;
			final double mean = Statistics.sum(matrix.matrix[y]) / N;
			final double stddev = Statistics.stddev(mean, matrix.matrix[y]);

			for (int x = 0; x < N; x++)
				matrix.matrix[y][x] = Statistics.zScore(matrix.matrix[y][x], mean, stddev);
		}
	}
}
