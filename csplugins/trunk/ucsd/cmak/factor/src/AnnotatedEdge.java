class AnnotatedEdge
{
    AnnotatedEdge(int interactionIndex)
    {
        this.interactionIndex = interactionIndex;
        invariant = false;
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

        return b.toString();
    }
}
