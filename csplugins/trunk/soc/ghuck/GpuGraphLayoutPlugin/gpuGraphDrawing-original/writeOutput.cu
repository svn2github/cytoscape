void writeOutput(graph * g){
	
	float zero = 0;
	float one = 1;
	FILE * oEdges =fopen("data\\4970_edge_6455.oedge","w");
	if(!oEdges) error("cannot open edge file");
	
	for(int i = 0; i < g->numVertices; i++)
		for(int j = g->AdjMatIndex[i]; j < g->AdjMatIndex[i+1]; j++)
			{
			int e2 = g->AdjMatVals[j];
			if(i < e2)
				fprintf(oEdges, "%d %d \r",i,e2);
			}
	fclose(oEdges);
	
	FILE * oVertices =fopen("data\\4970_edge_6455.ocoordinate","w");
	if(!oVertices) error("cannot open coordinate file");
	
	for(int i = 0; i < g->numVertices; i++)
		fprintf(oVertices, "%f %f %f\r",g->NodePos[i].x,g->NodePos[i].y,zero);
		
	fclose(oVertices);
	
	FILE * oGraph =fopen("data\\4970_edge_6455.graph","w");
	if(!oGraph) error("cannot open main.graph file");
	
	fprintf(oGraph, "./data/4970_edge_6455.ocoordinate\n");
	fprintf(oGraph, "./data/4970_edge_6455.oedge\n");
	fprintf(oGraph, "./data/4970_edge_6455.oweight\n");
	
	fclose(oGraph);
	
	FILE * oWeight =fopen("data\\4970_edge_6455.oweight","w");
	if(!oWeight) error("cannot open weight file");
	
	for(int i = 0; i < g->numVertices; i++)
		fprintf(oWeight, "%f\n",one);
		
	fclose(oWeight);
	
}

