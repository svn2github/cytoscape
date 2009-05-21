//the purpose of this class is to serve
//statistics regarding zvalues
package csplugins.jActiveModules;

//imported packages
import java.io.Serializable;

/**
 * This code was mostly appropriated from the activeModules C++
 * plugin (ZTable.h and statistics.c), so I can't comment on it too much.
 * the basic idea is that we need some functions that will
 * perform operations related to z values, and they are provided
 * here
 */
public class ZStatistics implements Serializable{

    private double [][] zs;
    private int maxRank, hthsMax, tthsMax, onesMax;
    private int hthsMaxInd, tthsMaxInd, onesMaxInd;
        
    public ZStatistics(int rank){
	//this(rank, 10,50,150);
	this(rank,20,100,300);
    }
    public ZStatistics(int rank, int hundredths, int tenths, int ones){
	maxRank = 0;
	hthsMax = 0;  tthsMax = 0;  onesMax = 0;
	hthsMaxInd=0;  tthsMaxInd=0;  onesMaxInd=0;

	// formatting / error check
	if((rank<=0)||(hundredths<0)||(hundredths>tenths)||(tenths>ones))
	    throw new IllegalArgumentException();


	// store new values
	maxRank = rank;
	hthsMax = hundredths;
	tthsMax = tenths;
	onesMax = ones;
	hthsMaxInd = hundredths*100;
	tthsMaxInd = hthsMaxInd + 10*(tthsMax-hthsMax);
	onesMaxInd = tthsMaxInd + (onesMax - tthsMax);

	// allocate new space
	zs = new double[maxRank][onesMaxInd+1];

	// produce rank adjusted Z scores from an initial Z score and rank (i+1)
	for(double Z=0.00;Z<hthsMax;Z+=0.01)
	    init_z_forall_ranks(Z);
	for(double Z=hthsMax;Z<tthsMax;Z+=0.1)
	    init_z_forall_ranks(Z);
	for(double Z=tthsMax;Z<=onesMax;Z+=1.0)
	    init_z_forall_ranks(Z);
    }



    // adapted from scores.c -  LogPValueForNormalOrderStatistic()

    public void init_z_forall_ranks (double Z) {
	double logzeta = LogOneMinusNormalCDF((double)Z);
	double lnRatio = Math.log(1-Math.exp(logzeta)) - logzeta;
	double lnProb = maxRank * logzeta;
	double pLogSum = lnProb;
    
	for(int rank=1;rank<maxRank;rank++) {
	    zs[(maxRank-rank+1)-1][(int)rAZIndex(Z)] = oneMinusNormalCDFInverseLog(pLogSum);
	    lnProb = lnProb + lnRatio + Math.log(((double)(maxRank - rank + 1)) / ((double)rank));
	    if ((pLogSum - lnProb)>20)
		pLogSum = pLogSum;
	    else
		pLogSum = lnProb + Math.log(1 + Math.exp(pLogSum-lnProb));
	}
	zs[0][(int)rAZIndex(Z)] = oneMinusNormalCDFInverseLog(pLogSum);
    }

    //determine the index in the z table to look up a z value for the 
    //double f
    private double rAZIndex(double f){
	if(f> hthsMax){
	    if(f>tthsMax){
		return f-tthsMax+tthsMaxInd+0.5;
	    }
	    else{
		return (f-hthsMax)*10 + hthsMaxInd+0.5;
	    }
	}
	else{
	    return f*100 + 0.5;
	}
	    
    }
    //determine if a z value for the double f
    //should be located in the z table, or if
    //it is out of bounds, the parameter f
    //should actually represent an index
    private boolean rAZCheck(double f_index){
	if(f_index>onesMaxInd || f_index < 0){
	    return false;
	}
	else{
	    return true;
	}
    }



   

