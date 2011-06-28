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
    
//     public static native int nativeAdd(int a, int b);
   
    // Create an igraph's graph
    public static native void createGraph(int edgeArray[], int length);
    
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
						boolean useSeed);
    
}