//Author Gang Su
//sugang@umich.edu


/*include this igraph.h first*/
#include "igraph.h"
#include "igraphJNA.h"
#include <iostream>
#include <stdio.h>
#include <stdlib.h>

using namespace std;


// Testing passing of an integer
// Try global variables just in case
int count = 0;

// Use a global graph object
// Make sure there's only one active graph @ a time to reduce confusions
igraph_t g;
int existsGraph = 0;

void createGraph(int edgeArray[], int length){

  // Destroy old graph if it exists
  if (existsGraph) {
    igraph_destroy(&g);
    existsGraph = 0;
  }

  igraph_vector_t v;
  igraph_vector_init(&v, length);
  for(int i=0; i<length; i++){
    VECTOR(v)[i] = edgeArray[i];
  }

  igraph_create(&g, &v, 0, 0);
  existsGraph = 1;
  printf("Graph created! Number of nodes: %d\n", nodeCount());

  // Free resources no longer needed
  igraph_vector_destroy(&v);
}


bool isConnected(){
	igraph_bool_t connected;
	igraph_is_connected(&g, &connected, IGRAPH_STRONG);
	return (bool)connected;
}

void simplify(){
  igraph_simplify(&g, 1, 1, 0);
}



void layoutCircle(double x[], double y[]){
	igraph_matrix_t locs;
	igraph_matrix_init(&locs, 0, 0);

	// Execute layout
	igraph_layout_circle(&g, &locs);
	
	long int nRow = igraph_matrix_nrow(&locs);
	long int nCol = igraph_matrix_ncol(&locs);
	for(int i=0; i<nRow; i++){
		x[i] = MATRIX(locs, i, 0);
		y[i] = MATRIX(locs, i, 1);
	}
}

void starLayout(double x[], double y[], int centerId) {

	igraph_matrix_t locs;
	igraph_matrix_init(&locs, 0, 0);

	// Execute layout
	igraph_layout_star(&g, &locs, centerId, NULL);

	long int nRow = igraph_matrix_nrow(&locs);
	long int nCol = igraph_matrix_ncol(&locs);
	for(int i=0; i<nRow; i++){
		x[i] = MATRIX(locs, i, 0);
		y[i] = MATRIX(locs, i, 1);
	}


}

// Fruchterman - Reingold Layout
// void layoutFruchterman(double x[], 
// 		       double y[], 
// 		       int iter, 
// 		       double maxDelta, 
// 		       double area, 
// 		       double coolExp, 
// 		       double repulserad, 
// 		       bool useSeed){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_fruchterman_reingold(&g, 
// 					   &locs, 
// 					   iter, 
// 					   maxDelta, 
// 					   area, 
// 					   coolExp, 
// 					   repulserad, 
// 					   useSeed, 
// 					   0);
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}	
// }




//////////////////////////////

//Boolean, test whether the current graph is simple
//Can only be called when a graph has been loaded
// bool isSimple(){
// 	igraph_bool_t simple;
// 	igraph_is_simple(&g, &simple);
// 	return (bool)simple;
// 	//return 1;
// }


// void clusters(int membership[], int csize[], int* numCluster){
// 	igraph_vector_t membership_v;
// 	igraph_vector_t csize_v;
// 	igraph_integer_t numCluster_v = 0;

// 	igraph_vector_init(&membership_v, 0);
// 	igraph_vector_init(&csize_v, 0);

// 	//last argument is ignored
// 	//The problem here is that the length of the array is unknown
// 	igraph_clusters(&g, &membership_v, &csize_v, &numCluster_v, IGRAPH_WEAK);

// 	*numCluster = (int)numCluster_v;
	
// 	//Convert data back
// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	for(int i=0; i<igraph_vector_size(&csize_v); i++){
// 		csize[i] = VECTOR(csize_v)[i];
// 	}
	
// }

//get nodeCount and edgeCount of the current loaded graph
int nodeCount(){
	return (int)igraph_vcount(&g);
}

// int edgeCount(){
// 	return (int)igraph_ecount(&g);
// }