    public double get_adj_z(int numConds, double zScore){
	int f_index = (int)rAZIndex(zScore);
	if(f_index < 0 )
	    throw new RuntimeException("z-score ("+zScore+") produced an f_index (" + f_index + 
	                               ") less than zero.");
	if(f_index > onesMaxInd) {
	    System.err.println("The extremely large z-score (" + zScore + ") generated an f_index ("+
	                       f_index+") that is too large, so using onesMaxInd instead: " + onesMaxInd );
	    f_index = onesMaxInd;
	}
	return zs[numConds-1][f_index];
    }

    //I'm not sure exactly what the following functions are doing,
    //I copied them from statistics.c and the activeModules
    //plugin

    public static double rankAdjustedZ(double rawZ, int n, int m) {
	
	double pval = pValueForNormalOrderStatistic(rawZ, n, n + 1 - m);
	return oneMinusNormalCDFInverse(pval);
	
    }

    public static double rankAdjustedZUsingLog(double rawZ, int n,int m){
	// step 1.
	double projZ = oneMinusNormalCDFInverse(((float)(m)-0.5)/((float)(n)));
	
	// step 2.
	int useOneMinusResult;
	if((rawZ-projZ)>-6)
	    useOneMinusResult=0;
	else
	    useOneMinusResult=1;
	
	// step 3.
	double logPval = LogPValueForNormalOrderStatisticWithFlag(rawZ, n, n + 1 - m,useOneMinusResult);
	return oneMinusNormalCDFInverseLogWithFlag(logPval,useOneMinusResult);
    }
    /*******************************************************************/
    //designed for large x
    //from 26.2.17, page 932, Handbook of Mathematical Functions, NBS, 1964
    //requires x >= 0; as implemented, requires x > 0

    private static double OneMinusNormalCDFForPositive(double x) {
  
	double t, temp;
  
	if (x > 0) {
	    t = 1 / (1 + 0.2316419 * x);
	    temp = Math.exp(-( x * x / 2)) / Math.sqrt(2 * Math.PI);
	    return temp * (0.31938153 * t - 0.356563782 * t*t + 1.781477937 * t*t*t
			   - 1.821255978 * t*t*t*t + 1.330274429 * t*t*t*t*t);
	}
	else {
	    throw new IllegalArgumentException("OneMinusNormalCDFForPositive called with nonpos. argument");
	}
    }


    /*******************************************************************/
    //designed for large x
    //from 26.2.17, page 932, Handbook of Mathematical Functions, NBS, 1964
    //requires Zin >= 0; as implemented, requires Zin > 0
    //now computations done in log form

    private static double LogOneMinusNormalCDFForPositive(double Zin) {
  
	double t, templog, returnlog=0, tprod=1;
  
	if (Zin > 0) {

	    templog = -(Zin * Zin / 2) - 0.5* Math.log(2 * Math.PI);
	    returnlog = templog;

	    t = 1/(1 + 0.2316419 * Zin);
	    // (0.31938153 * t - 0.356563782 * t^2 + 1.781477937 * t^3  - 1.821255978 * t^4 + 1.330274429 * t^5);
	    tprod = 1 - 0.7304159575 * t;
	    tprod = 1 - 1.0223286745 * t * tprod;
	    tprod = 1 - 4.9962391778 * t * tprod;
	    tprod = 1 - 1.1164195437 * t * tprod;
	    tprod = 0.31938153 * t * tprod;
	    returnlog += Math.log(tprod);

	    return (returnlog);

	}
	else {
	    throw new IllegalArgumentException("LogOneMinusNormalCDFForPositive called with nonpositive argument");
	}
    }


    /*******************************************************************/

    public static double oneMinusNormalCDF(double x) {
	if (x > 0)  return OneMinusNormalCDFForPositive(x);
	if (x < 0)  return 1 - OneMinusNormalCDFForPositive(-x);
	if (x == 0) return 0.5;
	return -1; //what else to do?
    }

    /*******************************************************************/

    private static double LogOneMinusNormalCDF(double x) {
	if (x > 0)  return LogOneMinusNormalCDFForPositive(x);
	if (x < 0)  return 1 - Math.exp(LogOneMinusNormalCDFForPositive(-x));
	if (x == 0) return -0.6931472;  // fixed 2002.01.07
	return 1; //what else to do?
    }


