package pinnaclez.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class ClassReader
{
	protected ClassReader() {}

	public static Map<String,Integer> read(String classFileName) throws ParsingException
	{
		FileReader fileReader = null;
		try
		{
			fileReader = new FileReader(classFileName);
		}
		catch (FileNotFoundException e)
		{
			throw new ParsingException("Class", e.getMessage());
		}

		return read(new BufferedReader(fileReader));
	}

	public static Map<String,Integer> read(BufferedReader classReader) throws ParsingException
	{
		int lineCount = 0;

		Map<String,Integer> classMap = new HashMap<String,Integer>();
		while (true)
		{
			String line = null;
			lineCount++;
			try
			{
				line = classReader.readLine();
			}
			catch (IOException e)
			{
				throw new ParsingException("Class", lineCount, e.getMessage());
			}

			if (line == null)
				break;
			
			// Skip lines with '#' at the beginning
			if (line.matches("[ \\t]*#.*"))
				continue;

			StringTokenizer tokenizer = new StringTokenizer(line);

			String experiment;
			try
			{
				experiment = tokenizer.nextToken();
			}
			catch (NoSuchElementException e)
			{
				throw new ParsingException("Class", lineCount, "failed to read experiment");
			}

			Integer classLabel;
			try
			{
				String token = tokenizer.nextToken();
				classLabel = new Integer(token);
			}
			catch (NoSuchElementException e)
			{
				throw new ParsingException("Class", lineCount, "failed to read class label");
			}
			catch (NumberFormatException e)
			{
				throw new ParsingException("Class", lineCount, "failed to parse class label");
			}

			classMap.put(experiment, classLabel);
		}

		return classMap;
	}
}
