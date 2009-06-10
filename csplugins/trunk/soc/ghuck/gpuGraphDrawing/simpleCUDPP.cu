#include "licence.h"
#include "copyright.h"

// includes, system
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <cmath>
#include <ctime>

// includes, project
#include "cutil.h"
#include "GL/glut.h"
#include "cudpp.h"


// includes, kernels
#include <kernel.cu>

// Include other source files
#include "grap.cu"
#include "kdNode.cu"
#include "pkdNode.cu"
#include "common.h"
#include "readFile.cu"
#include "writeOutput.cu"


graph		g;
kdNodeInt *	rootInt;
kdNodeFloat *	rootFloat;
kdNodeInt *	treeIntD;  
kdNodeFloat *	treeFloatD; 
float2 *	NodePosD; 
float3 *	NodeTemp;
dim3		threads,blocks; 
int *		AdjMatIndexD;  
int *		AdjMatValsD;  
int *		edgeLenD;  
float2 *	DispD, * Disp; 
graph *		gArray[150] = {0};
int		numLevels;
int		coarseGraphSize;
int		interpolationIterations;
int		levelConvergence;
float3 *	a;
//CUDPPScanConfig config;                          Deprecated
CUDPPConfiguration config; // This struct replaces the old CUDPPScanConfig in the release 1.0a of CUDPP
unsigned int *	data_out;
unsigned int *	d_temp_addr_uint; 
float3 *	d_out;
unsigned int *	nD;
complexDevice * OuterD;
     

// This function calculates one step of the force-driven layout process, updating the nodes position
void advancePositions(graph * g)
{
  cudaMemcpyToSymbol(gd, g, sizeof(graph));

  // check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");	
  
  for(int i = 0; i < g->numVertices; i++){
    NodeTemp[i].x=g->NodePos[i].x;
    NodeTemp[i].y=g->NodePos[i].y;
    NodeTemp[i].z = i;
  }
  
  cudaMemcpy(a, NodeTemp, g->numVertices*sizeof(float3), cudaMemcpyHostToDevice);
  
  //config.maxNumElements = g->numVertices;        Deprecated
  //cudppInitializeScan(&config);                  Deprecated
      
  // Configure CUDPP Scan Plan
  CUDPPHandle planHandle;
  cudppPlan (&planHandle, config, g->numVertices, 1, 0); // rows = 1, rowPitch = 0
  
  int sizeInt = g->numVertices*sizeof(kdNodeInt);
  int sizeFloat = g->numVertices*sizeof(kdNodeFloat);
  
  // Check if the KDTREE has to be rebuilded
  if((g->currentIteration < 4) ||(g->currentIteration%20==0) ){

    // Decide whether the KDTREE is goint to be builded in the CPU or in the GPU
    if (g->numVertices < 50000){ //CPU
      kdNodeInit(rootInt,rootFloat,1,0,0,SCREEN_W,0,SCREEN_H);
      construct(NodeTemp, NodeTemp+g->numVertices-1, rootInt,rootFloat, 1,0,0,SCREEN_W,0,SCREEN_H,3);
    }
    else{ //GPU   
      kdNodeInitD(rootInt,rootFloat,1,0,0,SCREEN_W,0,SCREEN_H);
      constructD(a, a+g->numVertices-1, rootInt,rootFloat, 1,0,0,SCREEN_W,0,SCREEN_H,3,data_out,d_temp_addr_uint, d_out,planHandle,nD,OuterD );
    }
  }
  	
  cudaMemcpy(NodePosD, g->NodePos, g->numVertices*sizeof(float2), cudaMemcpyHostToDevice);
  cudaMemcpy(treeIntD, rootInt, sizeInt, cudaMemcpyHostToDevice);
  cudaMemcpy(treeFloatD, rootFloat, sizeFloat, cudaMemcpyHostToDevice);
  cudaBindTexture(0,texNodePosD, NodePosD,sizeof(float2)*g->numVertices);
  cudaBindTexture(0,texInt, treeIntD,sizeInt);
  cudaBindTexture(0,texFloat, treeFloatD,sizeFloat);
  
  cudaMemcpy(AdjMatIndexD, g->AdjMatIndex, (g->numVertices+1)*sizeof(int), cudaMemcpyHostToDevice);
  cudaMemcpy(AdjMatValsD,  g->AdjMatVals,  (g->numEdges)*sizeof(int), cudaMemcpyHostToDevice);
  cudaMemcpy(edgeLenD,     g->edgeLen,     (g->numEdges)*sizeof(int), cudaMemcpyHostToDevice);

  // Check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");
  
  cudaBindTexture(0,texAdjMatValsD, AdjMatValsD,(g->numEdges)*sizeof(int));
  cudaBindTexture(0,texEdgeLenD,edgeLenD,  (g->numEdges)*sizeof(int));

  // Check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");
    
  // Execute the kernel, calculate forces
  calculateForces<<< blocks, threads >>>(g->numVertices, DispD,AdjMatIndexD);
  
  // Check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");

  cudaMemcpy(Disp, DispD, g->numVertices*sizeof(float2), cudaMemcpyDeviceToHost);
	
  // Calculate new positions of nodes, based on the force calculations
  for(int i = 0; i < g->numVertices; i++)
    calcPositions(i,g->NodePos, Disp,g); 

  // Decrease the temperature of graph g
  cool(g); 

  // Destroy CUDPP Scan Plan
  cudppDestroyPlan(planHandle);
}


