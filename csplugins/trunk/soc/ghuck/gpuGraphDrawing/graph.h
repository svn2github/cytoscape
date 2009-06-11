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


#include "licence.h"



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

