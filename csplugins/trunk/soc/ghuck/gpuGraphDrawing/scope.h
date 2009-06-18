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
// GPLv3 License
#include "license.h"


#ifndef __SCOPE__
#define __SCOPE__

typedef struct _globalScope {
  graph		     g;
  kdNodeInt         *rootInt;
  kdNodeFloat       *rootFloat;
  kdNodeInt         *treeIntD;  
  kdNodeFloat       *treeFloatD; 
  float2            *NodePosD; 
  float3            *NodeTemp;
  dim3	             threads,blocks; 
  int               *AdjMatIndexD;  
  int               *AdjMatValsD;  
  int               *edgeLenD;  
  float2            *DispD, *Disp; 
  graph             *gArray[150] = {0};
  int	             numLevels;
  int	             coarseGraphSize;
  int	             interpolationIterations;
  int	             levelConvergence;
  float3            *a;
  CUDPPConfiguration config;
  unsigned int       *data_out;
  unsigned int       *d_temp_addr_uint; 
  float3             *d_out;
  unsigned int       *nD;
  complexDevice      *OuterD;
} globalScope;

#ENDIF
