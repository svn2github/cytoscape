package bool;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Maps a configuration of variable states to the 
 * joint probability of that state.
 */
public class FunctionTable extends NodeAttributes
{
    int[][] _configs;
    int _numConfigs;

    Map _node2var;
    Set _varStates;

    public FunctionTable(int numvars, Set varStates)
    {
        super((int) Math.pow(varStates.size(), numvars));
        _numConfigs = (int) Math.pow(varStates.size(), numvars);

        _configs = enumerate(numvars, varStates);
        _node2var = new HashMap();
        _varStates = varStates;

        //print();
    }

    int getNumConfigs() { return _numConfigs; }

    int getStateValue(int config, int var)
    {
        return _configs[config][var];
    }
    
    void setNode2Var(int nodeId, int var)
    {
        _node2var.put(new Integer(nodeId), new Integer(var));
    }

    /**
     * @return the variable (column) of this table that corresponds
     * to the state of the input node "nodeId"
     */
    int node2var(int nodeId)
    {
        Integer i = new Integer(nodeId);
        if(_node2var.containsKey(i))
        {
            return ((Integer) _node2var.get(i)).intValue();
        }
        else
        {
            System.err.println("#### node=" + getId() 
                               + ": no var found for node " + nodeId);
            return 0;
        }
    }

    /**
     * @param msgs a list of messages.
     * @param targetNodeId the id of the target node for the message being computed
     * 
     */
    public Message sumProduct(List msgs, int targetNodeId)
    {
        double[] margProb = new double[_numConfigs];

        for(int x=0; x < margProb.length; x++)
        {
            margProb[x] = this.getProb(x);
        }

        // multiply incoming messages
        for(int c=0; c < _configs.length; c++)
        {
            for(int m = 0; m < msgs.size(); m++)
            {
                Message msg = (Message) msgs.get(m);
                if(msg.getSourceId() != targetNodeId)
                {
                    int state = _configs[c][node2var(msg.getSourceId())];
                    margProb[c] *= msg.getProb(state);
                }
            }
        }

        print(margProb);

        // marginalize (sum) over the states of the targetNode
        double[] sumProb = new double[_varStates.size()];
        Arrays.fill(sumProb, 0);
        int targetVarIndex = node2var(targetNodeId);
        for(int c=0; c < _configs.length; c++)
        {
            sumProb[_configs[c][targetVarIndex]] += margProb[c];
        }
        
        Message result = new Message(this.getId(), sumProb);
        result.normalize();

        System.out.println("node " + getId() + " sending\n" + result);
        return result;
    }

    private int[][] enumerate(int numvars, Set varStates)
    {
        int sz = varStates.size();

        //System.out.println("size of state set=" + sz);
        int[][] vals = new int[(int) Math.pow(sz, numvars)][numvars];

        //System.out.println("size of state set=" + sz);
        //System.out.println("numvars=" + numvars);
        //System.out.println("num configs=" + vals.length);
        int index = 0;
        for(Iterator it = varStates.iterator(); it.hasNext();)
        {
            State s = (State) it.next();
            //System.out.println("index = " + index);
            setbits(vals, index, vals.length/sz, s.toInt(), 0, numvars, varStates);
            index +=  vals.length/sz;
        }
        return vals;
    }

    private void setbits(int[][] data, int start, int num, int val, 
                         int index, int stop, Set varStates)
    {

        for(int x=start; x < start + num; x++)
        {
            //System.out.println("Setting [" + x + "][" + index + "] to " + val);
            data[x][index] = val;
        }
        if(index < stop - 1)
        {
            int sz = varStates.size();
            int i = start;
            for(Iterator it = varStates.iterator(); it.hasNext();)
            {
                State s = (State) it.next();
                //  System.out.println("# setbits from " + index + " for " + 
                //                 num/sz + " to " + s);
                setbits(data, i, num/sz, s.toInt(), index + 1, stop, varStates);
                i +=  num/sz;
            }
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
            System.out.print((float) getProb(x));
            System.out.print("\n");
        }
    }

    
    void print(double[] marg)
    {
        for(int x=0; x < _configs.length; x++)
        {
            for(int y=0; y < _configs[x].length; y++)
            {
                System.out.print(_configs[x][y]);
                System.out.print("\t");
            }
            System.out.print((float) getProb(x));
            System.out.print("\t");
            System.out.print((float) marg[x]);
            System.out.print("\n");
        }
    }


}
