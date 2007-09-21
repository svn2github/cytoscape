package pinnaclez.io;

/**
 * A general exception thrown for parsing files.
 */
public class ParsingException extends Exception
{
	public ParsingException(String fileType, int line, int column, String message)
	{
		super(fileType + " file error at line " + line + ", column " + column + ": " + message);
	}

	public ParsingException(String fileType, int line, String message)
	{
		super(fileType + " file error at line " + line + ": " + message);
	}

	public ParsingException(String fileType, String message)
	{
		super(fileType + " file error: " + message);
	}
}