    /*******************************************************************/
    //returns the p value for the kth order statistic of an iid sample of
    //n from a standard normal [where k=1 for smallest and k=n for
    //largest] where this p value is the probability that (taking
    //"orderStatistic" as fixed and given) the kth order statistic will
    //exceed the given value for orderStatistic

    private static double pValueForNormalOrderStatistic(double orderStatistic, int n, int k) {

	double zeta, tempSum, lnRatio, prob, lnProb;
	int i;

	zeta = oneMinusNormalCDF(orderStatistic);
	//cout << zeta << endl;
	lnRatio = Math.log((1 - zeta) / zeta);
	lnProb = n * Math.log(zeta);  // the natural logarithm
	prob = Math.pow(zeta,(double)n);
	tempSum = prob;
	for (i = 1; i < k; i++) {
	    lnProb = lnProb + lnRatio + Math.log(((double)(n - i + 1)) / ((double)i));
	    tempSum = tempSum + Math.exp(lnProb);
	    //cout << i << " " << lnProb << " " << lnRatio << " " << n << " " << (n - i + 1) / i << endl;
	}
	return tempSum;
    }


    private static double LogPValueForNormalOrderStatistic(double orderStatistic, int n, int k) {

	double logzeta, tempLogSum, lnRatio, lnProb;
	int i;

	logzeta = LogOneMinusNormalCDF(orderStatistic);
  
	lnRatio = Math.log(1 - Math.exp(logzeta)) - logzeta;
	lnProb = n * logzeta;  // the natural logarithm
	tempLogSum = lnProb;
	for (i = 1; i < k; i++) {
	    lnProb = lnProb + lnRatio + Math.log(((double)(n - i + 1)) / ((double)i));
	    // log (a + b)  =  log(a) + log( 1 + exp( log(b) - log(a))) ;
	    // where b = tempLogSum and  a = the ever-updating lnProb, though it doesn't much matter.
	    if ((tempLogSum - lnProb)>20)
		tempLogSum = tempLogSum;
	    else
		tempLogSum = lnProb + Math.log(1 + Math.exp(tempLogSum-lnProb));
	}
	return tempLogSum;
    }

 
    private static double LogPValueForNormalOrderStatisticWithFlag(double orderStatistic, int n, int k, int OneMinusAnswer) {

	double logzeta, logphi, tempLogSum, lnRatio, lnProb;
	int i;

	if(orderStatistic>0) {
	    logzeta = LogOneMinusNormalCDF(orderStatistic);      // = log((1-phi))
	    logphi = Math.log(1 - Math.exp(logzeta));                      // = log(phi)
	}
	else {
	    logphi = LogOneMinusNormalCDF(-orderStatistic);      // = Math.log(phi)
	    logzeta = Math.log(1 - Math.exp(logphi));                      // = Math.log((1-phi))
	}

	if (OneMinusAnswer != 0) {
	    lnProb = (double)(n) * logphi;                       // = Math.log(phi^n)
	    lnRatio = logzeta - logphi;                          // = Math.log((1-phi)/phi)
	    tempLogSum = lnProb;
	    for(i=n-1; i>=k; i--) {
		lnProb += (lnRatio + Math.log(((double)(n-i+1))/((double)i)));  // lnProb = Math.log( (1-phi)^(n-i) (phi)^(i) (n choose i) )
		if ((tempLogSum - lnProb)>20)
		    tempLogSum = tempLogSum;
		else
		    tempLogSum = lnProb + Math.log(1 + Math.exp(tempLogSum-lnProb));
	    }
	    return tempLogSum;
	}
	else {
	    lnProb = (double)(n) * logzeta;                      // = Math.log((1-phi)^n)
	    lnRatio = logphi - logzeta;                          // = Math.log(phi/(1-phi))
	    tempLogSum = lnProb;
	    for(i=1; i<k; i++){
		lnProb += (lnRatio + Math.log(((double)(n-i+1))/((double)i)));  // lnProb = Math.log( (1-phi)^(n-i) (phi)^(i) (n choose i) )
		if ((tempLogSum - lnProb)>20)
		    tempLogSum = tempLogSum;
		else
		    tempLogSum = lnProb + Math.log(1 + Math.exp(tempLogSum-lnProb));
	    }
	    return tempLogSum;
	}

    }