// //Map community algorithms
// //Note that some community algorithms work only on connected graphs
// void fastGreedy(int membership[], double* modularity, int csize[], int* numCluster){
// 	igraph_vector_t modularity_v;
// 	igraph_matrix_t merges;
// 	igraph_vector_t csize_v;
// 	igraph_vector_t membership_v;



// 	igraph_vector_init(&modularity_v, 0);
// 	igraph_matrix_init(&merges, 0, 0);
// 	igraph_vector_init(&csize_v, 0);
// 	igraph_vector_init(&membership_v, 0);

// 	igraph_community_fastgreedy(&g, 0, &merges, &modularity_v);
// 	igraph_community_to_membership(&merges, igraph_vcount(&g), igraph_vector_which_max(&modularity_v), &membership_v, &csize_v);

// 	//need only map modularity, membership, csize and number of clusters
// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	for(int i=0; i<igraph_vector_size(&csize_v); i++){
// 		csize[i] = VECTOR(csize_v)[i];
// 	}

// 	*modularity = igraph_vector_max(&modularity_v);
// 	*numCluster = igraph_vector_size(&csize_v);
// }

// void labelPropagation(int membership[], double* modularity){
// 	igraph_vector_t membership_v;
// 	igraph_vector_init(&membership_v, 0); //if i don't initialize will give run time error
// 										  //vectors and matrices will be initialized	
// 	igraph_real_t modularity_v;
	
// 	igraph_community_label_propagation(&g, &membership_v, 0, 0, 0);
// 	igraph_modularity(&g, &membership_v, &modularity_v, 0);

// 	//Reset these parameters to avoid mistakes.
// 	//*numCluster = 0;
// 	//for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 	//	csize[i] = 0;
// 	//}


// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	*modularity = modularity_v;
// 	//*modularity = 0.0;
// }

// void walkTrap(int membership[], double* modularity, int csize[], int* numCluster){
// 	igraph_vector_t modularity_v;
// 	igraph_matrix_t merges;
// 	igraph_vector_t csize_v;
// 	igraph_vector_t membership_v;



// 	igraph_vector_init(&modularity_v, 0);
// 	igraph_matrix_init(&merges, 0, 0);
// 	igraph_vector_init(&csize_v, 0);
// 	igraph_vector_init(&membership_v, 0);

// 	//igraph_community_fastgreedy(&g, 0, &merges, &modularity_v);
// 	//The default step is 4 steps.
// 	igraph_community_walktrap(&g, 0, 4, &merges, &modularity_v);
	
// 	igraph_community_to_membership(&merges, igraph_vcount(&g), igraph_vector_which_max(&modularity_v), &membership_v, &csize_v);

// 	//need only map modularity, membership, csize and number of clusters
// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	for(int i=0; i<igraph_vector_size(&csize_v); i++){
// 		csize[i] = VECTOR(csize_v)[i];
// 	}

// 	*modularity = igraph_vector_max(&modularity_v);
// 	*numCluster = igraph_vector_size(&csize_v);
// }

// void edgeBetweenness(int membership[], double* modularity, int csize[], int*numCluster){
// 	//The problem of edge betweenness is that 
// 	//need to calculate modularity for all merges
// 	igraph_vector_t modularity_v;
// 	igraph_matrix_t merges;
// 	igraph_vector_t csize_v;
// 	igraph_vector_t membership_v;
// 	igraph_vector_t result;
// 	igraph_real_t modularity_max;

// 	igraph_vector_init(&modularity_v, igraph_vcount(&g)-1); //Try to access items beyond the limit will give array out of bounds exception
// 	igraph_matrix_init(&merges, 0, 0);
// 	igraph_vector_init(&csize_v, 0);
// 	igraph_vector_init(&membership_v, 0);
// 	igraph_vector_init(&result, 0);

// 	igraph_community_edge_betweenness(&g, &result, 0, &merges, 0, 0);

