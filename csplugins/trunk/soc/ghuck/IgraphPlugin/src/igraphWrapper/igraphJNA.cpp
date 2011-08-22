/**************************************************************************************
Copyright (C) Gerardo Huck, 2011
Copyright (C) Gang Su, 2009

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

#include "igraph.h"
#include "igraphJNA.h"
#include <iostream>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

// Use a global graph object
// Make sure there's only one active graph at a time to reduce confusions
igraph_t g;
int existsGraph = 0;

void createGraph(int edgeArray[], int length, int directed){

  // Destroy old graph if it exists
  destroy_graph();

  igraph_vector_t v;
  igraph_vector_init(&v, length);
  for(int i=0; i<length; i++){
    VECTOR(v)[i] = edgeArray[i];
  }

  igraph_create(&g, &v, 0, directed);
  existsGraph = 1;
  //  printf("Graph created! Number of nodes: %d\n", nodeCount());

  // Free resources no longer needed
  igraph_vector_destroy(&v);
}


// Destroy graph if it exists
void destroy_graph() {
  if (existsGraph) {
    igraph_destroy(&g);
    existsGraph = 0;
  }
}


bool isConnected(){
  igraph_bool_t connected;
  igraph_is_connected(&g, &connected, IGRAPH_STRONG);
  return (bool)connected;
  destroy_graph();
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
  // Clean up
  igraph_matrix_destroy(&locs);
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
	
  // Clean up
  igraph_matrix_destroy(&locs);
  destroy_graph();
}

//Fruchterman - Reingold Layout
void layoutFruchterman(double x[], 
		       double y[], 
		       int iter, 
		       double maxDelta, 
		       double area, 
		       double coolExp, 
		       double repulserad, 
		       bool useSeed, 
		       bool isWeighted,
		       double weights[]){

  long int vcount = igraph_vcount(&g);
  long int ecount = igraph_ecount(&g);

  igraph_matrix_t locs;
  igraph_matrix_init(&locs, vcount, 2); 
  for (int i = 0; i < vcount; i++){
    MATRIX(locs, i, 0) = x[i];
    MATRIX(locs, i, 1) = y[i];
  }

  igraph_vector_t weights_vector;
  if (isWeighted) {
    igraph_vector_init(&weights_vector, ecount);
    for (int i = 0; i < ecount; i++){
      VECTOR(weights_vector)[i] = weights[i];
    } 
  }


  if (isWeighted) {
    igraph_layout_fruchterman_reingold(&g, 
				       &locs, 
				       iter, 
				       maxDelta, 
				       area, 
				       coolExp, 
				       repulserad, 
				       useSeed, 
				       &weights_vector,
				       0, 
				       0);

  } else {
    igraph_layout_fruchterman_reingold(&g, 
				       &locs, 
				       iter, 
				       maxDelta, 
				       area, 
				       coolExp, 
				       repulserad, 
				       useSeed, 
				       0, // weights 
				       0, 
				       0);
  }

  for(int i=0; i<vcount; i++){
    x[i] = MATRIX(locs, i, 0);
    y[i] = MATRIX(locs, i, 1);
  }	

  // Clean up
  igraph_matrix_destroy(&locs);
  igraph_vector_destroy(&weights_vector);	
  destroy_graph();
}

void layoutFruchtermanGrid(double x[],
			   double y[],
			   int iter,
			   double maxDelta,
			   double area,
			   double coolExp,
			   double repulserad,
			   bool useSeed,
			   bool isWeighted,
			   double weights[],
			   double cellSize) {


  long int vcount = igraph_vcount(&g);
  long int ecount = igraph_ecount(&g);

  igraph_matrix_t locs;
  igraph_matrix_init(&locs, vcount, 2); 
  for (int i = 0; i < vcount; i++){
    MATRIX(locs, i, 0) = x[i];
    MATRIX(locs, i, 1) = y[i];
  }

  igraph_vector_t weights_vector;
  if (isWeighted) {
    igraph_vector_init(&weights_vector, ecount);
    for (int i = 0; i < ecount; i++){
      VECTOR(weights_vector)[i] = weights[i];
    } 
  }

  if (isWeighted) {
    igraph_layout_grid_fruchterman_reingold(&g, 
					    &locs, 
					    iter, 
					    maxDelta, 
					    area, 
					    coolExp, 
					    repulserad,
					    cellSize,
					    useSeed, 
					    &weights_vector);

  } else {
    igraph_layout_grid_fruchterman_reingold(&g, 
					    &locs, 
					    iter, 
					    maxDelta, 
					    area, 
					    coolExp, 
					    repulserad,
					    cellSize, 
					    useSeed, 
					    0);   // weights
  }

  for(int i=0; i<vcount; i++){
    x[i] = MATRIX(locs, i, 0);
    y[i] = MATRIX(locs, i, 1);
  }	

  // Clean up
  igraph_matrix_destroy(&locs);
  igraph_vector_destroy(&weights_vector);	
  destroy_graph();
}


// lgl Layout
void layoutLGL(double x[], 
	       double y[], 
	       int maxIt, 
	       double maxDelta, 
	       double area, 
	       double coolExp, 
	       double repulserad, 
	       double cellSize) {

  long int vcount = igraph_vcount(&g);

  igraph_matrix_t locs;
  igraph_matrix_init(&locs, vcount, 2); 

  for(int i = 0; i < vcount; i++) {
    MATRIX(locs, i, 0) = x[i];
    MATRIX(locs, i, 1) = y[i];
  }
  
  igraph_layout_lgl(&g, 
		    &locs, 
		    maxIt, 
		    maxDelta, 
		    area, 
		    coolExp, 
		    repulserad, 
		    cellSize, 
		    -1); // random root
  
  for(int i = 0; i < vcount; i++) {
    x[i] = MATRIX(locs, i, 0);
    y[i] = MATRIX(locs, i, 1);
  }

  // Clean up
  igraph_matrix_destroy(&locs);
  destroy_graph();  
}


int minimum_spanning_tree_unweighted(int res[]) {

  igraph_t mst;  
  int from, to;

  // Calculate MST
  igraph_minimum_spanning_tree_unweighted(&g, &mst);

  // Copy results
  int e = igraph_ecount(&mst);

  //  printf("Number of edges in MST: %d\n", e);
  
  for(int i = 0; i < e ; i++) {
    igraph_edge(&mst, i, &from, &to);    
    res[2 * i]     = from;
    res[2 * i + 1] = to;
  }
  
  // Clean up
  igraph_destroy(&mst);
  destroy_graph();

  return e;
}

int minimum_spanning_tree_weighted(int res[], double weights[]) {
  igraph_t mst;  
  int from, to;
  long int ecount = igraph_ecount(&g);
  igraph_vector_t weights_vector;

  igraph_vector_init(&weights_vector, ecount);
  for (int i = 0; i < ecount; i++) 
    VECTOR(weights_vector)[i] = weights[i];    
  
  // Calculate MST
  igraph_minimum_spanning_tree_prim(&g, &mst, &weights_vector);

  // Copy results
  int e = igraph_ecount(&mst);

  //  printf("Number of edges in MST: %d\n", e);
  
  for(int i = 0; i < e ; i++) {
    igraph_edge(&mst, i, &from, &to);    
    res[2 * i]     = from;
    res[2 * i + 1] = to;
  }
  
  // Clean up
  igraph_destroy(&mst);
  destroy_graph();
  igraph_vector_destroy(&weights_vector);	

  return e;
}



extern "C"{
  //Simple adding of two integers
  int nativeAdd(int a, int b)
  {
    return( a + b );
  }
}


int nodeCount(){
  return (int)igraph_vcount(&g);
}
