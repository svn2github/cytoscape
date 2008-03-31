package clusterMaker.algorithms.MCL;

public class Matrix {

	private MatrixVector[] vectors;
	private double inflationParameter;
	private int number_iterations;
	private double clusteringThresh;
	private double inflatedThresh;
	private int size;
	public int numClusters = -1;
	private double[][] graph;
	
	
	
	public Matrix(double[][] graph, double inflationParameter,int number_iterations,double clusteringThresh)
	{
		this.size = graph.length;
		vectors = new MatrixVector[size];
		this.number_iterations = number_iterations;
		
		this.clusteringThresh = clusteringThresh;
			
		this.inflationParameter = inflationParameter;
		this.inflatedThresh = Math.pow(clusteringThresh,inflationParameter); /*values in vector below inflation threshold after inflation will be less then the clustering threshold */
		
		//Create set of vectors defining matrix
		for(int i = 0; i < size; i++)
			vectors[i] = new MatrixVector(graph[i],i,inflatedThresh);
		this.graph = toArray();
	}
	
	//return array represetion of matrix
	private double[][] toArray()
	{
		double[][] graph = new double[size][size];
		
		for (int i=0; i < size; i++)
			for (int j =0; j <size; j++)
				graph[i][j] = vectors[i].getIndex(j);
				
		
		return graph;
	}
	
	//Multiply Matrix by self
	private void expand()
	{
		double[][] graph = toArray();
		
		double[][] d = new double[this.size][this.size];
		for(int i=0; i < size; i++) {
			d[i] = vectors[i].matrixMultiply(graph);
		}
		for(int i=0; i < size; i++) {
			vectors[i].updateVector(d[i]);
		}
			
	}
	
	//inflate all vectors in matrix
	private void inflate()
	{
		for(int i=0; i < size; i++)
			vectors[i].inflate(inflationParameter);
	}
	
	//Run 8 iteration of expansion and inflation
	private void iterateMCL()
	{
		
		Console.startNewConsoleWindow(0,number_iterations,"MCL");
		
		for (int i=0; i<number_iterations; i++)
		{
			Console.setBarValue(i);
			Console.setBarText("MCL clustering - iteration " + (i+1));
			expand();
			inflate();
		}
		
		Console.closeWindow();
		
	}
	
	
	private void inflate2(){
		double sum[] = new double[graph.length];
		for (int i = 0; i < sum.length; i++) {
			sum[i] = 0;
		}
		for (int i = 0; i < this.graph.length; i++) {
			for (int j = 0; j < this.graph.length; j++) {
				if(graph[i][j]!=0 ||graph[i][j]!= 1) graph[i][j] = Math.pow(graph[i][j], inflationParameter);
				sum[i]+=graph[i][j];
			}
		}
		for (int i = 0; i < this.graph.length; i++) {
			for (int j = 0; j < this.graph.length; j++) {
				graph[i][j] /=sum[i];
				if(graph[i][j]<inflatedThresh) graph[i][j] = 0;
				else if (1-graph[i][j]<inflatedThresh) graph[i][j] = 1;
			}
		}
		
		
	}
	
	private void expand2()
	{
		double[][] graphDummy =  new double[this.size][this.size];
		
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				graphDummy[i][j] = calculateMatrixMultiplyValue( i,  j);
			}
		}
		
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				graph[i][j] = graphDummy[i][j];
			}
		}
		
		
		
	}
	
	private double calculateMatrixMultiplyValue(int i, int j){
		double value = 0;
		for (int k = 0; k < this.graph.length; k++) {
			value+=(graph[i][k]*graph[k][j]);
		}
		
		return value;
	}
	
	
	
	//Runs MCL and returns array mapping node indices to clusters
	public double[] cluster()
	{
		Clustering clustering = new Clustering(size,clusteringThresh);
		iterateMCL();	
		System.out.println("Clustering Matrix");
		double[] clusterMatrix = clustering.clusterMatrix(toArray());
		numClusters = clustering.numClusters;
		return clusterMatrix;
	}
}
