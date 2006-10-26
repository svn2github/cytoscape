package csplugins.mcode;


/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 ** User: Gary Bader
 ** Date: Jan 26, 2004
 ** Time: 2:44:30 PM
 ** Description: Stores an MCODE parameter set
 **/

/**
 * Stores an MCODE parameter set
 */
public class MCODEParameterSet {
    //parameters
    //used in scoring stage
    private boolean includeLoops;
    private int degreeCutOff;
    //used in complex finding stage
    private int maxDepthFromStart;
    private double nodeScoreCutOff;
    private boolean fluff;
    private boolean haircut;
    private double fluffNodeDensityCutOff;
    //used in directed mode
    private boolean preprocessNetwork;    //TODO: use when directed mode implemented
    //result viewing parameters (only used for dialog box of results)
    private int defaultRowHeight;

    /**
     * Constructor for the parameter set object. Default parameters are:
     * loops=false, degree cutoff=2, max depth=100, node score cutoff=0.2, fluff=false, haircut=true
     * fluff node density cutoff=0.1, preprocess network (directed mode)=true, default row height for
     * results table=80 pixels.
     */
    public MCODEParameterSet() {
        //default parameters
        includeLoops = false;
        degreeCutOff = 2;               //don't count nodes of degree 1
        maxDepthFromStart = 100;        //effectively unlimited
        nodeScoreCutOff = 0.2;          //user should change this as the main parameter
        fluff = false;
        haircut = true;
        fluffNodeDensityCutOff = 0.1;   //user should change this if fluffing
        preprocessNetwork = true;       //change in directed mode if inter complex connectivity desired
        //results dialog box
        defaultRowHeight = 80;
    }

    /**
     * Constructor for no default algorithm parameters.
     * Results dialog box row height is still default 80 pixels.
     */
    public MCODEParameterSet(boolean includeLoops, int degreeCutOff, int maxDepthFromStart, double nodeScoreCutOff,
                             boolean fluff, boolean haircut, double fluffNodeDensityCutOff) {
        this.setAllAlgorithmParams(includeLoops, degreeCutOff, maxDepthFromStart, nodeScoreCutOff, fluff, haircut,
                fluffNodeDensityCutOff);
        this.preprocessNetwork = true;
        //results dialog box
        defaultRowHeight = 80;
    }

    /**
     * Convenience method to set all the main algorithm parameters
     *
     * @param includeLoops
     * @param degreeCutOff
     * @param maxDepthFromStart
     * @param nodeScoreCutOff
     * @param fluff
     * @param haircut
     * @param fluffNodeDensityCutOff
     */
    public void setAllAlgorithmParams(boolean includeLoops, int degreeCutOff, int maxDepthFromStart, double nodeScoreCutOff,
                                      boolean fluff, boolean haircut, double fluffNodeDensityCutOff) {
        this.includeLoops = includeLoops;
        this.degreeCutOff = degreeCutOff;
        this.maxDepthFromStart = maxDepthFromStart;
        this.nodeScoreCutOff = nodeScoreCutOff;
        this.fluff = fluff;
        this.haircut = haircut;
        this.fluffNodeDensityCutOff = fluffNodeDensityCutOff;
    }

    /**
     * Copies a parameter set object
     *
     * @return A copy of the parameter set
     */
    public MCODEParameterSet copy() {
        MCODEParameterSet newParam = new MCODEParameterSet();
        newParam.setIncludeLoops(this.includeLoops);
        newParam.setDegreeCutOff(this.degreeCutOff);
        newParam.setMaxDepthFromStart(this.maxDepthFromStart);
        newParam.setNodeScoreCutOff(this.nodeScoreCutOff);
        newParam.setFluff(this.fluff);
        newParam.setHaircut(this.haircut);
        newParam.setFluffNodeDensityCutOff(this.fluffNodeDensityCutOff);
        newParam.setPreprocessNetwork(this.preprocessNetwork);
        //results dialog box
        newParam.setDefaultRowHeight(this.defaultRowHeight);
        return newParam;
    }

    //parameter getting and setting
    public boolean isIncludeLoops() {
        return includeLoops;
    }

    public void setIncludeLoops(boolean includeLoops) {
        this.includeLoops = includeLoops;
    }

    public int getDegreeCutOff() {
        return degreeCutOff;
    }

    public void setDegreeCutOff(int degreeCutOff) {
        this.degreeCutOff = degreeCutOff;
    }

    public int getMaxDepthFromStart() {
        return maxDepthFromStart;
    }

    public void setMaxDepthFromStart(int maxDepthFromStart) {
        this.maxDepthFromStart = maxDepthFromStart;
    }

    public double getNodeScoreCutOff() {
        return nodeScoreCutOff;
    }

    public void setNodeScoreCutOff(double nodeScoreCutOff) {
        this.nodeScoreCutOff = nodeScoreCutOff;
    }

    public boolean isFluff() {
        return fluff;
    }

    public void setFluff(boolean fluff) {
        this.fluff = fluff;
    }

    public boolean isHaircut() {
        return haircut;
    }

    public void setHaircut(boolean haircut) {
        this.haircut = haircut;
    }

    public double getFluffNodeDensityCutOff() {
        return fluffNodeDensityCutOff;
    }

    public void setFluffNodeDensityCutOff(double fluffNodeDensityCutOff) {
        this.fluffNodeDensityCutOff = fluffNodeDensityCutOff;
    }

    public boolean isPreprocessNetwork() {
        return preprocessNetwork;
    }

    public void setPreprocessNetwork(boolean preprocessNetwork) {
        this.preprocessNetwork = preprocessNetwork;
    }

    public int getDefaultRowHeight() {
        return defaultRowHeight;
    }

    public void setDefaultRowHeight(int defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }

    public String toString() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Scoring Step: Include Loops: " + includeLoops + " Degree Cutoff: " + degreeCutOff + lineSep);
        sb.append("Cluster Finding Step: Node Score Cutoff: " + nodeScoreCutOff + " Haircut: " + haircut + " Fluff: " + fluff
                + ((fluff) ? (" Fluff Density Cutoff " + fluffNodeDensityCutOff) : "") + " Max. Depth from Seed: " +
                maxDepthFromStart + lineSep);
        return sb.toString();
    }
}
