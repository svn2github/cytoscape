import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.lang.Math;

public class PathFactorNode extends FactorNode
{
    private static Logger logger = Logger.getLogger(PathFactorNode.class.getName());


    private static boolean FINE = logger.isLoggable(Level.FINE);
    
    protected static final double ep1 = 0.7; // epsilon 1
    protected static final double ep2 = 0.299; // epsilon 2
    protected static final double ep3 = 0.001; // epsilon 2

    private static final int ODD = 1; // product of signs == -1
    private static final int EVEN = 0; // product of sings == +1

    private static PathFactorNode __singleton = new PathFactorNode();
    
    public static PathFactorNode getInstance()
    {
        return __singleton;
    }

    protected PathFactorNode()
    {
        super(NodeType.PATH_FACTOR);
    }

    private ProbTable[] makePTArray(List items)
    {
        ProbTable[] pt = new ProbTable[items.size()];
        for(int x=0; x < items.size(); x++)
        {
            pt[x] = (ProbTable) items.get(x);
        }
        return pt;
    }
    
    /**
     * @param allMsgs a list of EdgeMessage objects.
     * @param tIndex the index of the EdgeMessage in the allMsgs list
     * that corresponds to messages passed from the factor node to the target 
     * @param target the target of the message
     */
    public ProbTable maxProduct(List allMsgs, int tIndex)
        throws AlgorithmException
    {
        //List incomingList = new ArrayList();
        List xList = new ArrayList();
        List dList = new ArrayList();
        List dirStatesList = new ArrayList();
        List sList = new ArrayList();
        ProbTable sigma = null;
        ProbTable k = null;

        // organize the incoming messages
        int numSig = 0;
        int numK = 0;
        NodeType tt = null;

        
        for(int m=0, N = allMsgs.size(); m < N; m++)
        {
            EdgeMessage em = (EdgeMessage) allMsgs.get(m);
            
            if(m == tIndex)
            {
                tt = em.getVariableType();
                continue;
            }

            ProbTable p = em.v2f();
            //incomingList.add(p);
            
            StateSet si = p.stateSet();
            if( si == StateSet.EDGE)
            {
                xList.add(p);
            }
            else if (si == StateSet.DIR)
            {
                dirStatesList.add(em.getDir());
                dList.add(p);
            }
            else if (si == StateSet.SIGN)
            {
                sList.add(p);
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
                logger.severe("Unexpected StateSet encountered: " + si);
            }
        }

        //ProbTable[] incoming = (ProbTable[]) incomingList.toArray();
        ProbTable[] x = makePTArray(xList);

        ProbTable[] d = makePTArray(dList);
        ProbTable[] s = makePTArray(sList);

        State[] dirStates = new State[dirStatesList.size()];
        for(int i=0; i < dirStatesList.size(); i++)
        {
            dirStates[i] = (State) dirStatesList.get(i);
        }
        
        if(numSig > 1 || numK > 1)
        {
            throw new AlgorithmException("> 1 message received from path-active or KO node");
        }
        
        if(tt == NodeType.EDGE)
        {
            StateSet ss = StateSet.EDGE;
            ProbTable pt = new ProbTable(ss);

            //double pathExplains = computePathExplains(x, d, dirStates, k, sigma, s);
            //double pathInactive = ep1 * sigma.prob(State.ZERO) * unconstrained;
            //double pathViolates = ep2 * sigma.prob(State.ONE) * unconstrained;

            double pathExplains = ep1*computeExplains_XDKSigmaS(x, d, dirStates, k, sigma, s);
            double maxDSX = maximize(x) * maximize(d) * maximize(s);
            double pathUnconstrained = computePathUnconstrained(maxDSX, k, sigma);
            double pathNotExplains = computePathNotExplains(maxDSX, k, sigma);
            
            double[] probs = new double[ss.size()];

            probs[ss.getIndex(State.ZERO)] = Math.max(pathUnconstrained, pathNotExplains);
            probs[ss.getIndex(State.ONE)] = maximize(pathExplains, 
                                                     pathUnconstrained, 
                                                     pathNotExplains);
            pt.init(probs);

            if(FINE)
            {
                logger.fine("P(0)=" + probs[ss.getIndex(State.ZERO)]);
                logger.fine("P(1)=" + probs[ss.getIndex(State.ONE)]);
                logger.fine("pe=" + pathExplains);
                logger.fine("pu=" + pathUnconstrained);
                logger.fine("pne=" + pathNotExplains);
            }
            
            return pt;
        }
        else if (tt == NodeType.DIR)
        {
            StateSet ss = StateSet.DIR;
            ProbTable pt = new ProbTable(ss);

            // find the state of DIR that is consistent with the path being explanatory
            State explanatory = ((EdgeMessage) allMsgs.get(tIndex)).getDir();

            // if explanatory is PLUS, the other state is MINUS, and vice versa.
            State other = (explanatory == State.PLUS ? State.MINUS : State.PLUS);
            

            double pathExplains = ep1*computeExplains_XDKSigmaS(x, d, dirStates, k, sigma, s);
            double maxDSX = maximize(x) * maximize(d) * maximize(s);
            double pathUnconstrained = computePathUnconstrained(maxDSX, k, sigma);
            double pathNotExplains = computePathNotExplains(maxDSX, k, sigma);
            
            double[] probs = new double[ss.size()];

            probs[ss.getIndex(other)] = Math.max(pathUnconstrained, pathNotExplains);
            probs[ss.getIndex(explanatory)] = maximize(pathExplains, 
                                                       pathUnconstrained, 
                                                       pathNotExplains);
            pt.init(probs);

            if(FINE)
            {
                logger.fine("P(" + other + ") other=" + probs[ss.getIndex(other)]);
                logger.fine("P(" + explanatory + ") explanatory=" + probs[ss.getIndex(explanatory)]);
                logger.fine("pe=" + pathExplains);
                logger.fine("pu=" + pathUnconstrained);
                logger.fine("pne=" + pathNotExplains);
            }

            
            return pt;
        }
        else if (tt == NodeType.SIGN)
        {
            StateSet ss = StateSet.SIGN;
            ProbTable pt = new ProbTable(ss);

            double pathExplainsMinus = ep1*computeExplains_FixSign(x, d, dirStates,
                                                                  k, sigma, 
                                                                  s, State.MINUS);

            double pathExplainsPlus = ep1*computeExplains_FixSign(x, d, dirStates,
                                                                 k, sigma, 
                                                                 s, State.PLUS);

            double maxDSX = maximize(x) * maximize(d) * maximize(s);
            double pathUnconstrained = computePathUnconstrained(maxDSX, k, sigma);
            double pathNotExplains = computePathNotExplains(maxDSX, k, sigma);
            
            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.MINUS)] = maximize(pathExplainsMinus, 
                                                      pathUnconstrained, 
                                                      pathNotExplains);