// 	//set step to 10
// 	for(int i=0; i < igraph_vcount(&g)-1 ; i++){
// 		igraph_community_to_membership(&merges, igraph_vcount(&g), i, &membership_v, 0);
// 		igraph_modularity(&g, &membership_v, &modularity_max, 0);
		
// 		VECTOR(modularity_v)[i] = modularity_max; //assign modularity vector
// 		//VECTOR(modularity_v)[
// 	}
	
	
// 	igraph_community_to_membership(&merges, igraph_vcount(&g), igraph_vector_which_max(&modularity_v), &membership_v, &csize_v);


// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	for(int i=0; i<igraph_vector_size(&csize_v); i++){
// 		csize[i] = VECTOR(csize_v)[i];
// 	}

// 	*modularity = igraph_vector_max(&modularity_v);
// 	*numCluster = igraph_vector_size(&csize_v);
	

// 	//*modularity = 0;
// }

// void spinGlass(int membership[], double* modularity, int csize[], int*numCluster){
// 	igraph_real_t modularity_v;
// 	igraph_matrix_t merges;
// 	igraph_vector_t csize_v;
// 	igraph_vector_t membership_v;
// 	//igraph_vector_t result;
// 	//igraph_real_t modularity_max;

// 	//igraph_vector_init(&modularity_v, 0); //Try to access items beyond the limit will give array out of bounds exception
// 	igraph_matrix_init(&merges, 0, 0);
// 	igraph_vector_init(&csize_v, 0);
// 	igraph_vector_init(&membership_v, 0);
// 	//igraph_vector_init(&result, 0);
	
// 	//Possible to change to parameter to make it converge faster
// 	igraph_community_spinglass(&g, 0, &modularity_v, 0, &membership_v, &csize_v, 25, 0, 1.0, 0.1, 0.99, IGRAPH_SPINCOMM_UPDATE_SIMPLE, 1.0);

// 	//Here

// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}

// 	for(int i=0; i<igraph_vector_size(&csize_v); i++){
// 		csize[i] = VECTOR(csize_v)[i];
// 	}

// 	*modularity = modularity_v;
// 	*numCluster = igraph_vector_size(&csize_v);
// }

// void spinGlassSingle(int targetNode, int community[], int* community_size){
// 	//igraph_real_t modularity_v;
// 	//igraph_matrix_t merges;
// 	//igraph_vector_t csize_v;
// 	//igraph_vector_t membership_v;
// 	//igraph_vector_t result;
// 	//igraph_real_t modularity_max;

// 	//igraph_vector_init(&modularity_v, 0); //Try to access items beyond the limit will give array out of bounds exception
// 	//igraph_matrix_init(&merges, 0, 0);
// 	//igraph_vector_init(&csize_v, 0);
// 	//igraph_vector_init(&membership_v, 0);
// 	//igraph_vector_init(&result, 0);

// 	igraph_vector_t community_v;
// 	igraph_vector_init(&community_v, 0);

// 	igraph_community_spinglass_single(&g, 0, targetNode, &community_v, 0, 0, 0, 0, 25, IGRAPH_SPINCOMM_UPDATE_SIMPLE, 1.0);
// 	*community_size = igraph_vector_size(&community_v);
// 	for(int i=0; i<igraph_vector_size(&community_v); i++){
// 		community[i] = VECTOR(community_v)[i];
// 	}
	
// 	//return the community surrounding the given vertex

// }

// void leadingEigenvector(int membership[], double* modularity){
// 	igraph_matrix_t merges;
// 	igraph_vector_t membership_v;
// 	igraph_arpack_options_t options;

// 	//Dosen't really change the outcome.
// 	//options.mxiter = 5000000;
	
// 	igraph_real_t modularity_v;

// 	igraph_matrix_init(&merges, 0, 0);
//     igraph_vector_init(&membership_v, 0);
// 	igraph_arpack_options_init(&options);

// 	igraph_community_leading_eigenvector(&g, &merges, &membership_v, -1, &options);
// 	igraph_modularity(&g, &membership_v, &modularity_v, 0);