// This function coarses a graph, by obtaining a maximal independant subset of it
graph* coarsen(graph * g)
{
  graph *	rg = (graph*) malloc(sizeof(graph));
  bool *	used   = (bool*) calloc(g->numVertices,sizeof(bool));
  int *		newNodesNos = (int*) calloc(g->numVertices+1,sizeof(int));
  int		current = 0;
  int		left = g->numVertices;
  int		numParents = 0;
  rg->parent = (int *)  calloc(g->numVertices,sizeof(int));
  
  while(left>0){
    left--;
    newNodesNos[numParents] = current;
    rg->parent[current] = numParents;
    used[current] = 1;
    
    for(int x = g->AdjMatIndex[current]; x < g->AdjMatIndex[current+1]; x++){
      int j = g->AdjMatVals[x];
      if(!used[j])
	left --;
      used[j] = 1;
      rg->parent[j] = numParents;
    }
    numParents++;

    // If there is any node left, search for an unused one
    if(left>0)
      while((used[current]))
	current++;
  }
  
  free(used);
  
  initGraph(rg,numParents);
  int numEdges = 0;
  rg->NodePos = (float2 *) malloc((numParents)*sizeof(float2));
  rg->AdjMatIndex =  (int * )  calloc(numParents+1,sizeof(int));
  rg->AdjMatVals  =  (int * )  calloc(g->numEdges,sizeof(int));
  rg->edgeLen     =  (int * )  calloc(g->numEdges,sizeof(int));
  
  for(int i = 0; i < numParents; i++){
    rg->NodePos[i].x = rand()%SCREEN_W;
    rg->NodePos[i].y = rand()%SCREEN_H;
  }
  
  for ( int i = 0; i < numParents; i++){
    int * usedChild = (int *) calloc(numParents,sizeof(int));
    int node = newNodesNos[i];
    for(int x = g->AdjMatIndex[node]; x < g->AdjMatIndex[node+1]; x++){
      int j = g->AdjMatVals[x];
      if (rg->parent[j] != i)
	usedChild[rg->parent[j]] = 1;
      else{
	for(int y = g->AdjMatIndex[j]; y < g->AdjMatIndex[j+1]; y++){
	  int neighbor = g->AdjMatVals[y];
	  usedChild[rg->parent[neighbor]] = 1;
	}
      }
    }
    
    for ( int k = 0; k < numParents; k++){
      if (usedChild[k]){
	rg->AdjMatVals[numEdges] = k;
	rg->edgeLen[numEdges] = EDGE_LEN;
	numEdges++;
      }
    }
      
    rg->AdjMatIndex[i+1] = numEdges;
    free(usedChild);
  }  
  
  rg->numEdges = numEdges;
  return rg;
}


// This function just applies a one step advance to a graph position
void exactLayoutOnce(graph * g){
  advancePositions(g);
}

// This funcion initializes a graph position, using the position of nodes in the coarsed graph (if it exists) as a guide
void nextLevelInitialization(graph g, graph * coarseGraph){
  
  // Nodes that exists in coarseGraph remain in the same position
  for(int i = 0; i < g.numVertices; i++){
    g.NodePos[i].x = coarseGraph->NodePos[coarseGraph->parent[i]].x ;
    g.NodePos[i].y = coarseGraph->NodePos[coarseGraph->parent[i]].y ;
  }
  
  //
  for(int j = 0; j <interpolationIterations; j++){
    for(int i = 0; i < g.numVertices; i++){
      int degree = g.AdjMatIndex[i+1] - g.AdjMatIndex[i];
      float2 pi; pi.x=0;pi.y=0;
      for(int k = g.AdjMatIndex[i]; k < g.AdjMatIndex[i+1]; k++){	
	int j = g.AdjMatVals[k];
	pi.x+=g.NodePos[j].x;
	pi.y+=g.NodePos[j].y;
      }
      if(degree){
	g.NodePos[i].x = 0.5 * ( g.NodePos[i].x+ (1.0/degree)*pi.x);
	g.NodePos[i].y = 0.5 * ( g.NodePos[i].y+ (1.0/degree)*pi.y);
      }
    }
  }
  
  
  free(coarseGraph->NodePos);
  free(coarseGraph->parent);
  free(coarseGraph->AdjMatIndex);
  free(coarseGraph->AdjMatVals);
  free(coarseGraph->edgeLen);
  free(coarseGraph);
}

