package bool;

import java.util.List;
import java.lang.Math;

public class ProbTest
{
    Variable _root;

    public static void main(String[] args)
    {
        
        Function cloudy  = new ZeroInput(new double[] {.5, .5});
        Function night  = new ZeroInput(new double[] {.4, .6});

        double[] rainp = new double[4];
        rainp[Function.TT] = .8;
        rainp[Function.FT] = .2;
        rainp[Function.TF] = .2;
        rainp[Function.FF] = .8;

        Function rain = new OneInput(rainp);

        double[] sp = new double[4];
        sp[Function.TT] = .1;
        sp[Function.FT] = .5;
        sp[Function.TF] = .9;
        sp[Function.FF] = .5;

        /*double[] s = new double[4];
          s[Function.TT] = .1;
          s[Function.FT] = .5;
          s[Function.TF] = .9;
          s[Function.FF] = .5;
        */

        Function sprinkler = new OneInput(sp);

        double[] wp = new double[8];
        wp[Function.TTT] = .99;
        wp[Function.TTF] = .01;
        wp[Function.FTT] = .9;
        wp[Function.FTF] = .1;
        wp[Function.TFT] = .9;
        wp[Function.TFF] = .1;
        wp[Function.FFT] = 0;
        wp[Function.FFF] = 1;
        Function wetgrass = new TwoInput(wp);

        Table in = new Table(5);

        int C = 0;
        int N = 1;
        int R = 2;
        int S = 3;
        int W = 4;

        for(int x=0; x < in.getNumConfigs(); x++)
        {

            in.set(x, cloudy.compute(in.get(x, C), new boolean[] {}) 
                   * night.compute(in.get(x, N), new boolean[] {}) 
                   * rain.compute(in.get(x, R), new boolean[] {in.get(x, C)}) 
                   * sprinkler.compute(in.get(x, S), new boolean[] {in.get(x, N)})
                   * wetgrass.compute(in.get(x, W), new boolean[] {in.get(x, S), in.get(x, R)}));

        }

        in.print();
        printMarginal(in, "C", C);
        printMarginal(in, "N", N);
        printMarginal(in, "S", S);
        printMarginal(in, "R", R);
        printMarginal(in, "W", W);


        System.out.println("P(S=1,R=1) = " 
                           + (float) in.marginalize(S, true, R, true));
        System.out.println("P(S=1,R=0) = " 
                           + (float) in.marginalize(S, true, R, false));
        System.out.println("P(S=0,R=1) = " 
                           + (float) in.marginalize(S, false, R, true));
        System.out.println("P(S=0,R=0) = " 
                           + (float) in.marginalize(S, false, R, false));
        

        Table in2 = new Table(4);

        int C2 = 0;
        int R2 = 1;
        int S2 = 2;
        int W2 = 3;

        for(int x=0; x < in2.getNumConfigs(); x++)
        {

            in2.set(x, cloudy.compute(in2.get(x, C2), new boolean[] {}) 
                   * rain.compute(in2.get(x, R2), 
                                  new boolean[] {in2.get(x, C2)}) 
                   * sprinkler.compute(in2.get(x, S2), 
                                       new boolean[] {in2.get(x, C2)})
                   * wetgrass.compute(in2.get(x, W2), 
                                      new boolean[] {in2.get(x, S2), 
                                                     in2.get(x, R2)}));

        }

        System.out.println("Cloudy only");
        in2.print();
        printMarginal(in2, "C2", C2);
        printMarginal(in2, "R2", R2);
        printMarginal(in2, "S2", S2);
        printMarginal(in2, "W2", W2);
        
        System.out.println("P(S=1,W=1) = " 
                           + (float) in2.marginalize(S2, true, W2, true));

        System.out.println("P(R=1,W=1) = " 
                           + (float) in2.marginalize(R2, true, W2, true));
        

        
        System.out.println("P(S=1,R=1) = " 
                           + (float) in2.marginalize(S2, true, R2, true));
        System.out.println("P(S=1,R=0) = " 
                           + (float) in2.marginalize(S2, true, R2, false));
        System.out.println("P(S=0,R=1) = " 
                           + (float) in2.marginalize(S2, false, R2, true));
        System.out.println("P(S=0,R=0) = " 
                           + (float) in2.marginalize(S2, false, R2, false));
        


    }

    static void printMarginal(Table t, String name, int var)
    {
        System.out.println("P(" + name + "=true) = " + 
                           (float) t.marginalize(var, true));
        System.out.println("P(" + name + "=false) = " + 
                           (float) t.marginalize(var, false));
    }
}

