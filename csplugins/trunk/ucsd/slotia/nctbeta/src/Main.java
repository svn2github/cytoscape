import nct.basic.BasicMutableUndirectedGraph;

public class Main
{
	public static void main(String[] args)
	{
		Main main = new Main();
		main.run();
	}

	public void run()
	{
		BasicMutableUndirectedGraph<MyNode,MyEdge> graph = new BasicMutableUndirectedGraph<MyNode,MyEdge>();
		int node1 = graph.addNode(new MyNode("node1"));
		int node2 = graph.addNode(new MyNode("node2"));
		int edge1 = graph.addEdge(node1, node2, new MyEdge("edge1"));
		int edge2 = graph.addEdge(node1, node1, new MyEdge("edge2"));
		int edge3 = graph.addEdge(node2, node2, new MyEdge("edge3"));
		int edge4 = graph.addEdge(node2, node2, new MyEdge("edge4"));
		graph.diag();

		graph.removeNode(node2);
		System.out.println("---------");
		graph.diag();
	}

	private class MyNode
	{
		public String name;
		public MyNode(String name)
		{
			this.name = name;
		}
		public String toString()
		{
			return name;
		}
	}

	private class MyEdge
	{
		public String name;
		public MyEdge(String name)
		{
			this.name = name;
		}
		public String toString()
		{
			return name;
		}
	}
}
