public class State
{
    private final String name;

    private State(String n)
    {
        name = n;
    }

    public String toString() { return name; }

    public static final State ONE = new State("1");
    public static final State ZERO = new State("0");

    public static final State PLUS = new State("+");
    public static final State MINUS = new State("-");
    
}

