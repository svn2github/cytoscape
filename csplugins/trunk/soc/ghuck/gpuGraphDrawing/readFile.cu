/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009


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

See license.h for more information.
**************************************************************************************/
#include "license.h"


void error(const char * p, const char * p2="")
{
  printf("%s %s\n",p,p2);
  exit(1);
}
			    

// This function reads a graph from a file (from) stored in a quite strange GML format
//TODO: FIX free() call in readGml() function. It produces a segmentation fault
// TODO: Write a function which reads from standard GML format
			  


/******************************* FORMAT SAMPLE ***********************
graph [
 Creator "makegml" directed 0 label ""
  node [ id 1 ]
  node [ id 2 ]
  node [ id 3 ]
  node [ id 4 ]
  node [ id 5 ]
  node [ id 6 ]
edge [ source 1 target 3 ]
edge [ source 1 target 4 ]
edge [ source 2 target 2 ]
edge [ source 2 target 3 ]
edge [ source 3 target 1 ]
edge [ source 3 target 2 ]
edge [ source 4 target 5 ]
]
**************************************************************************/


void readGml(graph * g, FILE * from)
{
  int numNodes;
  char string[MAX_REC_LEN]; // used to store data readed from the file temporaly

  printf("Reading nodes!");

  // Skip first 2 lines
  fgets(string, MAX_REC_LEN,from ); // graph [
  fgets(string, MAX_REC_LEN,from ); // Creator "makegml" directed 0 label ""


  /*     READ NODES    */

  // Get the offset (starting position)
  fgets(string, MAX_REC_LEN,from ); 

  int i=0;
  while(string[i]!='[')
    i++;
  int startPos = i; 

  // Get number of nodes
  numNodes = atoi(string+startPos+4);       // get initial node number
  while( fgets(string, MAX_REC_LEN,from) ){ // get a new line
    int n = atoi(string+startPos+4);        // read node number
    if(n!=numNodes+1)                       // check whether the new node number is numNodes+1 (if not, we're done here)
      break;
    else  
      numNodes = n;
    //printf("!Node:%ld!\n",ftell (from));		
  }
  printf ("\tnumNodes = %d\n", numNodes);

  /*     FINISH READING NODES      */


  // Get the position in the file in which the edge information starts (secFrom)
  long int secFrom  = ftell (from);         
  secFrom -= (long int)(strlen(string)+1);

  // Allocate memory for NodePos
  g->NodePos = (float2*) malloc((numNodes)*sizeof(float2));

  // Allocate memory for AdjMatIndex 
  g->AdjMatIndex =  (int*) calloc((numNodes+1),sizeof(int));


  /*     READ EDGES      */

  g->AdjMatIndex[0]=0;                       // Adjacency list of first node starts in position 0
  int numEdges = 0;                          // Initialize numEdges

  printf("Reading edges!"); 

  // Get the offset (starting position) for edge numbers
  i = 0;
  while(string[i]!='[')
    i++;
  startPos = i;

  // Get the edge source node (e1)
  int e1 = atoi(string+startPos+9 );

  // Go to the position of edge target node
  i=startPos+9;
  while(string[i]!= 't')
    i++;

  // Get the edge target node (e2)
  int e2 = atoi(string+i+ 6);

  // Increase number of edges, Adj Matrix indexes of e1 and e2
  (g->AdjMatIndex[e1])++;
  (g->AdjMatIndex[e2])++;
  numEdges++;
  
  // Process the rest of the edges
  while(fgets(string, MAX_REC_LEN,from )){
    
    // Check if the file is finishing
    if((string[0]==']') || (string[1]==']'))
      break;

    // Increase number of edges
    numEdges++;

    // Get the edge source node (e1)
    e1 = atoi(string+startPos+9 );

    // Go to the position of edge target node
    i=0;
    while(string[i]!= 't')
      i++;

    // Get the edge target node (e2)
    int e2 = atoi(string+i+ 6);
    (g->AdjMatIndex[e1])++;
    (g->AdjMatIndex[e2])++;
  }
  
  // Update AdjMatIndex so that each position points to the appropiate element in AdjMatVals
  for(int i = 0; i < numNodes; i++)
    g->AdjMatIndex[i+1] += g->AdjMatIndex[i];
  
  printf("\tNumber of Edges = %d\n",numEdges);

  // Allocate memory for AdjMatVals, edgeLen
  g->AdjMatVals  = (int*) malloc(2*numEdges*sizeof(int));
  g->edgeLen     = (int*) malloc(2*numEdges*sizeof(int));

  // Allocate memory for temp, an auxiliary array, initialize it whith zeros
  int *temp      = (int*) calloc(numNodes,sizeof(int));

  // Initialize Graph
  initGraph(g,numNodes); 

  // Save numEdges
  g->numEdges = 2*numEdges;

  // Go to secFrom position in file "from" (where the edge information starts)
  fseek ( from, secFrom, SEEK_SET );
  while( fgets(string, MAX_REC_LEN,from )){
    
    // Check if the file is finishing
    if( (string[0]==']') || (string[1]==']') )
      break;

    // Get the edge source node (e1)
    e1 = atoi(string+startPos+9 );

    // Go to the position of edge target node
    i=0;
    while(string[i]!= 't')
      i++;

    // Get the edge target node (e2)
    int e2 = atoi(string+i+ 6);

    // Add e1 to adjacency list of e2 and vice versa.
    g->AdjMatVals[g->AdjMatIndex[e1-1]+temp[e1-1]] = e2-1;
    g->AdjMatVals[g->AdjMatIndex[e2-1]+temp[e2-1]] = e1-1;

    // Save edge lenght for this edge
    g->edgeLen[g->AdjMatIndex[e1-1]+temp[e1-1]] = EDGE_LEN;
    g->edgeLen[g->AdjMatIndex[e2-1]+temp[e2-1]] = EDGE_LEN;

    // Increase the number of neighbors already processed of e1 and e2 
    (temp[e1 - 1])++;
    (temp[e2 - 1])++;

  }
 
  // FIX IT! The following free() call gives segmentation fault
  //  free ((void*) temp);
}





