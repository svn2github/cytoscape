#ifndef GRAPH_H
#define GRAPH_H

typedef struct 
{
		int screen_width;
		int screen_hieght;
		float EPSILON;
		int  numVertices;
		int  numEdges;
		float temperature;
		int    mMaxIterations;
		float attraction_multiplier;
		float repulsion_multiplier;
		int    currentIteration; 
		size_t edge_index;
		bool Converged;
		float2 * NodePos;
		int	   * AdjMatIndex;
		int	   * AdjMatVals;
		int	   * edgeLen;
		int	  * parent;
		int	  * sun;
		int	  coolTimes;
		int   level;
} graph;

#endif

