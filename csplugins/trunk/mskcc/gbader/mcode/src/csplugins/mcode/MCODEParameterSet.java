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

    //scope
    public static String NETWORK = "network";
    public static String NODE = "node";
    public static String NODE_SET = "node set";
    public String scope;

    //used in scoring stage
    private boolean includeLoops;
    private int degreeCutoff;
    private int kCore;

    //used in cluster finding stage
    private boolean optimize;
    private int maxDepthFromStart;
    private double nodeScoreCutoff;
    private boolean fluff;
    private boolean haircut;
    private double fluffNodeDensityCutoff;

    //used in directed mode
    private boolean preprocessNetwork;

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
        setDefaultParams();

        //results dialog box
        defaultRowHeight = 80;
    }

    /**
     * Constructor for no default algorithm parameters.
     * Results dialog box row height is still default 80 pixels.
     *
     * @param scope scope
     * @param includeLoops include loops
     * @param degreeCutoff degree cutoff
     * @param kCore K-core
     * @param optimize determines if parameters are customized by user/default or optimized
     * @param maxDepthFromStart max depth from start
     * @param nodeScoreCutoff node score cutoff
     * @param fluff fluff
     * @param haircut haircut
     * @param fluffNodeDensityCutoff fluff node density cutoff
     * @param preprocessNetwork preprocess network
     */
    public MCODEParameterSet(
            String scope,
            boolean includeLoops,
            int degreeCutoff,
            int kCore,
            boolean optimize,
            int maxDepthFromStart,
            double nodeScoreCutoff,
            boolean fluff,
            boolean haircut,
            double fluffNodeDensityCutoff,
            boolean preprocessNetwork) {

        setAllAlgorithmParams(
                scope,
                includeLoops,
                degreeCutoff,
                kCore,
                optimize,
                maxDepthFromStart, 
                nodeScoreCutoff,
                fluff,
                haircut,
                fluffNodeDensityCutoff,
                preprocessNetwork);

        //results dialog box
        defaultRowHeight = 80;
    }

    /**
     * Method for setting all parameters to their default values
     */
    public void setDefaultParams() {
        setAllAlgorithmParams(NETWORK, false, 2, 2, false, 100, 0.2, false, true, 0.1, true);
        /*
        scope = NETWORK;
        includeLoops = false;
        degreeCutOff = 2;               //don't count nodes of degree 1
        kCore = 2;                      //only count clusters with a maximally connected core of at least 2 edges (3 nodes)
        optimize = false;               //unless if otherwise specified, the default parameters should be used
        maxDepthFromStart = 100;        //effectively unlimited
        nodeScoreCutOff = 0.2;          //user should change this as the main parameter
        fluff = false;
        haircut = true;
        fluffNodeDensityCutOff = 0.1;   //user should change this if fluffing
        preprocessNetwork = true;       //change in directed mode if inter cluster connectivity desired
        */
    }

    /**
     * Convenience method to set all the main algorithm parameters
     *
     * @param scope scope
     * @param includeLoops include loops
     * @param degreeCutoff degree cutoff
     * @param kCore K-core
     * @param optimize determines if parameters are customized by user/default or optimized
     * @param maxDepthFromStart max depth from start
     * @param nodeScoreCutoff node score cutoff
     * @param fluff fluff
     * @param haircut haircut
     * @param fluffNodeDensityCutoff fluff node density cutoff
     * @param preprocessNetwork preprocess network
     */
    public void setAllAlgorithmParams(
            String scope,
            boolean includeLoops,
            int degreeCutoff,
            int kCore,
            boolean optimize,
            int maxDepthFromStart,
            double nodeScoreCutoff,
            boolean fluff,
            boolean haircut,
            double fluffNodeDensityCutoff,
            boolean preprocessNetwork) {

        this.scope = scope;
        this.includeLoops = includeLoops;
        this.degreeCutoff = degreeCutoff;
        this.kCore = kCore;
        this.optimize = optimize;
        this.maxDepthFromStart = maxDepthFromStart;
        this.nodeScoreCutoff = nodeScoreCutoff;
        this.fluff = fluff;
        this.haircut = haircut;
        this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
        this.preprocessNetwork = preprocessNetwork;
    }

    /**
     * Copies a parameter set object
     *
     * @return A copy of the parameter set
     */
    public MCODEParameterSet copy() {
        MCODEParameterSet newParam = new MCODEParameterSet();
        newParam.setScope(this.scope);
        newParam.setIncludeLoops(this.includeLoops);
        newParam.setDegreeCutoff(this.degreeCutoff);
        newParam.setKCore(this.kCore);
        newParam.setOptimize(this.optimize);
        newParam.setMaxDepthFromStart(this.maxDepthFromStart);
        newParam.setNodeScoreCutoff(this.nodeScoreCutoff);
        newParam.setFluff(this.fluff);
        newParam.setHaircut(this.haircut);
        newParam.setFluffNodeDensityCutoff(this.fluffNodeDensityCutoff);
        newParam.setPreprocessNetwork(this.preprocessNetwork);
        //results dialog box
        newParam.setDefaultRowHeight(this.defaultRowHeight);
        return newParam;
    }

    //parameter getting and setting
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isIncludeLoops() {
        return includeLoops;
    }

    public void setIncludeLoops(boolean includeLoops) {
        this.includeLoops = includeLoops;
    }

    public int getDegreeCutoff() {
        return degreeCutoff;
    }

    public void setDegreeCutoff(int degreeCutoff) {
        this.degreeCutoff = degreeCutoff;
    }

    public int getKCore() {
        return kCore;
    }

    public void setKCore(int kCore) {
        this.kCore = kCore;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public int getMaxDepthFromStart() {
        return maxDepthFromStart;
    }

    public void setMaxDepthFromStart(int maxDepthFromStart) {
        this.maxDepthFromStart = maxDepthFromStart;
    }

    public double getNodeScoreCutoff() {
        return nodeScoreCutoff;
    }

    public void setNodeScoreCutoff(double nodeScoreCutoff) {
        this.nodeScoreCutoff = nodeScoreCutoff;
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

    public double getFluffNodeDensityCutoff() {
        return fluffNodeDensityCutoff;
    }

    public void setFluffNodeDensityCutoff(double fluffNodeDensityCutoff) {
        this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
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
    //TODO: add scope into this aswell
    public String toString() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Scoring Step: Include Loops: " + includeLoops + " Degree Cutoff: " + degreeCutoff + " K-Core: " + kCore + lineSep);
        sb.append("Cluster Finding Step: Optimize: " + optimize
                + ((optimize) ? "" : (" Node Score Cutoff: " + nodeScoreCutoff + " Haircut: " + haircut + " Fluff: " + fluff
                + ((fluff) ? (" Fluff Density Cutoff " + fluffNodeDensityCutoff) : "")
                + " Max. Depth from Seed: " + maxDepthFromStart)) + lineSep);
        return sb.toString();
    }
}
