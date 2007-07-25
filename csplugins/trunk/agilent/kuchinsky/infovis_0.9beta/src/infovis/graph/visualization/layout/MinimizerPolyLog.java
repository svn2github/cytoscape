//  Copyright (C) 2004  Andreas Noack

//  This file is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public License
//  as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.

//  This file is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.

//  To receive a copy of the GNU Lesser General Public License
//  write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//  or download it from http://www.gnu.org/licenses/lgpl.txt

//  Andreas Noack (an@informatik.tu-cottbus.de)
//  Brandenburg University of Technology at Cottbus, Germany

package infovis.graph.visualization.layout;

import infovis.utils.RowIterator;

/**
 * Minimizer for the r-PolyLog energy model. For more information on the
 * r-PolyLog energy model, see "Energy Models for Drawing Clustered Small-World
 * Graphs", Technical Report I-07/2003, Technical University Cottbus, 2003.
 * Available at <a
 * href="http://www-sst.informatik.tu-cottbus.de/CrocoCosmos/linlog.html">
 * <code>www-sst.informatik.tu-cottbus.de/CrocoCosmos/linlog.html</code></a>.
 * 
 * @author Andreas Noack (an@informatik.tu-cottbus.de)
 * @version 21.10.03
 * 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * @infovis.factory GraphLayoutFactory "PolyLog"
 */
public class MinimizerPolyLog extends BasicSpringLayout {
    /**
     * Exponent of the Euclidean distance in the attraction energy = parameter r
     * of the r-PolyLog model.
     */
    private float attrExp = 1.0f;
    /** Normalizing factor for the repulsion energy. */
    private float repulsionFactor;

    /**
     * Sets number of nodes, similarity matrix (edge weights) and position
     * matrix.
     * 
     */
    public MinimizerPolyLog() {
    }

    public String getName() {
        return "PolyLog";
    }

    /**
     * Similarity matrix (edge weights). For unweighted graphs use 1.0f for
     * edges and 0.0f for non-edges. Preconditions: getSim(i,i) == 0 for all i;
     * getSim(i,j) == getSim(j, i) for all i, j
     * 
     * @param i
     *            first vertex
     * @param j
     *            second vertex
     * @return 0 if i==j, 1 otherwise
     */
    public float getSim(int i, int j) {
        return i == j ? 0 : 1;
    }

    /**
     * Sets the exponent of the Euclidean distance in the attraction energy (the
     * parameter r of the r-PolyLog model).
     * 
     * @param attrExp
     *            exponent of the distance in the attraction energy = parameter
     *            r of the r-PolyLog model. Is 1.0f in the LinLog model and 3.0f
     *            in the energy version of the Fruchterman-Reingold model.
     */
    public void setAttractionExponent(float attrExp) {
        this.attrExp = attrExp;
        invalidateVisualization();
    }
    
    protected boolean calcPosition(int v) {
        float[] bestDir = { 0, 0 };
        float oldEnergy = getEnergy(v);
        // compute direction of the move of node i
        getDir(v, bestDir);

        // compute length of the move of node i (line search)
        float oldX = getX(v);
        float oldY = getY(v);
        float bestEnergy = oldEnergy;
        int bestMultiple = 0;
        bestDir[0] /= 32;
        bestDir[1] /= 32;

        for (int multiple = 32; multiple >= 1
                && (bestMultiple == 0 || bestMultiple / 2 == multiple); multiple /= 2) {
            setX(v, oldX + bestDir[0] * multiple);
            setY(v, oldY + bestDir[1] * multiple);

            float curEnergy = getEnergy(v);
            if (curEnergy < bestEnergy) {
                bestEnergy = curEnergy;
                bestMultiple = multiple;
            }
        }

        for (int multiple = 64; multiple <= 128
                && bestMultiple == multiple / 2; multiple *= 2) {
            setX(v, oldX + bestDir[0] * multiple);
            setY(v, oldY + bestDir[1] * multiple);

            float curEnergy = getEnergy(v);
            if (curEnergy < bestEnergy) {
                bestEnergy = curEnergy;
                bestMultiple = multiple;
            }
        }

        setX(v, oldX + bestDir[0] * bestMultiple);
        setY(v, oldY + bestDir[1] * bestMultiple);

        return false;
    }

    /**
     * Returns the Euclidean distance between the nodes i1 and i2.
     * 
     * @return Euclidean distance between the nodes i1 and i2
     */
    private float getDist(int i1, int i2) {
        float dx = getX(i1) - getX(i2);
        float dy = getY(i1) - getY(i2);
        return (float) dist(dx, dy);
    }