// 	*modularity = modularity_v;
// 	for(int i=0; i<igraph_vector_size(&membership_v); i++){
// 		membership[i] = VECTOR(membership_v)[i];
// 	}
// }





// int igraph_layout_fruchterman_reingold(const igraph_t *graph, igraph_matrix_t *res,
// 				       igraph_integer_t niter, igraph_real_t maxdelta,
// 				       igraph_real_t area, igraph_real_t coolexp, 
// 				       igraph_real_t repulserad, igraph_bool_t use_seed,
// 				       const igraph_vector_t *weight);



// void layoutRandom(double x[], double y[]){
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, 0, 0);
// 	igraph_layout_random(&g, &locs);
	
// 	long int nRow = igraph_matrix_nrow(&locs);
// 	long int nCol = igraph_matrix_ncol(&locs);
// 	for(int i=0; i<nRow; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}
// }

// //This one also needs scaling
// //Let's actually do scaling in java then, it's liner time anyway
// void layoutGraphOpt(double x[], double y[], int iter, double nodeCharge, double nodeMass, int springLength, double springConstant, double maxSaMovement, bool useSeed){
	
// 	//This algorithm can work on a current seting, so will intialize res with x and y first
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); //2 col, with x and y

// 	//Res need to be initalized
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	//It will be quite painful to make dialogs to set values for these crap
// 	igraph_layout_graphopt(&g, &locs, iter, nodeCharge, nodeMass, springLength, springConstant, maxSaMovement, useSeed);
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}

// }

// //still not working
// void layoutDRL(double x[], double y[], bool useSeed, int mode){
	
	
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	/*
// 	igraph_layout_drl_options_t options;
// 	if(mode == 1){
// 		igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_COARSEN);
// 	}
// 	else if(mode == 2){
// 		igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_COARSEST);
// 	}
// 	else if(mode == 3){
// 		igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_REFINE);
// 	}
// 	else if(mode == 4){
// 		igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_FINAL);
// 	}
//     else {
// 		igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_DEFAULT);
// 	}
// 	*/
	
// 	igraph_layout_drl_options_t options;
// 	igraph_layout_drl_options_init(&options, IGRAPH_LAYOUT_DRL_COARSEN);

// 	//Force directed DRL
// 	igraph_layout_drl(&g, &locs, 0, &options, 0, 0);

// 	//Return
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}
	
// }


// //
// void layoutKamadaKawai(double x[], double y[], int iter, double sigma, double initTemp, double coolExp, double kkConsts, bool useSeed){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_kamada_kawai(&g, &locs, iter, sigma, initTemp, coolExp, kkConsts, useSeed);
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}	
// }

// //Tree like structure
// void layoutReingoldTilford(double x[], double y[], int root){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_reingold_tilford(&g, &locs, root);
	
// 	//This is nice, can specify a root
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}	

// }

// //circular
// void layoutReingoldTilfordCircular(double x[], double y[], int root){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_reingold_tilford_circular(&g, &locs, root);

// 	//
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}	

// }

// //grid
// void layoutGridFruchtermanReingold(double x[], double y[], int iter, double maxDelta, double area, double coolExp, double repulserad, double cellSize, bool useSeed){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_grid_fruchterman_reingold(&g, &locs, iter, maxDelta, area, coolExp, repulserad, cellSize, useSeed);
	
// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}
	
// }

// //lgl
// void layoutLGL(double x[], double y[], int maxIt, double maxDelta, double area, double coolExp, double repulserad, double cellSize, int root){
// 	long int vcount = igraph_vcount(&g);
// 	igraph_matrix_t locs;
// 	igraph_matrix_init(&locs, vcount, 2); 
// 	for(int i=0; i<vcount; i++){
// 		MATRIX(locs, i, 0) = x[i];
// 		MATRIX(locs, i, 1) = y[i];
// 	}

// 	igraph_layout_lgl(&g, &locs, maxIt, maxDelta, area, coolExp, repulserad, cellSize, root);

