package fgraph;

public class BadInputException extends Exception
{
    public BadInputException()
    {
        super();
    }

    public BadInputException(String msg)
    {
        super(msg);

    }

    public BadInputException(String msg, Throwable t)
    {
        super(msg, t);
    }
    
    public BadInputException(Throwable t)
    {
        super(t);
    }


}
