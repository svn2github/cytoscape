import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import cern.colt.bitvector.BitVector;

import java.lang.Math;

public class PathFactorNode implements FactorNode
{
    protected static final double ep1 = 0.7; // epsilon 1
    protected static final double ep2 = 0.299; // epsilon 2


    private static final int ODD = 1; // product of signs == -1
    private static final int EVEN = 0; // product of sings == +1

    private static PathFactorNode __singleton = new PathFactorNode();

    public static PathFactorNode getInstance()
    {
        return __singleton;
    }

    private PathFactorNode()
    {
    }

    /**
     * @param allMsgs a list of EdgeMessage objects.
     * @param tIndex the index of the EdgeMessage in the allMsgs list
     * that corresponds to messages passed from the factor node to the target 
     * @param target the target of the message
     */
    public ProbTable maxProduct(List allMsgs, int tIndex, VariableNode target)
        throws AlgorithmException
    {
        List incoming = new ArrayList();
        List x = new ArrayList();
        List d = new ArrayList();
        List dirStates = new ArrayList();
        List s = new ArrayList();
        ProbTable sigma = null;
        ProbTable k = null;

        // organize the incoming messages
        int numSig = 0;
        int numK = 0;
        for(int m=0, N = allMsgs.size(); m < N; m++)
        {
            if(m == tIndex)
            {
                continue;
            }
            
            EdgeMessage em = (EdgeMessage) allMsgs.get(m);
            ProbTable p = em.v2f();
            incoming.add(p);
            
            StateSet si = p.stateSet();
            if( si == StateSet.EDGE)
            {
                x.add(p);
            }
            else if (si == StateSet.DIR)
            {
                dirStates.add(em.getDir());
                d.add(p);
            }
            else if (si == StateSet.SIGN)
            {
                s.add(p);
            }
            else if (si == StateSet.PATH_ACTIVE)
            {
                sigma = p;
                numSig++;
            }
            else if (si == StateSet.KO)
            {
                k = p;
                numK++;
            }
            else
            {
                System.err.println("Unexpected StateSet encountered: " + si);
            }
        }

        if(numSig > 1 || numK > 1)
        {
            throw new AlgorithmException("> 1 message received from path-active or KO node");
        }
        
        
        NodeType tt = target.type();
        if(tt == NodeType.EDGE)
        {
            StateSet ss = StateSet.EDGE;
            ProbTable pt = new ProbTable(ss);

            double pathExplains = computePathExplains(x, d, dirStates, k, sigma, s);
            double tmpProb = maximize(x, 1) * maximize(d, 1) * maximize(s, 1);
            double pathInactive = ep1 * tmpProb * k.max() * sigma.prob(State.ZERO);
            double pathViolates = ep2 * tmpProb * k.prob(State.ZERO) * sigma.prob(State.ONE);

            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.ZERO)] = maximize(incoming, ep1);
            probs[ss.getIndex(State.ONE)] = maximize(pathExplains, 
                                                     pathInactive, 
                                                     pathViolates);
            pt.init(probs);

            System.out.println("cv=" + probs[ss.getIndex(State.ZERO)]);
            System.out.println("pe=" + pathExplains);
            System.out.println("pi=" + pathInactive);
            System.out.println("pv=" + pathViolates);


