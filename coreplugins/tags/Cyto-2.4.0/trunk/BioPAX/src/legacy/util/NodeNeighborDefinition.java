package legacy.util;

/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * This is a hook to narrow the definition of <i>node neighbor</i> as
 * originally defined in <code>GraphCompiler</code>.
 * The original definition can only be narrowed by this hook - that is, if
 * node A <i>is not</i> a neighbor of node B in the original definition,
 * then this interface <i>is not</i> able to define A as a neighbor of B.
 * On the  other hand if node A <i>is</i> a neighbor of node B according to
 * the original definition in <code>GraphCompiler</code>, then this interface
 * is able to define that node A <i>is not</i> a neighbor of node B.
 */
public interface NodeNeighborDefinition {

    /**
     * Lets programmers override the default definition of
     * <i>node neighbor</i>.
     * This method defines the answer to the question:
     * in a given graph, does edge E at edge
     * index <code>edgeIndex</code>, whose endpoint nodes have indices
     * <code>nodeAIndex</code> and <code>nodeBIndex</code>, have the property
     * that makes node at index <code>nodeAIndex</code> a neighbor of node
     * at index <code>nodeBIndex</code>?  The way we've stated this (and the way
     * this interface was designed) makes the definition of node neighbors
     * base itself on examining exactly one edge at a time, ingoring all
     * other topological aspects of the graph (other edges, other nodes, etc.)
     * at each edge examination.<p>
     * This is a hook to narrow the definition of <i>node neighbor</i> defined
     * in <code>GraphTopology</code>.  With this hook, our new definition
     * becomes such: Node A is a <i>neighbor</i> of node B if and only if
     * <nobr><code>isNodeNeighbor(e, a, b)</code></nobr> returns
     * <code>true</code> for some combination of edge index <code>e</code>,
     * node index <code>a</code>, and node index <code>b</code> such that
     * there exists, in our graph, an edge with index <code>e</code> whose
     * end-nodes have indices <code>a</code> and <code>b</code> (notice that
     * I'm talking about endpoints, not source and target nodes).<p>
     * You should notice right away that that this definition
     * defines a binary relation on our set of nodes which is a subset of
     * the original <i>neighbor</i> binary relation defined in
     * <code>GraphTopology</code>.  This is exactly what I mean by a restricting
     * (or narrowing) definition.<p>
     * In order for this definition to function properly in a framework,
     * the method <code>isNodeNeighbor()</code> must be called twice for every
     * <code>edgeIndex</code> in a graph.  Of course, if node A is a neighbor
     * of node B as defined by a previous call to
     * <code>isNodeNeighbor()</code>, it would be superfluous (but not
     * necessarily harmful) to call this method again against the same
     * nodes and order of nodes, but against a differnt edge.  &quot;You can't
     * undo node A being a neighbor of node B once an edge has defined it
     * so&quot;.  This hook is able to define a
     * &quot;neighbor&quot; binary relation on our node set which in not
     * necessarily symmetric (non-symmetric means:
     * if '~' stands for &quot;is a neighbor
     * of&quot;, then <nobr>&quot;A ~ B&quot;</nobr> does not necessarily imply
     * <nobr>&quot;B ~ A&quot;</nobr>).
     */
    public boolean isNodeNeighbor(int edgeIndex,
            int nodeAIndex,
            int nodeBIndex);

}
