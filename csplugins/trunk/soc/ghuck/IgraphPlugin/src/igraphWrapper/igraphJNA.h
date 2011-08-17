/**************************************************************************************
Copyright (C) Gerardo Huck, 2011
Copyright (C) Gang Su, 2009

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

**************************************************************************************/


extern "C"
{
  // Basic functions
  void createGraph(int edgeArray[], int length, int directed);

  void destroy_graph();

  // Igraph functions
  bool isConnected();

  void simplify();

  int nodeCount();

  // Layouts
  void layoutCircle(double x[], double y[]);

  void starLayout(double x[], double y[], int centerId);

  void layoutFruchterman(double x[],
			 double y[],
			 int iter,
			 double maxDelta,
			 double area,
			 double coolExp,
			 double repulserad,
			 bool useSeed,
			 bool isWeighted,
			 double weights[]);

  void layoutFruchtermanGrid(double x[],
			     double y[],
			     int iter,
			     double maxDelta,
			     double area,
			     double coolExp,
			     double repulserad,
			     bool useSeed,
			     bool isWeighted,
			     double weights[],
			     double cellSize);

  // lgl Layout
  void layoutLGL(double x[], 
		 double y[], 
		 int maxIt, 
		 double maxDelta, 
		 double area, 
		 double coolExp, 
		 double repulserad, 
		 double cellSize);
    
  // Minimum spanning tree - unweighted
  int minimum_spanning_tree_unweighted(int res[]);

  // Minimum spanning tree - weighted
  int minimum_spanning_tree_weighted(int res[], double weights[]);
  

  //test functions
  int nativeAdd(int a, int b);

}