class Table
{
    boolean[][] _configs;
    double[] _probs;
    int _numConfigs;

    Table(int numvars)
    {
        _numConfigs = (int) Math.pow(2, numvars);
        _configs = enumerate(numvars);
        _probs = new double[_numConfigs];
    }

    int getNumConfigs() { return _numConfigs; }

    boolean get(int config, int var)
    {
        return _configs[config][var];
    }
    void set(int config, double prob)
    {
        _probs[config] = prob;
    }

    double marginalize(int var, boolean value)
    {
        double sum = 0;
        for(int x=0; x < _configs.length; x++)
        {
            if(_configs[x][var] == value)
            {
                sum += _probs[x];
            }
        }
        return sum;
    }

    double marginalize(int var, boolean value,
                       int v2, boolean val2)
    {
        double sum = 0;
        for(int x=0; x < _configs.length; x++)
        {
            if((_configs[x][var] == value) && (_configs[x][v2] == val2))
            {
                sum += _probs[x];
            }
        }
        return sum;
    }


    private boolean[][] enumerate(int numvars)
    {
        boolean[][] vals = new boolean[(int) Math.pow(2, numvars)][numvars];
        for(int x=0; x < vals.length; x++)
        {
            setbits(vals, 0, vals.length/2, true, 0, numvars);
            setbits(vals, vals.length/2, vals.length/2, false, 0, numvars);
        }
        return vals;
    }

    private void setbits(boolean[][] data, int start, int num, boolean val, 
                         int index, int stop)
    {
        for(int x=start; x < start + num; x++)
        {
            data[x][index] = val;
        }
        if(index < stop)
        {
            setbits(data, start, num/2, true, index + 1, stop);
            setbits(data, start + num/2, num/2, false, index + 1, stop);
        }
    }

    void print()
    {
        for(int x=0; x < _configs.length; x++)
        {
            for(int y=0; y < _configs[x].length; y++)
            {
                System.out.print(_configs[x][y]);
                System.out.print("\t");
            }
            System.out.print((float) _probs[x]);
            System.out.print("\n");
        }
    }
}

abstract class Function
{
    static int F = 0;
    static int T = 1;
    static int TT = 0; // inp, val
    static int TF = 1;
    static int FT = 2;
    static int FF = 3;
    static int TTT = 0; // in1, in2, val
    static int TFF = 1;
    static int FTF = 2;
    static int FFT = 3;
    static int TTF = 4;
    static int TFT = 5;
    static int FTT = 6;
    static int FFF = 7;

    double[] _probs;

    Function(double[] probs)
    {
        _probs = probs;
    }
    
    abstract double compute(boolean b, boolean[] variables);
}

class ZeroInput extends Function
{
    ZeroInput(double[] probs)
    {
        super(probs);
    }
    double compute(boolean b, boolean[] variables) 
    { 
        if(b)
            return _probs[T];
        else
            return _probs[F]; 
    }
}


class OneInput extends Function
{
    OneInput(double[] p)
    {
        super(p);
    }
    
    double compute(boolean b, boolean[] variables) 
    { 
        if(b)
        {
            if(variables[0]) {
                return _probs[TT];
            } else {
                return _probs[FT];
            }
        }
        else
         {
             if(variables[0]) {
                 return _probs[TF];
             } else {
                 return _probs[FF];
             }
         }
    }
}


class TwoInput extends Function
{
    TwoInput(double[] p)
    {
        super(p);
    }
    
    double compute(boolean b, boolean[] variables) 
    { 
        if(b)
        {
            if(variables[0]) {
                if(variables[1]) {
                    return _probs[TTT];
                }
                else {
                    return _probs[TFT];
                }
            } else {
                if(variables[1]) {
                    return _probs[FTT];
                }
                else {
                    return _probs[FFT];
                }
            }
        }
        else
        {
            if(variables[0]) {
                if(variables[1]) {
                    return _probs[TTF];
                }
                else {
                    return _probs[TFF];
                }
            } else {
                if(variables[1]) {
                    return _probs[FTF];
                }
                else {
                    return _probs[FFF];
                }
            }
        }
    }
}



class Variable
{
    private int _states;
    private double[] _probs;
    private List _depends;

    Variable(int states, List depends)
    {
        _states = states;
        _probs = new double[states];
        _depends = depends;
    }

    int numStates() { return _states; }
    List getDepends() { return _depends; }
    void setProb(int state, double val)
    {
        _probs[state] = val;
    }
}
