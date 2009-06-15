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
along with this program.  If not, see <http://wwwº.gnu.org/licenses/>.

See license.h for more information.
**************************************************************************************/
#include "license.h"



#include "graph.h"
#include <stdlib.h>
#include <cmath>
#include "common.h"

void initGraph(graph* g, int n){
	g->EPSILON = (float)0.000001;
	g->mMaxIterations = 750;
	g->numVertices=n;
	g->screen_width=SCREEN_W;
	g->screen_hieght=SCREEN_H;
	g->currentIteration = 0;
    g->temperature = (float)g->screen_width ;
	g->edge_index=0;
	g->Converged = false;
	float forceConstant = sqrt((float)g->screen_hieght * (float)g->screen_width  /(float) g->numVertices);
	g->attraction_multiplier = 0.75 * forceConstant;
	g->repulsion_multiplier = 0.75 * forceConstant;
	g->coolTimes = 0;
	g->level = 0;
}

bool Converging(graph * g, float * Disp)
{
	for(int i=0;i<g->numVertices;i++)
	{
		if(abs(Disp[2*i])> 0.0 && abs(Disp[2*i+1])>0.0)
			return false;
	}
	return true;

}

bool incrementsAreDone(graph * g) {
		if(g->currentIteration>=g->mMaxIterations) return true;
        return false;
}

void cool(graph * g) {
		g->currentIteration++;
        g->temperature *= ((float) 1.0 - g->currentIteration / (float) (g->mMaxIterations));
        
        //Todo: Remove this
        if(g->level > 20)
        {
			if(g->currentIteration == 1)
			g->currentIteration=g->mMaxIterations;	
        }
        else{
        if(g->currentIteration == initialNoIterations/(3*g->level+1))
			g->currentIteration=g->mMaxIterations;	
		}
}	

void calcPositions(int i,float2 * NodePos, float2 * Disp, graph * g) {

		float xdisp=Disp[i].x;
		float ydisp=Disp[i].y;
		float deltaLength = max(g->EPSILON, sqrt((xdisp*xdisp)+(ydisp*ydisp)));

        float newXDisp = xdisp/ deltaLength ;//* min(deltaLength, g->temperature);
		
        float newYDisp = ydisp / deltaLength ;//* min(deltaLength, g->temperature);
        NodePos[i].x+=newXDisp;
        NodePos[i].y+=newYDisp;
        float borderWidth = g->screen_width / (float)50.0;
        float newXPos = NodePos[i].x;
        if (newXPos < borderWidth) {
            newXPos = borderWidth + (rand()/(float(RAND_MAX)+1)) * borderWidth * (float)2.0;
        } else if (newXPos > (g->screen_width - borderWidth)) {
            newXPos =  g->screen_width- borderWidth - (rand()/(float(RAND_MAX)+1))* borderWidth * (float)2.0;
        }
        
        float newYPos = NodePos[i].y;
        if (newYPos < borderWidth) {
            newYPos = borderWidth + (rand()/(float(RAND_MAX)+1)) * borderWidth * (float)2.0;
        } else if (newYPos > (g->screen_hieght - borderWidth)) {
            newYPos = g->screen_hieght - borderWidth - (rand()/(float(RAND_MAX)+1)) * borderWidth * (float)2.0;
        }
		
		NodePos[i].x=newXPos;
        NodePos[i].y=newYPos;
		
}
