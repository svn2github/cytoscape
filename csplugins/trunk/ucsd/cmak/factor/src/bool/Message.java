package bool;

public class Message extends BooleanAttributes
{
    private int _sourceId;

    public Message(int id)
    {
        super();
        _sourceId = id;
    }

    public Message(int id, double[] probs)
    {
        super();
        _sourceId = id;
        setProb(T, probs[T]);
        setProb(F, probs[F]);
    }
    

    public int getSourceId()
    {
        return _sourceId;
    }

}
