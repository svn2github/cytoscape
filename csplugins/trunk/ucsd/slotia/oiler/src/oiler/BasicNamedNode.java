package oiler;

/**
 * A minimal implementation of <code>NamedNode</code>.
 *
 * @author Samad Lotia
 */
public class BasicNamedNode implements NamedNode, Comparable<NamedNode>
{
	protected String name;

	public BasicNamedNode(final String name)
	{
		this.name = name;
	}

	public final String name()
	{
		return name;
	}

	public final String toString()
	{
		return name;
	}

	public final int compareTo(final NamedNode that)
	{
		return name.compareTo(that.name());
	}

	public boolean equals(final Object that)
	{
		if (that == null)
			return false;
		return name.equals(that.toString());
	}
}
