//Author Gang Su.
//sugang@umich.edu


extern "C"
{
  //test functions
  int nativeAdd( int a, int b );
/*   void nativeIncrement(int* iptr); */
/*   int nativeArrayReset(int data[], int length); */
/*   int nativeCountAdd(int value); */
/*   void nativeArrayTest(int data[]); */
/*   void nativeMemoryAllocate(int **data, int *length); //pointer to pointer reference */
/*   void nativePointerMemoryAllocate(int *data, int *length); //single pointer reference */
/*   void nativeListAllocate(int*** data, int** list_lengths, int* data_length); //Return a zig-zag type */
/*   void nativeMatrixAllocate(int** data, int* nrow, int* ncol); */

  //igraph functions
  void createGraph(int edgeArray[], int length);
/*   bool isSimple(); */
  bool isConnected();
  void simplify();
/*   void clusters(int membership[], int csize[], int* numCluster); */
  int nodeCount();
/*   int edgeCount(); */

/*   void fastGreedy(int membership[], double* modularity, int csize[], int * numCluster); */
/*   void labelPropagation(int membership[], double* modularity); */
/*   void walkTrap(int membership[], double*modularity, int csize[], int * numCluster); */
/*   void edgeBetweenness(int membership[], double*modularity, int csize[], int * numCluster); */
/*   void spinGlass(int membership[], double* modularity, int csize[], int* numCluster); */
/*   void spinGlassSingle(int target, int community[], int* community_size); //only compute community close to the target id */
  //Can also make a function to compute an array of ids, if possible in later versions.


}
