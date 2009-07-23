/**************************************************************************************
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

See "license.h" for more information.
**************************************************************************************/
#include "license.h"

/*
This program converts graphs which are stored in the format described in 
http://staffweb.cms.gre.ac.uk/~c.walshaw/jostle/jostle-exe.pdf (also used in programs JOSTLE, CHACO & METIS) to simple GML format
(as described in http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html).
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fstream>
#include <iostream>

#define MAX_LENGTH 1000

using namespace std;

int main (int argc, char** argv)
{

  // Get arguments
  if (argc < 3){
    printf ("Error, this program should be called in the following way:\n\"chaco2gml <inputFile> <outputFile>\"\n");
    return 0;
  }
  char *inputFile  = argv[1];
  char *outputFile = argv[2];

  cout << "Reading file: " << inputFile << endl;

  // Open files
  fstream fin  (inputFile,  fstream::in );
  fstream fout (outputFile, fstream::out);

  // Read number of nodes and edges
  int numNodes, numEdges;
  fin >> numNodes >> numEdges;

  cout << "Graph has " << numNodes << " nodes and " << numEdges << " edges\n";

  // Write heading of output file
  fout << "graph [\n";
  fout << "\tcomment \"Converted with chaco2gml from file " << inputFile << "\"\n";
  fout <<  "\tdirected 0\n";

  // Write nodes
  for (int i = 0; i < numNodes; i++){
    fout << "\tnode [\n";
    fout << "\t\tid " << i << endl;
    fout << "\t\tlabel\n";
    fout << "\t\t\"Node " << i << "\"]\n";
  }

  // Go to start of second line
  fin.ignore(256, '\n');

  // Temporary storage for line's content
  string str;
  char *p, *cstr;

  // Write edges
  for (int i = 0; i < numNodes; i++){
    
    // Get neighbors of node ith
    getline(fin, str, '\n');

    cstr = new char [str.size() + 1];
    strcpy (cstr, str.c_str());

    p=strtok (cstr," ");
    while (p!=NULL){

      int destinationNode = atoi(p) - 1;

      // Write edge
      fout << "\tedge [\n";
      fout << "\t\tsource "      << i               << endl;
      fout << "\t\tdestination " << destinationNode << endl;
      fout << "\t\t\"Edge from node " << i << " to node " << destinationNode << "\"\n";
      fout << "\t\t]\n";      

      p=strtok(NULL," ");
    }
  }

  fout << "]\n";

  fin.close();
  fout.close();

  return 0;
}
