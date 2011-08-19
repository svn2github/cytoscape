/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.networkClusterers.glay;
import java.util.*;
import cytoscape.*;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.function.*;
import giny.model.Node;
import giny.model.Edge;
/**
 *
 * @author Gang Su
 */
public interface GAlgorithm {

    public abstract double getModularity();
    public int[] getMembership();

}