            probs[ss.getIndex(State.PLUS)] = maximize(pathExplainsPlus,
                                                      pathUnconstrained, 
                                                      pathNotExplains);
                                                      
            pt.init(probs);

            if(FINE)
            {
                logger.fine("pe+ =" + pathExplainsPlus);
                logger.fine("pe- =" + pathExplainsMinus);
                logger.fine("pu=" + pathUnconstrained);
                logger.fine("pne=" + pathNotExplains);
            }
            
            return pt;
        }
        else if (tt == NodeType.PATH_ACTIVE)
        {
            StateSet ss = StateSet.PATH_ACTIVE;
            ProbTable pt = new ProbTable(ss);

            double pathExplains = ep1*computeExplains_XDKS(x, d, dirStates, k, s);
            double maxDSX = maximize(x) * maximize(d) * maximize(s);
            double pathUnconstrained = ep2 * k.prob(State.ZERO) * maxDSX;

            double[] probs = new double[ss.size()];

            probs[ss.getIndex(State.ZERO)] = Math.max(pathUnconstrained,
                                                      ep2 * k.max() * maxDSX);
            
            probs[ss.getIndex(State.ONE)] = maximize(pathExplains,
                                                     pathUnconstrained,
                                                     ep3 * k.max() * maxDSX);
 
            pt.init(probs);

            if(FINE)
            {
                logger.fine("P(0)=" + probs[ss.getIndex(State.ZERO)]);
                logger.fine("P(1)=" + probs[ss.getIndex(State.ONE)]);
                logger.fine("pe=" + pathExplains);
                logger.fine("pu=" + pathUnconstrained);
            }
            
            return pt;
        }
        else if (tt == NodeType.KO)
        {
            StateSet ss = StateSet.KO;
            ProbTable pt = new ProbTable(ss);

            double pathExplainsPlus = ep1*computeExplains_FixKO(x, d, dirStates,
                                                                sigma, 
                                                                s, State.PLUS);

            double pathExplainsMinus = ep1*computeExplains_FixKO(x, d, dirStates,
                                                                 sigma, 
                                                                 s, State.MINUS);

            double maxDSX = maximize(x) * maximize(d) * maximize(s);
            double pathNotExplains = Math.max(ep2 * sigma.prob(State.ZERO) * maxDSX,
                                              ep3 * sigma.prob(State.ONE) * maxDSX);
            

            double[] probs = new double[ss.size()];
            probs[ss.getIndex(State.ZERO)] = ep2 * maxDSX * sigma.max();
            probs[ss.getIndex(State.PLUS)] = Math.max(pathExplainsPlus, pathNotExplains); 
            probs[ss.getIndex(State.MINUS)] = Math.max(pathExplainsMinus, pathNotExplains); 
 
            pt.init(probs);

            if(FINE)
            {
                logger.fine("P(0)=" + probs[ss.getIndex(State.ZERO)]);
                logger.fine("pe+ =" + pathExplainsPlus);
                logger.fine("pe- =" + pathExplainsMinus);
                logger.fine("pne=" + pathNotExplains);
            }
            
            return pt;
        }
        else
        {
            logger.severe("Unknown target variable type: " + tt);
            return null;
        }
    }

    protected double computePathUnconstrained(double maxDSX,
                                              ProbTable k, 
                                              ProbTable sigma)
    {
        if(k.maxState() == State.ZERO)
        {
            return ep2 * sigma.max() * maxDSX;
        }
        else
        {
            return ep2 * k.prob(State.ZERO) * sigma.max() * maxDSX;
        }
    }

    
    protected double computePathNotExplains(double maxDSX,
                                            ProbTable k, ProbTable sigma)
    {
        double pathNotExplains = 0;
        
        if(k.maxState() == State.ZERO)
        {
            /*
            logger.fine("kmax is zero: 0=" + k.prob(State.ZERO) + " +=" + k.prob(State.PLUS)
                        + " -=" + k.prob(State.MINUS)
                        + " unique=" + k.hasUniqueMax());
            */
            pathNotExplains = 0;
        }
        else
        {
            /*
            logger.fine("kmax is: " + k.max() + " 0=" + k.prob(State.ZERO) + " +=" + k.prob(State.PLUS)
                        + " -=" + k.prob(State.MINUS)
                        + " maxState=" + k.maxState()
                        + " unique=" + k.hasUniqueMax());
            */
            
            pathNotExplains = Math.max(ep2 * sigma.prob(State.ZERO) * maxDSX * k.max(),
                                       ep3 * sigma.prob(State.ONE) * maxDSX * k.max());
        }

        return pathNotExplains;
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
    protected double computeExplains_FixKO(ProbTable[] x, ProbTable[] d,
                                           State[] dirStates,
                                           ProbTable sigma, 
                                           ProbTable[] s, State fixed)
    {
        if((sigma.maxState() == State.ZERO) && (sigma.max() == 0))
        {
            return 0;
            
        }
        
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
     * @param fixedSign State.PLUS | State.MINUS that the target sign variable
     *  will be fixed to.
     */
    protected double computeExplains_FixSign(ProbTable[] x, ProbTable[] d,
                                             State[] dirStates,
                                             ProbTable k, 
                                             ProbTable sigma, 
                                             ProbTable[] s, State fixedSign)
    {
        if(k.maxState() == State.ZERO)
        {
            return 0;
        }

        if((sigma.maxState() == State.ZERO) && (sigma.max() == 0))
        {
            return 0;
            
        }
        
        
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
    protected double computeExplains_XDKSigmaS(ProbTable[] x, ProbTable[] d,
                                               State[] dirStates,
                                               ProbTable k, ProbTable sigma, 
                                               ProbTable[] s)
    {
        if(k.maxState() == State.ZERO)
        {
            /*
            logger.fine("kmax is zero: 0=" + k.prob(State.ZERO) + " +=" + k.prob(State.PLUS)
                        + " -=" + k.prob(State.MINUS)
                        + " unique=" + k.hasUniqueMax());
            */
            return 0;
        }

        if((sigma.maxState() == State.ZERO) && (sigma.max() == 0))
        {
            //logger.fine("sigma is zero");
            return 0;
            
        }
                
        double m = 1;

        // sigma must be 1
        m *= sigma.prob(State.ONE);

        m *= computeExplains_XDKS(x, d, dirStates, k, s);

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
    protected double computeExplains_XDKS(ProbTable[] x, ProbTable[] d,
                                          State[] dirStates,
                                          ProbTable k, ProbTable[] s)
    {
        if(k.maxState() == State.ZERO)
        {
            return 0;
        }

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
    protected double computeProbDir(ProbTable[] d, State[] dirStates)
    {
        double m = 1;

        for(int y=0, n=d.length; y < n; y++)
        {
            m *= d[y].prob( dirStates[y] );
        }
        return m;
    }
    
    
    /**
     * @return the probabilites of all of the vars in the input List
     * for a fixed state "fixed"
     *
     * @param fixed the state
     */
    protected double computeProbFixState(ProbTable[] p, State fixed)
    {
        double m = 1;
        for(int y=0, n=p.length; y < n; y++)
        {
            m *= p[y].prob(fixed);
        }
        return m;
    }

    protected double maximizeSign(ProbTable[] signs, ProbTable k)
    {
        return maximizeSign(signs, k, null);
    }
    
    /**
     * @param signs a list of ProbTables of sign variables.
     * Does not include the fixed sign. 
     * @param k the ProbTable of the knockout node
     * @param fixedSign If we should compute the max under the condition that
     * one of the sign vars (not in the input list) is fixed to a specific
     * state: either State.PLUS or State.MINUS.  Or null if no signs are
     * externally fixed.
     */
    protected double maximizeSign(ProbTable[] signs, ProbTable k,
                                  State fixedSign)
    {
        int numSign = signs.length;

        // numSign == 0 only if a factor node is connected to one
        // sign variable and that sign variable is the target.
        // Hence, its max value is determined by k and fixedSign.
        if(! (numSign > 0))
        {
            if(fixedSign == State.PLUS)
            {
                return k.prob(State.MINUS);
            }
            else if(fixedSign == State.MINUS)
            {
                return k.prob(State.PLUS);
            }

            return 1;
        }
        
        // Generate all combinations of valid signs given
        // the number of sign variables
        short[] plusCombos;
        short[] minusCombos;

        /* If one of the signs is fixed to -1, then the product
         * of all signs will be -1 * product(other signs).
         * Hence, the swap plusCombos and minusCombos.
         *
         * Fixing one of the signs to +1 does not change the set
         * of valid combos
         */
        if(fixedSign == State.MINUS)
        {
            plusCombos = enumerate(numSign, State.MINUS);
            minusCombos = enumerate(numSign, State.PLUS);;
        }
        else
        {
            plusCombos = enumerate(numSign, State.PLUS);
            minusCombos = enumerate(numSign, State.MINUS);
        }
        
        // Copy sign probabilities into an array for better performance
        double[] probPlus = new double[numSign];
        double[] probMinus = new double[numSign];
        for(int x=0, n=numSign; x < n; x++)
        {
            ProbTable pt = signs[x];
            probPlus[x] = pt.prob(State.PLUS);
            probMinus[x] = pt.prob(State.MINUS);
        }
                
        // compute the probability of each combo of signs

        double maxPlus = _maxComboProb(k.prob(State.PLUS),
                                       plusCombos, numSign,
                                       probPlus, probMinus);

        double maxMinus = _maxComboProb(k.prob(State.MINUS),
                                        minusCombos, numSign,
                                        probPlus, probMinus);

        // return the max value.
        return Math.max(maxPlus, maxMinus);
    }

    
    /**
     * @throws IllegalArgumentException if pORm is not State.PLUS
     *                                  or State.MINUS
     */
    protected double maximizeSign(ProbTable[] signs, State pORm)
    {
        int numSign = signs.length;
        
        if(pORm != State.PLUS && pORm != State.MINUS)
        {
            throw new IllegalArgumentException(pORm + " is not PLUS or MINUS");
        }

        if(! (signs.length > 0))
        {
            return 1;
        }
        
        // Generate all combinations of valid signs given
        // the state pORm
        short[] validCombos = enumerate(numSign, pORm);

        // Copy sign probabilities into an array for better performance
        double[] probPlus = new double[numSign];
        double[] probMinus = new double[numSign];
        for(int x=0, n=numSign; x < n; x++)
        {
            ProbTable pt = signs[x];
            probPlus[x] = pt.prob(State.PLUS);
            probMinus[x] = pt.prob(State.MINUS);
        }
        
        double max = _maxComboProb(1, validCombos, numSign,
                                   probPlus, probMinus);
        return max;
    }


    /**
     * Compute the probability of each on the combos and return
     * the max.
     * <p>
     * The length of the probPlus and probMinus must be equal to the number
     * of bits in each combo.
     * 
     * @param initialProb The initial probability of each combo
     * @param combos the combiniations
     * @param probPlus the probability vector for the sign variables.
     *        The i-th element is the probability that i-th sign is PLUS.
     * @param probMinus the probability vector for the sign variables.
     *        The i-th element is the probability that i-th sign is MINUS.
     *        
     * @return the probability of the most likely combination of signs
     */
    private double _maxComboProb(double initialProb, short[] combos,
                                 int numSign,
                                 double[] probPlus, double[] probMinus)
    {
        double[] vals = new double[combos.length];
        Arrays.fill(vals, initialProb);
        int i = 0;
        
        // compute the probability of each sign combo
        for(int c=0; c < combos.length; c++)
        {
            //logger.fine("combo: " + combos[c]);
            
            // if the i-th bit in a combo is 1, then multiply the c-th
            // val by the i-th minus probability.  If the bit is 0, multiply
            // the i-th val by the i-th plus probability
            int combo = combos[c];
            for(i=0; i < numSign; i++)
            {
                if((0x1 & combo) == 1)
                {
                    vals[c] *= probMinus[i];
                    //logger.fine("  bit=" + i + " => "
                    //+ (0x1 &combo) + " P(minus)=" + probMinus[i]);
                                
                }
                else
                {
                    vals[c] *= probPlus[i];
                    //logger.fine("  bit=" + i + " => "
                    //+ (0x1 &combo) + " P(plus)=" + probPlus[i]);
                }

                combo = combo>>1;
            }
        }
        
        // Sort values into ascending numerical order
        Arrays.sort(vals);
        
        // return the max
        return vals[vals.length - 1];
    }

    
    /**
     * @param numSigns the number of sign variables.
     *                 Assumption: numSigns < 16.
     * @param pORm either State.PLUS or State.MINUS
     *
     * @return an array of 16 bit numbers (type short) that represent 
     * configurations of sign variables.
     * <p>
     * Each short is one configuration of sign variables.
     * Bit 0 (the least-significant bit) holds the state of sign 0.
     * Bit 1 holds the state of sign 1.  Bit 2 represents sign 2, and so on.
     *  <p>
     * If a bit is 1, the state if the sign variable is -1 in the
     * configuration.  If a bit bit is 0, the state of the is +1
     * in the configuration.
     * <p>
     * Each configuration satisfies the constraint
     * that the product of signs is -1 if "pORm" == PLUS,
     * or +1 if "pORm" == MINUS.
     */
    protected short[] enumerate(int numSigns, State pORm)
    {
        short numCombos = (short) Math.pow(2, numSigns - 1);
        short[] combos = new short[numCombos];

        int par;

        if(pORm == State.PLUS)
        {
            par = ODD;
        }
        else
        {
            par = EVEN;
        }

        for(short x=0, n=(short) (2*numCombos), i=0; x < n; x++)
        {
            if(parity(x) == par)
            {
                combos[i] = x;
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
     * @return the product of the max probability of each message in the List
     *
     * @param messages a List of ProbTable objects
     */ 
    private double maximize(ProbTable[] messages)
    {
        return maximize(messages, 1);
    }

    
    /**
     * @return weight * (the product of the max probability of each of the
     * messages in the List)
     *
     * @param messages a List of ProbTable objects
     * @param weight multiplied by the product of the max probabilities
     */ 
    private double maximize(ProbTable[] messages, double weight)
    {
        double m = weight;

        if(weight <= 0)
        {
            m = 1;
        }

        for(int x=0, n=messages.length; x < n; x++)
        {
            m *= messages[x].max();
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
    private double maximize(int skip, ProbTable[] messages, double weight)
    {
        double m = weight;

        if(weight <= 0)
        {
            m = 1;
        }

        for(int x=0, n=messages.length; x < n; x++)
        {
            if(x == skip)  continue;
            
            m *= messages[x].max();
        }

        return m;
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
     *
    protected short[][] enumerate(int numSigns)
    {
        short numCombos = (short) Math.pow(2, numSigns - 1);

        short[][] combos = new short[2][numCombos];
        short[] plusCombos = combos[0];
        short[] minusCombos = combos[1];

        for(short x=0, n=(short) (2*numCombos), p=0, m=0; x < n; x++)
        {
            int par = parity(x);
            if(par == ODD)
            {
                
                logger.finest("adding odd  combo: x=" + x + 
                                   " p=" + p + " n=" + n +
                                   " parity=" + parity(x));
                
                plusCombos[p] = x;
                p++;
                
            }
            else
            {
                logger.finest("adding even combo: x=" + x + 
                                   " m=" + m + " n=" + n +
                                   " parity=" + parity(x));
                
                minusCombos[m] = x;
                m++;
            
            }
        }
        
        return combos;
    }
    */

    
}
