/*
 * Author: stmae
 * Created: Monday, October 30, 2006 1:25:40 PM
 * Modified: Monday, October 30, 2006 1:25:40 PM
 */

import java.math.*;
import cern.jet.random.*;
import cern.jet.random.engine.*;

class test_hypergeometric
{
	public static void main(String[] args){
		int x = 10;
        int bigX = 3000;
        int n = 20;
        int bigN = 5000;	
		HyperGeometric distrib = new HyperGeometric(bigN, n, bigX, RandomEngine.makeDefault());	
		double pdfi = distrib.pdf(x)	;
		System.out.println("x"+x+"X"+bigX+"n"+n+"N"+bigN+"p"+pdfi+"\n");
	}
}


 