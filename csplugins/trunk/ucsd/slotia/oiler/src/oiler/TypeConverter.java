package oiler;

/**
 * Used to convert node objects of type N1 to N2 and
 * edge objects of type E1 to E2.
 *
 * <p><b>Why use this class?</b>
 * This provides a convenient way to convert node or
 * edge objects of a graph to a different type by
 * the <code>Graph</code> constructors. For example,
 * when reading a SIF file, edges are given in a String
 * format. If the graph needs to be converted so the edges
 * are weighted, we can do so by this interface.
 * </p>
 *
 * <p><b>Example:</b>
 * <pre>
 * Graph&lt;String,String&gt; graph = SIFReader.read("mygraph.sif");
 * TypeConverter&lt;String,String,String,Double&gt; converter
 * 	= new TypeConverter&lt;String.String,String,Double&gt;()
 * {
 *	public String convertNodeObject(String original)
 *	{
 *		return original;
 *	}
 *	public Double convertEdgeObject(String original)
 *	{
 *		return new Double(original);
 *	}
 * }
 * Graph&lt;String,Double&gt; weightedGraph = new LinkedListGraph(graph, converter);
 * ...
 * </pre>
 * </p>
 *
 * @author Samad Lotia
 */
public interface TypeConverter<N1,E1,N2,E2>
{
	public N2 convertNodeObject(N1 original);
	public E2 convertEdgeObject(E1 original);
}
