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

See licence.h for more information.
**************************************************************************************/
#include "license.h"


void error(const char * p, const char * p2="")
{
	printf("%s %s\n",p,p2);
	exit(1);
}

void readGml(graph * g, FILE * from){
	
	printf("Reading nodes!!\n");
	int numNodes;
	char string[MAX_REC_LEN];
	fgets(string, MAX_REC_LEN,from ); // graph [
	fgets(string, MAX_REC_LEN,from ); //  Creator "makegml" directed 0 label ""
	fgets(string, MAX_REC_LEN,from ); 
	int i=0;
	while(string[i]!='[')
		i++;
	int startPos = i; 
	numNodes = atoi(string+startPos+4);
	while(fgets(string, MAX_REC_LEN,from )){
		int n = atoi(string+startPos+4);
		if(n!=numNodes+1)
			break;
		else  
			numNodes = n;
		//printf("!Node:%ld!\n",ftell (from));		
	}
	long int secFrom  = ftell (from);
	secFrom -= (long int)(strlen(string)+1);
	
	g->NodePos = (float2 *) malloc((numNodes)*sizeof(float2));
	g->AdjMatIndex =  (int * )  calloc((numNodes+1),sizeof(int));
	g->AdjMatIndex[0]=0;
	int numEdges = 0;
	printf("Reading edges!!\n"); 
	i = 0;
	while(string[i]!='[')
		i++;
	startPos = i;
	int e1 = atoi(string+startPos+9 );
	i=startPos+9;
	while(string[i]!= 't')
		i++;
	int e2 = atoi(string+i+ 6);
	g->AdjMatIndex[e1]++;
	g->AdjMatIndex[e2]++;
	numEdges++;
	
	while(fgets(string, MAX_REC_LEN,from )){
		if((string[0]==']') || (string[1]==']'))
			break;
		numEdges++;
		e1 = atoi(string+startPos+9 );
		i=0;
		while(string[i]!= 't')
		i++;
		int e2 = atoi(string+i+ 6);
		g->AdjMatIndex[e1]++;
		g->AdjMatIndex[e2]++;
	}
	for(int i = 0; i < numNodes; i++)
		g->AdjMatIndex[i+1] += g->AdjMatIndex[i];
			
	printf("No of Edges: %d\n",numEdges);
	g->AdjMatVals  =  (int * )  malloc(2*numEdges*sizeof(int));
	g->edgeLen     =  (int * )  malloc(2*numEdges*sizeof(int));
	int * temp =  (int * )  calloc((numNodes),sizeof(int));
	initGraph(g,numNodes); g->numEdges = 2*numEdges;
	
	fseek ( from, secFrom, SEEK_SET );
	while(fgets(string, MAX_REC_LEN,from )){
		
		if((string[0]==']') || (string[1]==']'))
			break;
		e1 = atoi(string+startPos+9 );
		i=0;
		while(string[i]!= 't')
			i++;
		int e2 = atoi(string+i+ 6);
		g->AdjMatVals[g->AdjMatIndex[e1-1]+temp[e1-1]] = e2-1;
		g->AdjMatVals[g->AdjMatIndex[e2-1]+temp[e2-1]] = e1-1;
		g->edgeLen[g->AdjMatIndex[e1-1]+temp[e1-1]] = EDGE_LEN;
		g->edgeLen[g->AdjMatIndex[e2-1]+temp[e2-1]] = EDGE_LEN;
		temp[e1-1]++;
		temp[e2-1]++;
	}
	free(temp);
	
}


void readChaco(graph * g, FILE * from){
	int numNodes,numEdges;
	printf("Reading nodes!!\n");
	if(!fscanf(from,"%d",&numNodes))
		error("Cannot read 1st file");
	if(!fscanf(from,"%d",&numEdges))
		error("Cannot read 1st file");
		
	printf("Reading edges!!\n");
	char string[MAX_REC_LEN];
	int index = 0;
	initGraph(g,numNodes); g->numEdges = 2*numEdges;
	g->NodePos = (float2 *) malloc((numNodes)*sizeof(float2));
	g->AdjMatIndex =  (int * )  malloc((numNodes+1)*sizeof(int));
	g->AdjMatVals  =  (int * )  malloc(2*numEdges*sizeof(int));
	g->edgeLen     =  (int * )  malloc(2*numEdges*sizeof(int));
	
	int nEdges = 0;
	g->AdjMatIndex[0]=0;
	while(fgets(string, MAX_REC_LEN,from ))
	{
		if((string[0]==10) || (string[0]==8) ) continue;
		g->NodePos[index].x= (int)rand()%g->screen_width;
		g->NodePos[index].y= (int)rand()%g->screen_hieght;
		if(g->NodePos[index].x < 0)
		{
		exit(0);
		}
		char * first = string;
		int sl=strlen(string);
		for(int i=0;i<sl;i++)
		{
			if(string[i]==10)
			{
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

