package fgraph;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

public class SubmodelOutput
{
    List models;
    File edgeDir;
    File edgeSign;
    //    File edgeModel;
    File nodeType;

    public SubmodelOutput()
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
}
