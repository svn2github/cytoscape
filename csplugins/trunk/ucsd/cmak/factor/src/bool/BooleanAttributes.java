package bool;

import java.util.List;

public class BooleanAttributes extends NodeAttributes
{
    public static int T = 1;
    public static int F = 0;

    public BooleanAttributes()
    {
        super(2);
        setProb(F, .5);
        setProb(T, .5);
    }

    public BooleanAttributes(double pTrue, double pFalse)
    {
        super(2);
        setProb(F, pFalse);
        setProb(T, pTrue);
    }

    public Message sumProduct(List msgs, int targetNodeId)
    {
        double t = 1;
        double f = 1;
        
        if(msgs.size() > 0)
        {
            for(int x=0; x < msgs.size(); x++)
            {
                Message m = (Message) msgs.get(x);
                t *= m.getProb(T);
                f *= m.getProb(F);
            }
        }
        else
        {
            t = getProb(T);
            f = getProb(F);
        }

        this.setProb(T, t);
        this.setProb(F, f);
        this.normalize();

        Message result = new Message(this.getId());
        result.setProb(T, t);
        result.setProb(F, f);
        result.normalize();

        System.out.println("node " + getId() + " sending\n" + result);
        return result;
    }

    public String toString()
    {
        String s = "F\t" + getProb(F) + "\nT\t" + getProb(T);
        return s;
    }
}
