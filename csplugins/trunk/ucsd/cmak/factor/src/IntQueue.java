import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class IntQueue 
{
    LinkedList _l;

    public IntQueue()
    {
        _l = new LinkedList();
    }

    public void enqueue(int n)
    {
        _l.addLast(new Integer(n));
    }

    public int dequeue() throws NoSuchElementException
    {
        Object first = _l.removeFirst();
        return ((Integer) first).intValue();
    }

    public int head() throws NoSuchElementException
    {
        return ((Integer) _l.getFirst()).intValue();
    }

    public int size()
    {
        return _l.size();
    }

    public Iterator iterator()
    {
        return _l.iterator();
    }
}
