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

// GpuGraphDrawing interface
#include "interface.cu"



////////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////////

int main(int argc, char** argv)
{
  FILE* from;
  globalScope *scope;

  // Check number of arguments
  if (argc < 2) error("Wrong no of args");

  // Create scope
  scope = globalScopeCreate();

  // Ask for parameters
  printf("Enter the size of the coarsest graph (Default 50):");          scanf("%d", &(scope->coarseGraphSize));
  printf("Enter the number of interpolation iterations (Default 50):");  scanf("%d", &(scope->interpolationIterations));
  printf("Enter the level of convergence (Default 2):");                 scanf("%d", &(scope->levelConvergence));
  printf("Enter the ideal edge length (Default 5):");                    scanf("%d", &(scope->EDGE_LEN));
  printf("Enter the initial no of force iterations(Default 300):");      scanf("%d", &(scope->initialNoIterations));
 
  // Open file 
  from=fopen(argv[1],"r");
  if(!from) error("cannot open 1st file");
  
  // Read graph grom file (argv[1])
  int len = strlen(argv[1]);
  if((argv[1][len-1]=='l') && (argv[1][len-2]=='m') && (argv[1][len-3]=='g') )
    readGml(scope, from);
  else
    readChaco(scope, from);

  printf ("Finished reading graph!\n");

  calculateLayout (scope);

  printf ("Finished calculationg layout, showing results...\n");

  // Show results in display
  showGraph (scope, argc, argv);
    
  return 0;
}