// This function creates the MIS (Maximal Independent Set) Filtration of a graph
void createCoarseGraphs(graph * g,int level)
{
  gArray[level] = g;
  if(g->numVertices <= coarseGraphSize)
    return;
  
  graph * coarseGraph = coarsen(g);
  
  if(g->numVertices < 1.07 * coarseGraph->numVertices )
    return;
  
  if(g->numVertices - coarseGraph->numVertices > 0 )
    createCoarseGraphs(coarseGraph,level+1);
}

// Show results in screen 
void display(void)
{	
  glLoadIdentity();
  glClearColor(1.0f, 1.0f, 1.0f, 1.0f);	
  int l = 0;
  
  glClear(GL_COLOR_BUFFER_BIT);
  glLoadIdentity();
  glBegin(GL_LINES);
  glColor3f(0.2,0.2,0.2);
  for(int i = 0; i < gArray[l]->numVertices; i++)
    for(int j = gArray[l]->AdjMatIndex[i]; j < gArray[l]->AdjMatIndex[i+1]; j++){
      int k = gArray[l]->AdjMatVals[j];
      glVertex3f(gArray[l]->NodePos[i].x,gArray[l]->NodePos[i].y,00);
      glVertex3f(gArray[l]->NodePos[k].x,gArray[l]->NodePos[k].y,00);
    }
  glEnd();
  glColor3f(1,0,0);
  glPointSize(1.1);
  glBegin(GL_POINTS);
  for(int i = 0; i < gArray[l]->numVertices; i++)
    glVertex3f(gArray[l]->NodePos[i].x,gArray[l]->NodePos[i].y,00);
  glEnd();
  
  glFlush();  /* OpenGL is pipelined, and sometimes waits for a full buffer to execute */
  glutSwapBuffers();
}

// Reshape screen
void reshape(int w,int h)
{
  glViewport(0,0,w,h);
}