// 	for(int i=0; i<vcount; i++){
// 		x[i] = MATRIX(locs, i, 0);
// 		y[i] = MATRIX(locs, i, 1);
// 	}

// }

// ///////////////////////////////////////////////////////////////////////////////
// //Shortest path function

// //Calculate shortest path as the distance matrix
// void distMatrix(double dist1d[]){

// 	//This will just map back the data to dist
// 	long nodeCount = igraph_vcount(&g);
// 	igraph_matrix_t dist;
// 	igraph_matrix_init(&dist,nodeCount, nodeCount); 

// 	igraph_vs_t nodes;
// 	igraph_vs_all(&nodes);

// 	//This idea is to get all the nodes.
// 	igraph_shortest_paths(&g, &dist, nodes, IGRAPH_ALL);

// 	int counter = 0;
// 	for(int i=0; i<nodeCount; i++){
// 		for(int j=0; j<nodeCount; j++){
// 			dist1d[counter] = MATRIX(dist, i, j);
// 			counter++;
// 		}
// 	}

// }








// ///////////////////////////////////////////////////////////////////////////////
// //These are testing functions
// void nativeArrayTest(int data[]){
// 	//try to pass unitialized array
// 	//Allocated to a different memory address, the passed array is not affected.
// 	data = new int[10];
// 	for(int i=0; i<10; i++){
// 		data[i] = i;
// 	}
// }


// int nativeCountAdd(int value){
// 	count += value;
// 	return count;
// }


extern "C"{
//Simple adding of two integers
  int nativeAdd( int a, int b )
  {
    return( a + b );
  }
}
// //Add one to the passed integer via pointer
// void nativeIncrement(int* iptr){
// 	*iptr += 1;
// 	//return a;
// }

// //Reset all the elements in integer array to 0
// //return the number of operations
// int nativeArrayReset(int data[], int length){
// 	int i;
// 	for(i=0; i<length; i++){
// 		data[i] = 10;
// 	}
// 	return i;
// }


// //Try to allocate some memory by malloc
// //This is the test for, 
// void nativeMemoryAllocate(int **data, int *length){
// 	//try that the memory allocation is done in c
// 	*length = 10;
// 	*data = (int*) malloc (*length * sizeof(int)); //allocate the space for array of length 10, this is equivalent to initialized the first item in data ...

// 	for(int i=0; i < *length; i++){
// 		//**data = i;
// 		//*data++; //move the pointer
// 		(*data)[i] = i;
// 	}

// 	//This won't waste array space
// 	//For vectors, just need to do a int **data
// }

// //This is not working very well...need int**data instead of int* data
// void nativePointerMemoryAllocate(int *data, int *length){
// 	*length = 10;
// 	data = (int*) malloc ( *length * sizeof(int)); //however, the memory is not freed. Wonder how long it will persist in memory.
	
// 	for(int i=0; i< *length; i++){
// 		data[i] = i+10;
// 	}
// }

// void nativeListAllocate(int*** data, int** list_lengths, int* data_length){
// 	//data containing pointers
// 	*data_length = 2;
// 	*list_lengths = (int*) malloc (*data_length * sizeof(int)); //The first element this pointer is pointing to, and the only one
	
// 	//int** data;
	
	
// 	//The problem is that data must be pointer to pointers
// 	*data = (int**) malloc(*data_length * sizeof(int*));

// 	//didn't allocate space for the arrays in data


//     //initialize some list_lengths
// 	(*list_lengths)[0] = 3;
// 	(*list_lengths)[1] = 4; //Onluy two elements

// 	//allocate memory for data
// 	//When getting a vector it's a lot different than this
// 	for(int i=0; i< *data_length; i++){
// 		(*data)[i] = (int*) malloc ( (*list_lengths)[i] * sizeof(int));
// 	}




// 	//This way to assign value is funky
// 	for(int i=0; i< *data_length; i++){
// 		for(int j=0; j < (*list_lengths)[i]; j++ ){
// 			((*data)[i])[j] = i+j*10;
// 		}
// 	}

// 	//*data_ref = data;
// }
