package ucsd.trey.SigAttributes;
import java.util.*;
import java.text.DecimalFormat;

// small class to link a function with its pvalue and permit sorting
public class FunAndPval implements Comparable {
    protected String function;
    protected double pvalue;
    protected double pvalCorrected;
    protected double x;
    protected double NR;
    protected double NB;
    protected double n;
    
    public FunAndPval (String function, double pvalue, double pvalCorrected,
		       double x, double NR, double NB, double n) {
	this.function = function;
	this.pvalue = pvalue;
	this.pvalCorrected = pvalCorrected;
	this.x = x;
	this.NR = NR;
	this.NB = NB;
	this.n = n;
    }
    
    public String getFunction() { return function; }

    public String getFunctionAndPval() { 
	DecimalFormat form  = new DecimalFormat("0.000E000");
	return new String (function + "; (" + form.format(pvalCorrected) + ")"); 
    }
    
    public double getPvalue() { 
	//return pvalue; 
	return pvalCorrected;
    }
    
    public String toString() {
	DecimalFormat form  = new DecimalFormat("0.000E000");
	DecimalFormat form2 = new DecimalFormat(" ##,###,###");  
	String output = new String ("ID: " + function + "\n");
	output += "  p < " + form.format(pvalue) + ";  " 
	    + form.format(pvalCorrected) + " (corrected)\n";
	output += "  x = " + form2.format(x) + ";  n = " + form2.format(n) + "\n";
	output += "  nr= " + form2.format(NR)+ ";  t = " + form2.format(NB+NR) + "\n";
	return output;
    }
    
    public int compareTo(Object o) {
	FunAndPval other = (FunAndPval) o;
	if (this.pvalue > other.pvalue) return 1;
	if (this.pvalue < other.pvalue) return -1;
	else return 0;
    }
}