////////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////////
int
main(int argc, char** argv)
{
  // Initialize device, using macro defined in "cutil.h"
  CUT_DEVICE_INIT();
  
  FILE* from;
  graph g;

  // Check number of arguments
  if (argc < 2) error("Wrong no of args");

  // Ask for parameters
  printf("Enter the size of the coarsest graph (Default 50):"); scanf("%d",&coarseGraphSize);
  printf("Enter the number of interpolation iterations (Default 50):"); scanf("%d", &interpolationIterations);
  printf("Enter the level of convergence (Default 2):"); scanf("%d",&levelConvergence);
  printf("Enter the ideal edge length (Default 5):"); scanf("%d",&EDGE_LEN);
  printf("Enter the initial no of force iterations(Default 300):"); scanf("%d",&initialNoIterations);
 
  // Open file 
  from=fopen(argv[1],"r");
  if(!from) error("cannot open 1st file");
  
  //Read graph grom file (argv[1])
  int len = strlen(argv[1]);
  if((argv[1][len-1]=='l') && (argv[1][len-2]=='m') && (argv[1][len-3]=='g') )
    readGml(&g, from);
  else
    readChaco(&g, from);
  
  /*    Initializations    */

  // Number of Nodes
  int  numNodes = g.numVertices;

  // Amount of memory to be used by integers
  int sizeInt = numNodes*sizeof(kdNodeInt);

  // Amount of memory to be used by integers
  int sizeFloat = numNodes*sizeof(kdNodeFloat);
  
  rootInt   = (kdNodeInt *) calloc(numNodes,sizeof(kdNodeInt));
  rootFloat = (kdNodeFloat *) calloc(numNodes,sizeof(kdNodeFloat));
  cudaMalloc((void**)&treeIntD,sizeInt);
  cudaMalloc((void**)&treeFloatD,sizeFloat);
  cudaMalloc((void**)&NodePosD, numNodes*sizeof(float2));
  
  // Check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");
  
  // check if kernel execution generated and error
  NodeTemp = (float3 *)malloc(numNodes*sizeof(float3));
  cudaMalloc((void**)&a, numNodes*sizeof(float3));
  
  Disp = (float2 *) malloc((numNodes)*sizeof(float2));
  cudaMalloc((void**)&DispD, numNodes*sizeof(float2));
  
  cudaMalloc((void**)&AdjMatIndexD, (g.numVertices+1)*sizeof(int));
  cudaMalloc((void**)&AdjMatValsD, (g.numEdges)*sizeof(int));
  cudaMalloc((void**)&edgeLenD, (g.numEdges)*sizeof(int));
  
  // Initialize parameters for config (see CUDPP in cudpp.h)

  config.algorithm = CUDPP_SCAN;
  config.op        = CUDPP_ADD;
  config.datatype  = CUDPP_INT;
  config.options   = CUDPP_OPTION_FORWARD | CUDPP_OPTION_EXCLUSIVE; 

  //config.direction      = CUDPP_SCAN_FORWARD;                  Deprecated
  //config.exclusivity    = CUDPP_SCAN_EXCLUSIVE;                Deprecated
  //config.op	          = CUDPP_ADD;                           Deprecated
  //config.datatype       = CUDPP_INT;                           Deprecated
  //config.maxNumRows	  = 1;                                   Deprecated
  //config.rowPitch       = 0;                                   Deprecated


  
  cudaMalloc((void**)&data_out,sizeof(unsigned int)* g.numVertices);
  cudaMalloc((void**)&d_temp_addr_uint,sizeof(unsigned int)* g.numVertices);
  cudaMalloc((void**)&d_out,sizeof(float3)* g.numVertices);
  cudaMalloc((void**)&nD,sizeof(unsigned int));
  /* End Initializations */
  
  
  printf("Coarsening graph...\n");
  
  clock_t start, end_coarsen,end_layout;
  double elapsed_layout,elapsed_coarsen;
  start = clock();
  
  
  gArray[0] = &g;
  createCoarseGraphs(&g,0);
  numLevels=0;
  while(gArray[numLevels]!=NULL)
    numLevels++;
  gArray[numLevels-1]->level = 0;
  
  end_coarsen = clock();
  elapsed_coarsen = ((double) (end_coarsen - start)) / CLOCKS_PER_SEC;
  start = clock();
  printf("Computing layout...\n");
  
  for(int i = 0; i < numLevels; i++){
    
    // setup execution parameters
    
    unsigned m_chunks = gArray[numLevels-i-1]->numVertices / maxThreadsThisBlock;
    unsigned m_leftovers = gArray[numLevels-i-1] ->numVertices % maxThreadsThisBlock;
    
    if ((m_chunks == 0) && (m_leftovers > 0)){
      // can't even fill a block
      blocks = dim3(1, 1, 1); 
      threads = dim3((m_leftovers), 1, 1);
    } 
    else {
      // normal case
      if (m_leftovers > 0){
	// not aligned, add an additional block for leftovers
	blocks = dim3(m_chunks + 1, 1, 1);
      }
      else{
	// aligned on block boundary
	blocks = dim3(m_chunks, 1, 1);
      }
      threads = dim3(maxThreadsThisBlock , 1, 1);
    }
    
    if(i < numLevels-levelConvergence)
      while(!incrementsAreDone(gArray[numLevels-i-1]))
	exactLayoutOnce(gArray[numLevels-i-1]);
    if(numLevels-i-2 >= 0)
      nextLevelInitialization(*gArray[numLevels-i-2], gArray[numLevels-i-1]);
  }
  
  end_layout = clock();
  elapsed_layout = ((double) (end_layout - start)) / CLOCKS_PER_SEC;
  
  printf("Time for coarsening graph: %f\n",elapsed_coarsen);
  printf("Time for calculating layout: %f\n",elapsed_layout);
  
  cudaFree(AdjMatIndexD);
  cudaFree(edgeLenD);
  cudaFree(AdjMatValsD);
  cudaFree(NodePosD);
  cudaFree(DispD);
  cudaFree(treeIntD);
  cudaFree(treeFloatD);
  cudaFree(data_out);
  cudaFree(d_temp_addr_uint);
  cudaFree(d_out);
  cudaFree(nD);
  free(NodeTemp);
  free(rootInt);
  free(rootFloat);
  free(Disp);
  
  writeOutput(&g);
  
  
  glutInit(&argc, argv);		/* setup GLUT */
  glutInitDisplayMode(GLUT_RGB); 
  glutInitWindowSize(SCREEN_W,SCREEN_H);
  glutInitWindowPosition(100,100);
  glutCreateWindow(argv[0]);	/* open a window */
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluOrtho2D(0,SCREEN_W,0,SCREEN_H);
  glMatrixMode(GL_MODELVIEW);
  glutReshapeFunc(reshape);
  glutDisplayFunc(display);		/* tell GLUT how to fill window */
  glutMainLoop();				/* let glut manage i/o processing */
  
  return EXIT_SUCCESS;
}
