package fgraph;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

/**
 * Running the max product algorithm generates a set
 * of output files.  This data structure keeps track
 * of all of the files for a single run of the algorithm
 *
 */
public class SubmodelOutputFiles
{
    List models;
    File edgeDir;
    File edgeSign;
    File edgeModel;
    File edgePath;
    File nodeType;

    public SubmodelOutputFiles()
    {
        models = new ArrayList();
    }

    public void addModel(File f)
    {
        models.add(f);
    }

    public void setEdgeDir(File f)
    {
        edgeDir = f;
    }

    public void setEdgeSign(File f)
    {
        edgeSign = f;
    }

    public void setNodeType(File f)
    {
        nodeType = f;
    }

    public void setEdgeModel(File f)
    {
        edgeModel = f;
    }

    public void setEdgePath(File f)
    {
        edgePath = f;
    }

    
    public List getModels()
    {
        return models;
    }

    public File getEdgeDir()
    {
        return edgeDir;
    }

    public File getEdgeSign()
    {
        return edgeSign;
    }
    
    public File getNodeType()
    {
        return nodeType;
    }

    public File getEdgeModel()
    {
        return edgeModel;
    }

    public File getEdgePath()
    {
        return edgePath;
    }
}
