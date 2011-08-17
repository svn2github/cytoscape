/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


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

package cytoscape.plugins.igraph;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.Structure;


/**
 *
 */
public class IgraphInterface {

    static{
	//load the dynamic library
	Native.register("igraphWrapper"); 
    }
    
    public static native int nativeAdd(int a, int b);
   
    // Create an igraph's graph
    public static native void createGraph(int edgeArray[], int length, int directed);
    
    // Test whether the graph is connected
    public static native boolean isConnected();
    
    // Simplify the graph
    public static native void simplify();

    // Circle layout
    public static native void layoutCircle(double x[], double y[]);

    // Star Layout
    public static native void starLayout(double x[], double y[], int centerId);

    // Fruchterman - Reingold Layout
    public static native void layoutFruchterman(double x[], 
						double y[], 
						int iter, 
						double maxDelta, 
						double area, 
						double coolExp, 
						double repulserad, 
						boolean useSeed,
						boolean isWeighted,
						double weights[]);
    // Fruchterman - Reingold Grid Layout
    public static native void layoutFruchtermanGrid(double x[], 
						    double y[], 
						    int iter, 
						    double maxDelta, 
						    double area, 
						    double coolExp, 
						    double repulserad, 
						    boolean useSeed,
						    boolean isWeighted,
						    double weights[], 
						    double cellSize);
    
    // lgl Layout
    public static native void layoutLGL(double x[], 
					double y[], 
					int maxIt, 
					double maxDelta, 
					double area, 
					double coolExp, 
					double repulserad, 
					double cellSize);
    
    // Minimum spanning tree - unweighted
    public static native int minimum_spanning_tree_unweighted(int res[]);

    // Minimum spanning tree - weighted
    public static native int minimum_spanning_tree_weighted(int res[], double weights[]);

}

