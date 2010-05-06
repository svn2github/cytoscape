package org.idekerlab.PanGIAPlugin.networks.hashNetworks;

import giny.model.Edge;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.networks.DirectedSFEdge;
import org.idekerlab.PanGIAPlugin.networks.SEdge;
import org.idekerlab.PanGIAPlugin.networks.SFEdge;
import org.idekerlab.PanGIAPlugin.networks.SFNetwork;
import org.idekerlab.PanGIAPlugin.networks.UndirectedSEdge;
import org.idekerlab.PanGIAPlugin.networks.UndirectedSFEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.utilities.IIterator;
import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;
import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import org.idekerlab.PanGIAPlugin.data.StringVector;

public class FloatHashNetwork extends SFNetwork implements Iterable<SFEdge> {
	private Map<String, Set<SEdge>> nodeMap;
	private Map<SEdge, SFEdge> edgeMap;

	public FloatHashNetwork(boolean selfOk, boolean directed, int startsize) {
		super(selfOk, directed);
		this.edgeMap = new HashMap<SEdge, SFEdge>(startsize);
		this.nodeMap = new HashMap<String, Set<SEdge>>(100);
	}

	public FloatHashNetwork(String file, boolean selfOk, boolean directed) {
		super(selfOk, directed);
		Load(file, 0, 1, 2);
	}

	public FloatHashNetwork(String file, boolean selfOk, boolean directed,
			int n1col, int n2col, int valcol) {
		super(selfOk, directed);
		Load(file, n1col, n2col, valcol);
	}
	
	public FloatHashNetwork(final CyNetwork network, final String edgeAttrName, boolean selfOk, boolean directed) {
		super(selfOk, directed);
		this.edgeMap = new HashMap<SEdge, SFEdge>(network.getEdgeCount());
		this.nodeMap = new HashMap<String, Set<SEdge>>();
		final CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		
		for (Integer edgeIdx: network.getEdgeIndicesArray()) {
			final Edge edge = network.getEdge(edgeIdx);
			final Double attrValue = edgeAttrs.getDoubleAttribute(edge.getIdentifier(), edgeAttrName);
			if (attrValue != null)
				add(new UndirectedSFEdge(edge.getSource().getIdentifier(), edge.getTarget().getIdentifier(), 
					 attrValue.floatValue()));
		}
		
	}
	

	public FloatHashNetwork(TypedLinkNetwork<String, Float> net) {
		super(net.isSelfOK(), net.isDirected());

		int numEdges = net.numEdges();
		this.edgeMap = new HashMap<SEdge, SFEdge>(numEdges);
		this.nodeMap = new HashMap<String, Set<SEdge>>();

		for (TypedLinkEdge<String, Float> e : net.edgeIterator())
			this.add(e.source().value(), e.target().value(), e.value());
	}
	
	private void convert(final CyNetwork network, final CyAttributes edgeAttr, final String attrName) {
		
	}

	protected void Load(String file, int n1col, int n2col, int valcol) {
		int numEdges = FileUtil.countLines(file);
		this.edgeMap = new HashMap<SEdge, SFEdge>(numEdges);
		this.nodeMap = new HashMap<String, Set<SEdge>>();

		for (String line : new FileIterator(file)) {
			String[] cols = line.split("\t");
			this.add(new UndirectedSFEdge(cols[n1col], cols[n2col], Float
					.valueOf(cols[valcol])));
		}
	}

	private void updateNodeMap(SEdge i) {
		Set<SEdge> iset = nodeMap.get(i.getI1());
		if (iset == null) {
			Set<SEdge> newIset = new HashSet<SEdge>();
			newIset.add(i);
			nodeMap.put(i.getI1(), newIset);
		} else
			iset.add(i);

		iset = nodeMap.get(i.getI2());
		if (iset == null) {
			Set<SEdge> newIset = new HashSet<SEdge>();
			newIset.add(i);
			nodeMap.put(i.getI2(), newIset);
		} else
			iset.add(i);
	}

	public boolean isSelfOk() {
		return selfOk;
	}

	public boolean isDirected() {
		return directed;
	}

	public boolean contains(SEdge e) {
		return this.contains(e.getI1(), e.getI2());
	}

	public Iterator<SFEdge> iterator() {
		return edgeMap.values().iterator();
	}

	public Set<? extends SFEdge> getEdges() {
		return new HashSet<SFEdge>(edgeMap.values());
	}

	public Set<String> getNodes() {
		return new HashSet<String>(nodeMap.keySet());
	}

	public void add(SFEdge i) {
		this.edgeMap.put(i, i);
		this.updateNodeMap(i);
	}

	public void add(String n1, String n2, float value) {
		if (this.directed)
			this.add(new DirectedSFEdge(n1, n2, value));
		else
			this.add(new UndirectedSFEdge(n1, n2, value));
	}

	public int numEdges() {
		return this.edgeMap.size();
	}

	public int numNodes() {
		return this.nodeMap.size();
	}

	public FloatHashNetwork subNetwork(Set<String> nodes) {

		FloatHashNetwork subnet = new FloatHashNetwork(this.selfOk,
				this.directed, this.numEdges());

		for (SFEdge i : this)
			if (nodes.contains(i.getI1()) && nodes.contains(i.getI2()))
				subnet.add(i);

		return subnet;
	}

	public float edgeValue(String n1, String n2) {
		return edgeValue(new UndirectedSEdge(n1, n2));
	}

	public float edgeValue(SEdge i) {
		SFEdge f = edgeMap.get(i);
		if (f == null)
			return Float.NaN;
		else
			return f.value();
	}

	public boolean contains(String n1, String n2) {
		return edgeMap.containsKey(new UndirectedSEdge(n1, n2));
	}

	public TypedLinkNetwork<String, Float> asTypedLinkNetwork() {
		TypedLinkNetwork<String, Float> out = new TypedLinkNetwork<String, Float>(
				this.selfOk, this.directed);

		for (String node : this.nodeMap.keySet())
			out.addNode(node);

		for (SFEdge i : this)
			out.addEdgeWNodeUpdate(i.getI1(), i.getI2(), this.edgeValue(i));

		return out;
	}

	public FloatHashNetwork shuffleNodes() {
		StringVector nodes = new StringVector(this.getNodes());
		StringVector rnodes = nodes.sample(nodes.size(), false);
		Map<String, String> rnodeMap = new HashMap<String, String>(rnodes
				.size(), 1);

		for (int i = 0; i < rnodes.size(); i++)
			rnodeMap.put(nodes.get(i), rnodes.get(i));

		FloatHashNetwork out = new FloatHashNetwork(this.selfOk, this.directed,
				this.numEdges());
		for (SFEdge i : this)
			out.add(i);

		return out;
	}

	public IIterator<String> nodeIterator() {
		return new IIterator<String>(this.nodeMap.keySet().iterator());
	}

	public IIterator<SFEdge> edgeIterator() {
		return new IIterator<SFEdge>(this.iterator());
	}

	public void save(String file) {
		BufferedWriter bw = FileUtil.getBufferedWriter(file, false);

		try {
			for (SFEdge e : this)
				bw
						.write(e.getI1() + "\t" + e.getI2() + "\t" + e.value()
								+ "\n");

			bw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public FloatHashNetwork subNetworkExclusive(Set<String> nodes) {

		FloatHashNetwork subnet = new FloatHashNetwork(this.selfOk,
				this.directed, this.numEdges());

		for (SFEdge i : this)
			if (nodes.contains(i.getI1()) && nodes.contains(i.getI2()))
				subnet.add(i);

		return subnet;
	}

}
