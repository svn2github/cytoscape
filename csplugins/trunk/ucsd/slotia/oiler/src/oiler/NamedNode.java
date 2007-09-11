package oiler;

/**
 * An interface for node objects that have names.
 * This interface provides a means for guaranteeing named nodes for
 * node objects.
 *
 * @author Samad Lotia
 */
public interface NamedNode extends Comparable<NamedNode>
{
	public String name();
}
