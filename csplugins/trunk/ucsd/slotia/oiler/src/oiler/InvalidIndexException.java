package oiler;

/**
 * This is the exception for invalid indices.
 *
 * @author Samad Lotia
 */
public class InvalidIndexException extends RuntimeException
{
	/**
	 * @param name The name of the parameter that has the invalid index
	 * @param index The invalid index
	 */
	public InvalidIndexException(String name, int index)
	{
		super(name + " is an invalid index (value: " + index + ')');
	}
}
