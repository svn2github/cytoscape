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

// Header with JNI's function declaration
#include "../GpuLayout.h" 

// GpuGraphDrawing interface
#include "interface.cu"

//#include <unistd.h>



////////////////////////////////////////////////////////////////////////////////////////
// Implementation of JNI call, it initializes the arguments and calls compute_layout()
////////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jobjectArray JNICALL Java_GpuLayout_ComputeGpuLayout (JNIEnv*    env, 
								jobject    thisJ, 
								jintArray  AdjMatIndexJ, 
								jintArray  AdjMatValsJ, 
								jint       coarseGraphSizeJ, 
								jint       interpolationIterationsJ, 
								jint       levelConvergenceJ, 
								jint       EDGE_LENJ, 
								jint       initialNoIterationsJ, 
								jdouble    hSizeJ, 
								jdouble    vSizeJ
								)
{

  // Scope which will be used for this computation
  globalScope *scope;

  // Create scope
  scope = globalScopeCreate();

  // Set parameters
  scope->coarseGraphSize          = coarseGraphSizeJ;
  scope->interpolationIterations  = interpolationIterationsJ;
  scope->levelConvergence         = levelConvergenceJ;
  scope->EDGE_LEN                 = EDGE_LENJ;
  scope->initialNoIterations      = initialNoIterationsJ;

  ////////////
  // Set graph
  ////////////
  int numNodes,numEdges;

  // Get numNodes, numEdges
  numNodes = env->GetArrayLength(AdjMatIndexJ) - 1; //AdjMatIndexJ has an extra index for marking the end of AdjMatValsJ
  numEdges = env->GetArrayLength(AdjMatValsJ);

  // Initialize Graph
  initGraph(&(scope->g), numNodes); 

  // Save numEdges
  scope->g.numEdges = numEdges;

  // Allocate memory for NodePos, AdjMatIndex, AdjMatVals, edgeLen
  scope->g.NodePos     = (float2*) malloc (numNodes       * sizeof(float2) );
  scope->g.AdjMatIndex =    (int*) malloc ((numNodes + 1) * sizeof(int)    );
  scope->g.AdjMatVals  =    (int*) malloc (numEdges       * sizeof(int)    );
  scope->g.edgeLen     =    (int*) malloc (numEdges       * sizeof(int)    );

  // Get temporary copies of AdjMatIndex, AdjMatVals
  int *temp_AdjMatIndex = env->GetIntArrayElements(AdjMatIndexJ, NULL);
  int *temp_AdjMatVals  = env->GetIntArrayElements(AdjMatValsJ , NULL);
	
  // Copy temporary copies
  memcpy (scope->g.AdjMatIndex, temp_AdjMatIndex, (numNodes + 1) * sizeof(int));
  memcpy (scope->g.AdjMatVals,  temp_AdjMatVals,  (numEdges)     * sizeof(int));
			
  // Free graph in JVM
  env->ReleaseIntArrayElements(AdjMatIndexJ, temp_AdjMatIndex, 0);
  env->ReleaseIntArrayElements(AdjMatValsJ,  temp_AdjMatVals , 0);

  // Initialize node positions 
  for (int i = 0; i < numNodes; i++){
      scope->g.NodePos[i].x = (int)rand() % scope->g.screen_width;
      scope->g.NodePos[i].y = (int)rand() % scope->g.screen_hieght;
  } 
	 
  // Initialize edge lengths
  for (int i = 0; i < scope->g.AdjMatIndex[numNodes]; i++){
      scope->g.edgeLen[i] = scope->EDGE_LEN;
  }			

		
  // Calculate layout
  calculateLayout (scope);
	      
  // Show results in display
  /*  int argc = 1;
  char **argv;
  char aux[] = "";
  argv[0] = aux;
  showGraph (scope, argc, argv);
  free (argv);
  */

  // Create return object
  jobjectArray result;
  
  // Get the class of float[]
  jclass floatArrCls = env->FindClass("[F");
  if (floatArrCls == NULL) {
    return NULL;
  }

  // Result is an object of type float[][]
  result = env->NewObjectArray(numNodes, floatArrCls, NULL);
  if (result == NULL) {
         return NULL; 
  }
  
  // Allocate memory for each float[] (each one contains the coordinates of a single node) and copy node's position
  for (int i = 0; i < numNodes; i++) {

    // Temporary storage for positions
    float tmp[2];

    // Create a float[]
    jfloatArray temp_float_arr = env->NewFloatArray(2);
    if (temp_float_arr == NULL) {
      return NULL; 
    }

    // Save X and Y positions
    tmp[0] = scope->g.NodePos[i].x * SCREEN_W / hSizeJ;
    tmp[1] = scope->g.NodePos[i].y * SCREEN_H / vSizeJ;

    env->SetFloatArrayRegion(temp_float_arr, 0, 2, tmp);
    env->SetObjectArrayElement(result, i, temp_float_arr);
    env->DeleteLocalRef(temp_float_arr);
  }
	 



  // Release resources
  free (scope->g.NodePos);
  free (scope->g.AdjMatIndex);
  free (scope->g.AdjMatVals);
  free (scope->g.edgeLen);



  return result;
}
  
