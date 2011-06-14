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
    
    public static native void createGraph(int edgeArray[], int length);
    
    //Test whether the graph is connected
    public static native boolean isConnected();
    
    //Simplify the graph for community ananlysis
    public static native void simplify();
    
}