    /*******************************************************************/
    //from 26.2.23, page 933, Handbook of Mathematical Functions, NBS, 1964
    //Requires 0 < p <= 0.5

    private static double oneMinusNormalCDFInversePLT5(double p) {

	double t, temp;

	if (p < 0) {
	    throw new IllegalArgumentException("oneMinusNormalCDFInversePLT5 called with negative p\n");
	}
	else if (p > 0.5) {
	    throw new IllegalArgumentException("oneMinusNormalCDFInversePLT5 called with p > 0.5\n");
	}
	else {
	    t = Math.sqrt(-2 * Math.log(p));
	    temp = 2.515517 + 0.802853 * t + 0.010328 * t*t;
	    temp = t - temp / (1 + 1.432788 * t + 0.189269 * t*t + 0.001308 * t*t*t);
	    return temp;
	}
    }

    /*******************************************************************/
    //from 26.2.23, page 933, Handbook of Mathematical Functions, NBS, 1964
    //Requires -inf < p <= -0.6931472

    private static double oneMinusNormalCDFInversePLT5Log(double logp) {

	double t, temp;

	if (logp > -0.6931472) {
	    throw new IllegalArgumentException("oneMinusNormalCDFInversePLT5Log called with p > 0.5 (logp > -0.6931472)\n");
	}
	else {
	    t = Math.sqrt(-2 * logp);
	    temp = 2.515517 + 0.802853 * t + 0.010328 * t*t;
	    temp = t - temp / (1 + 1.432788 * t + 0.189269 * t*t + 0.001308 * t*t*t);
	    return temp;
	}
    }

    /*******************************************************************/

    public static double oneMinusNormalCDFInverse(double p) {
	if (p <= 0.5) {
	    if (p > 0) return oneMinusNormalCDFInversePLT5(p);
	    else       return Double.POSITIVE_INFINITY;
	}
	else {
	    if (p < 1) return -oneMinusNormalCDFInversePLT5(1 - p);
	    else       return Double.NEGATIVE_INFINITY;
	}
    }

    /*******************************************************************/

    private static double oneMinusNormalCDFInverseLog(double logp) {
	if (logp <= -0.6931472) {
	    return oneMinusNormalCDFInversePLT5Log(logp);
	}
	else {
	    if (logp < 0) return -oneMinusNormalCDFInversePLT5(1 - Math.exp(logp));
	    else       return Double.NEGATIVE_INFINITY;
	}
    }

    /*******************************************************************/

    private static double oneMinusNormalCDFInverseLogWithFlag(double logp, int OneMinusP) {
	if ((logp < -0.693147)&&(logp > -0.693148)) {
	    //cout << "boundary" << endl;
	    logp = -0.6931472;
	}
	if (logp <= -0.6931472) {
	    if(OneMinusP == 0)
		return oneMinusNormalCDFInversePLT5Log(logp);
	    else
		return -oneMinusNormalCDFInversePLT5Log(logp);
	}
	else {
	    if (logp < 0) {
		if(OneMinusP == 0)
		    return -oneMinusNormalCDFInversePLT5(1 - Math.exp(logp));
		else
		    return oneMinusNormalCDFInversePLT5(1 - Math.exp(logp));
	    }
	    else       return Double.NEGATIVE_INFINITY;
	}
    }

    /*******************************************************************/

    private static double normalCDFInverse(double p) {
	return -oneMinusNormalCDFInverse(p);
    }

    /*******************************************************************/
   

}