/******************************* FORMAT SAMPLE ***********************
7 10
2 3
1 3 7
1 2 6 7
6
6 7
3 4 5 7
2 3 5 6
**********************************************************************/
//In more detail, there are 7 nodes and 10 edges in the graph; node 1 is adjacent to 2,3; node 2 is adjacent to 1,3,7; etc


void readChaco(graph * g, FILE * from)
{
  int numNodes,numEdges;
  char string[MAX_REC_LEN];        // Temporary string in which each line of the file will be temporary stored
  int index = 0;
  int nEdges = 0;
  
  if(!fscanf(from,"%d",&numNodes))
    error("Cannot read 1st file");
  if(!fscanf(from,"%d",&numEdges))
    error("Cannot read 1st file");

  printf ("Number of nodes: %d\n", numNodes);
  printf ("Number of edges: %d\n", numEdges);
  printf ("Reading nodes!!\n");		
  printf ("Reading edges!!\n");

  // Initialize Graph
  initGraph(g,numNodes); 

  // Save numEdges
  g->numEdges = 2*numEdges;

  // Allocate memory for NodePos, AdjMatIndex, AdjMatVals, edgeLen
  g->NodePos     = (float2*) malloc((numNodes)*sizeof(float2));
  g->AdjMatIndex =    (int*) malloc((numNodes+1)*sizeof(int));
  g->AdjMatVals  =    (int*) malloc(2*numEdges*sizeof(int));
  g->edgeLen     =    (int*) malloc(2*numEdges*sizeof(int));
	
  // First node's adjacency list starts at position 0
  g->AdjMatIndex[0]=0;
  while(fgets(string, MAX_REC_LEN,from )){
    
    if((string[0]==10) || (string[0]==8) ) 
      continue;
    
    g->NodePos[index].x= (int)rand()%g->screen_width;
    g->NodePos[index].y= (int)rand()%g->screen_hieght;
    
    if(g->NodePos[index].x < 0){
      exit(0);
    }
    
    char * first = string;
    int sl=strlen(string);
    
    for(int i=0;i<sl;i++){
    
      if(string[i]==10){
	string[i]='\0';
	int n = atoi(first);
	first = &string[i]; 
	g->AdjMatVals[nEdges] = n - 1;
	g->edgeLen[nEdges] = EDGE_LEN;
	nEdges++;
	break;
      }
      
      if(i==0 && string[0]==' '){
	first=string+1;
	continue;
      }
      if(string[i]!=' ') continue;
      
      while(string[i]==' ') 
	i++;
      
      string[i-1]='\0';
      
      if(strlen(first)){
	int n = atoi(first);
	first = &string[i]; 
	g->AdjMatVals[nEdges] = n - 1;
	g->edgeLen[nEdges] = EDGE_LEN;
	nEdges++;
      }
    }
    
    g->AdjMatIndex[index+1] = nEdges;
    index++;
    
  }	
}