    /**
     * Returns the energy of a node.
     * 
     * @param v
     *            index of a node
     * @return energy of the node
     */
    private float getEnergy(int v) {
        float energy = 0.0f;
        float dist; // Euclidean distance to other node

        for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
            int i = iter.nextRow();
            if (i != v) {
                dist = getDist(i, v);
                if (dist == 0.0) {
                    energy = Float.MAX_VALUE;
                    break;
                }
                else {
                    energy += 
                        getSim(v,i) * Math.pow(dist, attrExp) / attrExp
                        - repulsionFactor * (float) Math.log(dist);
                }
            }
        }
        return energy;
    }

    /**
     * Computes the direction of the total force acting on a node.
     * 
     * @param v
     *            index of a node
     * @param dir
     *            direction of the total force acting on the node (output
     *            parameter)
     */
    private void getDir(int v, float[] dir) {
        dir[0] = 0;
        dir[1] = 0;
        float dist; // Euclidean distance to other node
        float avgDist = 0.0f; // average Euclidean distance to other nodes
        float tmp; // temporary, only for efficiency
        float dir2 = 0.0f; // approximate second derivation of energy

        for (int i = 0; i < graph.getVerticesCount(); i++) {
            if (i != v) {
                dist = getDist(i, v);
                avgDist += dist;

                if (dist != 0.0) {
                    dir2 += getSim(v, i) * (attrExp - 1)
                            * Math.pow(dist, attrExp - 2) + repulsionFactor
                            / (dist * dist);

                    tmp = getSim(v,i) * (float) Math.pow(dist, attrExp - 2)
                            - repulsionFactor / (dist * dist);
                    dir[0] += (getX(i) - getX(v)) * tmp;
                    dir[1] += (getY(i) - getY(v)) * tmp;
                }
            }
        }

        dir[0] /= dir2;
        dir[1] /= dir2;

        // length of the vector dir should not be greater
        // then average Euclidean distance to other nodes
        tmp = (float) dist(dir[0], dir[1]);

        avgDist /= graph.getVerticesCount() - 1;
        if (tmp > avgDist && avgDist > 0.0f) {
            tmp /= avgDist;
            dir[0] /= tmp;
            dir[1] /= tmp;
        }
    }

    /**
     * Computes the factor for repulsion forces <code>repulsionfactor</code>
     * such that in the energy minimum the average Euclidean distance between
     * pairs of nodes with similarity 1.0 is approximately 1.
     */
    protected void calcRepulsion() {
        repulsionFactor = 0.0f;
        for (RowIterator iter = graph.vertexIterator(); iter.hasNext(); ) {
            int i = iter.nextRow();
            for (RowIterator jter = graph.vertexIterator(); jter.hasNext(); ) {
                    int j = jter.nextRow();
                repulsionFactor += getSim(i,j);
            }
        }
        repulsionFactor /= 
            graph.getVerticesCount() * (graph.getVerticesCount() - 1) / 2;
        if (repulsionFactor == 0.0f) {
            repulsionFactor = 1.0f;
        }
    }
    
    protected void calcRepulsion(int v) {
        // Nothing to do
    }
    
    protected void calcAttraction(int e) {
        // Nothing to do
    }

    /**
     * Computes and outputs some statistics.
     */
    public String toString() {
        float edgeLengthSum = 0.0f;
        float edgeLengthLogSum = 0.0f;
        float simSum = 0.0f;
        float distLogSum = 0.0f;

        for (int i = 1; i < graph.getVerticesCount(); i++) {
            for (int j = 0; j < i; j++) {
                float dist = getDist(i, j);
                float distLog = (float) Math.log(dist);
                edgeLengthSum += getSim(i, j) * dist;
                edgeLengthLogSum += getSim(i, j) * distLog;
                simSum += getSim(i, j);
                distLogSum += distLog;
            }
        }
        return "Number of nodes: " + graph.getVerticesCount()
            + "\nSum of edge weights: " + simSum
            + "\nArithmetic mean of edge lengths: " + edgeLengthSum / simSum
            + "\nGeometric mean of edge lengths: "
                + (float) Math.exp(edgeLengthLogSum / simSum)
            + "\nGeometric mean of distances: "
                + (float) Math.exp(distLogSum
                        / (graph.getVerticesCount()
                                * (graph.getVerticesCount() - 1) / 2));
    }

}
