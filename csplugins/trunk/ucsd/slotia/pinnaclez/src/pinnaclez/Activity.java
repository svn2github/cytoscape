package pinnaclez;

/**
 * Instances of this class are associated with each node
 * in the network. It specifies the name of the
 * node's gene and the corresponding index
 * in ExpressionMatrix's matrix[] array if it exists.
 */
public class Activity
{
	public String name;
	public int matrixIndex;

	public String toString()
	{
		return name;
	}

	public int getMatrixIndex()
	{
		return matrixIndex;
	}
}
