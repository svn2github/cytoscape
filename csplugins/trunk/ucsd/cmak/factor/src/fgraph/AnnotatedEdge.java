package fgraph;

import cern.colt.list.IntArrayList;

class AnnotatedEdge
{
    AnnotatedEdge(int interactionIndex)
    {
        this.interactionIndex = interactionIndex;
        invariant = false;
        active = false;
        submodels = new IntArrayList();
    }
    
    int interactionIndex;
    int fgIndex;
    int signIndex;
    int dirIndex;

    String label;
    
    State maxState;
    State maxDir;
    State maxSign;

    boolean invariant;
    boolean active;

    IntArrayList submodels;

    void addSubmodel(int m)
    {
        submodels.add(m);
    }
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(label);
        b.append(" [");
        b.append(interactionIndex);
        b.append("] fg=");
        b.append(fgIndex);
        b.append(" dir=");
        b.append(dirIndex);
        b.append(" sign=");
        b.append(signIndex);
        b.append(" max=");
        b.append(maxState);
        b.append(" maxDir=");
        b.append(maxDir);
        b.append(" maxSign=");
        b.append(maxSign);
        b.append(" active=");
        b.append(active);

        return b.toString();
    }
}
