package ucsd.trey.SigAttributes;
import java.util.*;
import java.text.DecimalFormat;

// small class to link a function with its pvalue and permit sorting
public class FunAndPval implements Comparable {
    protected String function;
    protected double pvalue;
    protected double x;
    protected double NR;
    protected double NB;
    protected double n;
    
    public FunAndPval (String function, double pvalue, 
		       double x, double NR, double NB, double n) {
	this.function = function;
	this.pvalue = pvalue;
	this.x = x;
	this.NR = NR;
	this.NB = NB;
	this.n = n;
    }
    
    public String getFunction() { return function; }
    
    public double getPvalue() { return pvalue; }
    
    public String toString() {
	DecimalFormat form  = new DecimalFormat("0.000E000");
	DecimalFormat form2 = new DecimalFormat(" ##,###,###");  
	String output = new String ("ID: " + function + "\n");
	output += "  p < " + form.format(pvalue) + "\n";
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