            return pt;
        }
        else if (tt == NodeType.DIR)
        {
            // this is incorrect.  d must be consistent with k
            StateSet ss = StateSet.DIR;
            ProbTable pt = new ProbTable(ss);

            double pathExplains = computePathExplains(x, d, dirStates, k, sigma, s);
            double tmpProb = maximize(x, 1) * maximize(d, 1) * maximize(s, 1);
            double pathInactive = ep1 * tmpProb * k.max() * sigma.prob(State.ZERO);
            double pathViolates = ep2 * tmpProb * k.prob(State.ZERO) * sigma.prob(State.ONE);

            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.MINUS)] = maximize(incoming, ep1);
            probs[ss.getIndex(State.PLUS)] = maximize(pathExplains, 
                                                      pathInactive, 
                                                      pathViolates);
            pt.init(probs);

            System.out.println("cv=" + probs[ss.getIndex(State.MINUS)]);
            System.out.println("pe=" + pathExplains);
            System.out.println("pi=" + pathInactive);
            System.out.println("pv=" + pathViolates);
            
            return pt;
        }
        else if (tt == NodeType.SIGN)
        {
            StateSet ss = StateSet.SIGN;
            ProbTable pt = new ProbTable(ss);

            double pathExplainsMinus = computePathExplainsFixSign(x, d, dirStates,
                                                                  k, sigma, 
                                                                  s, State.MINUS);

            double pathExplainsPlus = computePathExplainsFixSign(x, d, dirStates,
                                                                 k, sigma, 
                                                                 s, State.PLUS);
            double tmpProb = maximize(x, 1) * maximize(d, 1) * maximize(s, 1);
            double pathInactive = ep1 * tmpProb * k.max() * sigma.prob(State.ZERO);
            double pathViolates = ep2 * tmpProb * k.prob(State.ZERO) * sigma.prob(State.ONE);

            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.MINUS)] = maximize(pathExplainsMinus, 
                                                      pathInactive, 
                                                      pathViolates);

            probs[ss.getIndex(State.PLUS)] = maximize(pathExplainsPlus, 
                                                      pathInactive, 
                                                      pathViolates);
            pt.init(probs);

            System.out.println("pe+ =" + pathExplainsPlus);
            System.out.println("pe- =" + pathExplainsMinus);
            System.out.println("pi=" + pathInactive);
            System.out.println("pv=" + pathViolates);

            return pt;
        }
        else if (tt == NodeType.PATH_ACTIVE)
        {
            StateSet ss = StateSet.PATH_ACTIVE;
            ProbTable pt = new ProbTable(ss);

            double pathExplains = computePathExplains(x, d, dirStates, k, s);
            double pathInactive = ep1 * maximize(x, 1) * maximize(d, 1) * maximize(s, 1) * k.max();
            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.ZERO)] = maximize(incoming, ep1);
            probs[ss.getIndex(State.ONE)] = Math.max(pathExplains, pathInactive); 
 
            pt.init(probs);
           
            System.out.println("cv=" + probs[ss.getIndex(State.ZERO)]);
            System.out.println("pe=" + pathExplains);
            System.out.println("pi=" + pathInactive);

            return pt;
        }
        else if (tt == NodeType.KO)
        {
            StateSet ss = StateSet.KO;
            ProbTable pt = new ProbTable(ss);

            double pathExplainsPlus = computePathExplainsFixKO(x, d, dirStates,
                                                               sigma, 
                                                               s, State.PLUS);

            double pathExplainsMinus = computePathExplainsFixKO(x, d, dirStates,
                                                                sigma, 
                                                                s, State.MINUS);
            double pathViolates = ep2 * maximize(x, 1) * maximize(d, 1) * maximize(s, 1) * sigma.prob(State.ONE);

            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.ZERO)] = maximize(incoming, ep1);
            probs[ss.getIndex(State.PLUS)] = Math.max(pathExplainsPlus, pathViolates); 
            probs[ss.getIndex(State.MINUS)] = Math.max(pathExplainsMinus, pathViolates); 
 
            pt.init(probs);
           
            System.out.println("cv=" + probs[ss.getIndex(State.ZERO)]);
            System.out.println("pe+ =" + pathExplainsPlus);
            System.out.println("pe- =" + pathExplainsMinus);
            System.out.println("pv=" + pathViolates);

            return pt;
        }
        else
        {
            System.err.println("Unknown target variable type: " + tt);
            return null;
        }
    }

    /**
     * @return Prod [ msgs(x==1), msgs(all d consistent with path), 
     *                max { msgs(s) : Prod s == -k, k = fixed} ]
     * 
     * @param x messages from edge nodes
     * @param d messages from direction nodes
     * @param dirStates a List of State.{PLUS|MINUS}.  The i-th state corresponse
     * to the value that the i-th d variable must have to be consistent with the
     *  direction of information flow implied by a knockout effect.
     *  The size of dirStates must be the same as the size of d.
     * @param s messages from sign nodes
     * @param sigma message from knockout effect node
     * @param State state that the KO will be fixed
     */
    protected double computePathExplainsFixKO(List x, List d, List dirStates,
                                              ProbTable sigma, 
                                              List s, State fixed)
    {
        double m = 1;

        // all edge presences must be 1
        m *= computeProbFixState(x, State.ONE);

        // directions must match dir implied by path
        m *= computeProbDir(d, dirStates);
        
        // sigma must be 1
        m *= sigma.prob(State.ONE);

        if(fixed == State.PLUS || fixed == State.MINUS)
        {
            m *= maximizeSign(s, fixed);
        }

        return m;
    }


    /**
     * @return Prod [ msgs(x==1), msgs(all d consistent with path), 
     *                max{m(k==+1), m(k==-1)},
     *                max { msgs(s) : Prod s == -k AND s[i] = fixedSign} ]
     * 
     * @param x messages from edge nodes
     * @param d messages from direction nodes
     * @param dirStates a List of State.{PLUS|MINUS}.  The i-th state corresponse
     * to the value that the i-th d variable must have to be consistent with the
     *  direction of information flow implied by a knockout effect.
     *  The size of dirStates must be the same as the size of d.
     * @param s messages from sign nodes
     * @param sigma messages from sigma node
     * @param k messages from knockout effect node
     */
    protected double computePathExplainsFixSign(List x, List d, List dirStates,
                                                ProbTable k, 
                                                ProbTable sigma, 
                                                List s, State fixedSign)
    {
        double m = 1;

        // all edge presences must be 1
        m *= computeProbFixState(x, State.ONE);

        // directions must match dir implied by path
        m *= computeProbDir(d, dirStates);

        // sigma must be 1
        m *= sigma.prob(State.ONE);

        // find max value of k and signs given the fixedSign
        m *= maximizeSign(s, k, fixedSign);

        return m;
    }


    /**
     * @return Prod [ msgs(x==1), msgs(all d consistent with path), 
     *                max{m(k==+1), m(k==-1)},
     *                max { msgs(s) : Prod s == -k} ]
     * 
     * @param x messages from edge nodes
     * @param d messages from direction nodes
     * @param dirStates a List of State.{PLUS|MINUS}.  The i-th state corresponse
     * to the value that the i-th d variable must have to be consistent with the
     *  direction of information flow implied by a knockout effect.
     *  The size of dirStates must be the same as the size of d.
     * @param s messages from sign nodes
     * @param sigma messages from sigma node
     * @param k messages from knockout effect node
     */
    protected double computePathExplains(List x, List d, List dirStates,
                                         ProbTable k, ProbTable sigma, 
                                         List s)
    {
        double m = 1;

        // sigma must be 1
        m *= sigma.prob(State.ONE);

        m *= computePathExplains(x, d, dirStates, k, s);

        return m;
    }


    /**
     * @return Prod [ msgs(x==1), msgs(all d consistent with path), 
     *                max{m(k==+1), m(k==-1)},
     *                max { msgs(s) : Prod s == -k} ]
     * 
     * @param x messages from edge nodes
     * @param d messages from direction nodes
     * @param dirStates a List of State.{PLUS|MINUS}.  The i-th state corresponse
     * to the value that the i-th d variable must have to be consistent with the
     *  direction of information flow implied by a knockout effect.
     *  The size of dirStates must be the same as the size of d.
     * @param s messages from sign nodes
     * @param k messages from knockout effect node
     */
    protected double computePathExplains(List x, List d, List dirStates,
                                         ProbTable k, List s)
    {
        double m = 1;

        // all edge presences must be 1
        m *= computeProbFixState(x, State.ONE);

        // directions must match dir implied by path
        m *= computeProbDir(d, dirStates);
        
        // find max value of k and signs
        m *= maximizeSign(s, k);

        return m;
    }

    // directions must match dir implied by path
    protected double computeProbDir(List d, List dirStates)
    {
        double m = 1;

        for(int y=0, n=d.size(); y < n; y++)
        {
            m *= ((ProbTable) d.get(y)).prob((State) dirStates.get(y));
        }
        return m;
    }
    
    
    /**
     * @return the probabilites of all of the vars in the input List
     * for a fixed state "fixed"
     *
     * @param fixed the state
     */
    protected double computeProbFixState(List p, State fixed)
    {
        double m = 1;
        for(int y=0, n=p.size(); y < n; y++)
        {
            // fix this later
            m *= ((ProbTable) p.get(y)).prob(fixed);
        }
        return m;
    }

    protected double maximizeSign(List signs, ProbTable k)
    {
        return maximizeSign(signs, k, null);
    }
    
    /**
     * @param signs a list of ProbTables of sign variables
     * @param k the ProbTable of the knockout node
     * @param fixedSign If we should compute the max under the condition that
     * one of the sign vars (not in the input list) is fixed to a specific
     * state: either State.PLUS or State.MINUS.  Or null if no signs are
     * externally fixed.
     */
    protected double maximizeSign(List signs, ProbTable k, State fixedSign)
    {
        if(! (signs.size() > 0))
        {
            return 1;
        }
        
        // Generate all combinations of valid signs given
        // the number of sign variables
        BitVector[][] validCombos = enumerate(signs.size());

        BitVector[] plusCombos = validCombos[0];
        BitVector[] minusCombos = validCombos[1];

        // if one of the signs is fixed to be -1, then the
        // of signs is flipped, hence the validCombos are swapped
        //
        // fixing one of the signs to +1 does not change the set
        // of valid combos
        if(fixedSign == State.MINUS)
        {
            plusCombos = validCombos[1];
            minusCombos = validCombos[0];
        }
        
        // Copy sign probabilities into an array for better performance
        double[] probPlus = new double[signs.size()];
        double[] probMinus = new double[signs.size()];
        for(int x=0, n=signs.size(); x < n; x++)
        {
            ProbTable pt = (ProbTable) signs.get(x);
            probPlus[x] = pt.prob(State.PLUS);
            probMinus[x] = pt.prob(State.MINUS);
        }
                
        // compute the probability of each combo of signs
        double[] maxPlus = new double[plusCombos.length];
        double[] maxMinus = new double[minusCombos.length];

        Arrays.fill(maxPlus, k.prob(State.PLUS));
        Arrays.fill(maxMinus, k.prob(State.MINUS));

        for(int x=0; x <= 1 ; x++)
        {
            BitVector[] combos;
            double[] vals;
            if(x==0) {
                combos=plusCombos;
                vals=maxPlus;
            }
            else
            {
                combos=minusCombos;
                vals=maxMinus;
            }
            
            for(int v=0; v < combos.length; v++)
            {
                for(int bit=0, numBits=combos[v].size(); bit < numBits; bit++)
                {
                    if(combos[v].get(bit) == true)
                    {
                        vals[v] *= probMinus[bit];
                    }
                    else
                    {
                        vals[v] *= probPlus[bit];
                    }
                }
            }
        }
        
        // Sort values into ascending numerical order
        Arrays.sort(maxPlus);
        Arrays.sort(maxMinus);

        // return the max value.
        return Math.max(maxPlus[maxPlus.length - 1], maxMinus[maxMinus.length - 1]);
    }

    /**
     * @param numSigns the number of sign variables
     *
     * @return an array of 2 arrays of cern.colt.BitVector
     * Each nested array of cern.colt.bitvector.BitVector's represent 
     * configurations of sign variables that satisfy the constraint
     * that the product of signs is -1 if pORm == PLUS, or +1 if pORm == MINUS.
     * <p>
     * Element 0 is the array of configurations that satisfy k=+1
     * Element 1 is the array of configurations that satisfy k=-1
     * <p>
     * The i-th bit a BitVector is set to 1 if the i-th sign variable is
     * -1 in the configuration.  The i-th bit is 0 if the i-th sign variable
     * is +1 in the configuration.
     */
    protected BitVector[][] enumerate(int numSigns)
    {
        int numCombos = (int) Math.pow(2, numSigns - 1);

        BitVector[][] combos = new BitVector[2][numCombos];
        BitVector[] plusCombos = combos[0];
        BitVector[] minusCombos = combos[1];

        for(int x=0, n=2*numCombos, p=0, m=0; x < n; x++)
        {
            int par = parity(x);
            if(par == ODD)
            {
                /*
                System.out.println("adding odd  combo: x=" + x + 
                                   " p=" + p + " n=" + n +
                                   " parity=" + parity(x));
                */
                plusCombos[p] = new BitVector(new long[] {x}, numSigns);
                p++;
                
            }
            else
            {
                /*System.out.println("adding even combo: x=" + x + 
                                   " m=" + m + " n=" + n +
                                   " parity=" + parity(x));
                */
                minusCombos[m] = new BitVector(new long[] {x}, numSigns);
                m++;
            
            }
        }
        
        return combos;
    }
    
    /**
     * @throws IllegalArgumentException if pORm is not State.PLUS or State.MINUS
     */
    protected double maximizeSign(List signs, State pORm)
    {
        if(pORm != State.PLUS && pORm != State.MINUS)
        {
            throw new IllegalArgumentException(pORm + " is not PLUS or MINUS");
        }

        if(! (signs.size() > 0))
        {
            return 1;
        }
        
        // Generate all combinations of valid signs given
        // the state pORm
        BitVector[] validCombos = enumerate(signs.size(), pORm);

        // Copy sign probabilities into an array for better performance
        double[] probPlus = new double[signs.size()];
        double[] probMinus = new double[signs.size()];
        for(int x=0, n=signs.size(); x < n; x++)
        {
            ProbTable pt = (ProbTable) signs.get(x);
            probPlus[x] = pt.prob(State.PLUS);
            probMinus[x] = pt.prob(State.MINUS);
        }
                
        // compute the probability of each combo of signs
        double[] max = new double[validCombos.length];
        Arrays.fill(max, 1);
 
        for(int v=0; v < validCombos.length; v++)
        {
            for(int bit=0, numBits=validCombos[v].size(); bit < numBits; bit++)
            {
                if(validCombos[v].get(bit) == true)
                {
                    max[v] *= probMinus[bit];
                }
                else
                {
                    max[v] *= probPlus[bit];
                }
            }
        }

        // Sort values into ascending numerical order
        Arrays.sort(max);

        // return the max value.
        return max[max.length - 1];
    }
    
    /**
     * @param numSigns the number of sign variables
     * @param pORm either State.PLUS or State.MINUS
     *
     * @return an array of cern.colt.bitvector.BitVector's that represent 
     * configurations of sign variables that satisfy the constraint
     * that the product of signs is -1 if pORm == PLUS, or +1 if pORm == MINUS.
     * The i-th bit in the vector is set to 1 if the i-th sign variable is
     * -1 in the configuration.  The i-th bit is 0 if the i-th sign variable
     * is +1 in the configuration.
     */
    protected BitVector[] enumerate(int numSigns, State pORm)
    {
        int numCombos = (int) Math.pow(2, numSigns - 1);
        BitVector[] combos = new BitVector[numCombos];

        int par;

        if(pORm == State.PLUS)
        {
            par = ODD;
        }
        else
        {
            par = EVEN;
        }

        for(int x=0, n=2*numCombos, i=0; x < n; x++)
        {
            if(parity(x) == par)
            {
                combos[i] = new BitVector(new long[] {x}, numSigns);
                i++;
            }
        }

        return combos;
    }
    
    /**
     * @return the number of 1-bits in x
     * @param x an number >= 0
     * copied from bitcnt_1.c at http://c.snippets.org
     */
    protected int countBits(int x)
    {
        int n = 0;
        /*
        ** The loop will execute once for each bit of x set, this is in average
        ** twice as fast as the shift/test method.
        */
        if (x > 0) 
        {
            do {
                n++;
            } while (0 != (x = x&(x-1)));
        }
                  
        return n;
    }

    /**
     * @param w a 32 bit int >= 0
     *
     * @return 1 of the number of 1-bits in w is odd, 0 if even.
     */
    protected int parity (int w)
    {
      // parity of the number of 1-bit
      // 0 if even; 1 if odd
      // implementation for 32 bit words
      w ^= w>>1;
      w ^= w>>2;
      w ^= w>>4;
      w ^= w>>8;
      w ^= w>>16;

      return w & 1;
    }

    /**
     * @return max {d1, d2, d3}
     */
    private double maximize(double d1, double d2, double d3)
    {
        return Math.max(Math.max(d1, d2), d3);
    }

    /**
     * @return weight * (the product of the max probability of each of the
     * messages in the List)
     *
     * @param messages a List of ProbTable objects
     * @param weight multiplied by the product of the max probabilities
     */ 
    private double maximize(List messages, double weight)
    {
        double m = weight;

        if(weight <= 0)
        {
            m = 1;
        }

        for(int x=0, n=messages.size(); x < n; x++)
        {
            ProbTable pt = (ProbTable) messages.get(x);
            m *= pt.max();
        }

        return m;
    }

    /**
     * @return weight * Product[max(m) for each m in "messages" except the
     *  message indexed by "skip"]
     *  @param skip the index of the message in the "messages" list to skip
     *  @param messages a list of ProbTables
     *  @param weight weighting factor
     */
    private double maximize(int skip, List messages, double weight)
    {
        double m = weight;

        if(weight <= 0)
        {
            m = 1;
        }

        for(int x=0, n=messages.size(); x < n; x++)
        {
            if(x == skip)  continue;
            
            ProbTable pt = (ProbTable) messages.get(x);
            m *= pt.max();
        }

        return m;
    }

    
}
