//Author Gang Su.
//sugang@umich.edu


extern "C"
{


  // Igraph functions
  void createGraph(int edgeArray[], int length);
  bool isConnected();
  void simplify();
  int nodeCount();
  /*   bool isSimple(); */
  /*   int edgeCount(); */
  /*   void clusters(int membership[], int csize[], int* numCluster); */

  // Layouts
  void layoutCircle(double x[], double y[]);
  void starLayout(double x[], double y[], int centerId);
  void layoutFruchterman(double x[],
			 double y[],
			 int iter,
			 double maxDelta,
			 double area,
			 double coolExp,
			 double repulserad,
			 bool useSeed,
			 bool isWeighted,
			 double weights[]);


/*   void fastGreedy(int membership[], double* modularity, int csize[], int * numCluster); */
/*   void labelPropagation(int membership[], double* modularity); */
/*   void walkTrap(int membership[], double*modularity, int csize[], int * numCluster); */
/*   void edgeBetweenness(int membership[], double*modularity, int csize[], int * numCluster); */
/*   void spinGlass(int membership[], double* modularity, int csize[], int* numCluster); */
/*   void spinGlassSingle(int target, int community[], int* community_size); //only compute community close to the target id */
  

  //test functions
  int nativeAdd(int a, int b);
/*   void nativeIncrement(int* iptr); */
/*   int nativeArrayReset(int data[], int length); */
/*   int nativeCountAdd(int value); */
/*   void nativeArrayTest(int data[]); */
/*   void nativeMemoryAllocate(int **data, int *length); //pointer to pointer reference */
/*   void nativePointerMemoryAllocate(int *data, int *length); //single pointer reference */
/*   void nativeListAllocate(int*** data, int** list_lengths, int* data_length); //Return a zig-zag type */
/*   void nativeMatrixAllocate(int** data, int* nrow, int* ncol); */

}
