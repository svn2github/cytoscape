package org.idekerlab.PanGIAPlugin.utilities.math.svd;


public class PartialSVD extends SVD
{
	private final double epsilon = 1e-8;
	
	public PartialSVD(double[][] M, int k)
	{
		super(new double[M.length][k],new double[k],null);
		powerMethod(M,k);
	}
	
	private void powerMethod(double[][] M, int scount)
	{
		double[] v = new double[M[0].length];
		double[] w = new double[M[0].length];
		
		//Random guess for w
		for (int j=0;j<M[0].length;j++)
			w[j] = Math.random();
				
		for (int si=0;si<scount;si++)
		{
			//Set v to w
			normalize(w);
			for (int i=0;i<v.length;i++)
				v[i] = w[i];
									
			double diff = Double.MAX_VALUE;
			
			int count = 0;
			while (diff>epsilon)
			{
				double[] newU = new double[M.length];
				
				//Generate newU ~40-50% of the time
				for (int i=0;i<M.length;i++)
				{
					newU[i] = 0;
					for (int j=0;j<M[0].length;j++)
						newU[i] += M[i][j]*v[j];
				}
				
				normalize(newU);
				
				//Calculate convergence
				diff = 0;
				for (int i=0;i<newU.length;i++)
					diff = Math.max(diff, Math.abs(newU[i]-U[i][si]));
				
				//Update u
				for (int i=0;i<newU.length;i++)
					U[i][si] = newU[i];
				
				
				//Update v ~ 50-60% of the time
				for (int j=0;j<v.length;j++)
				{
					double newVj = 0;
					for (int i=0;i<newU.length;i++)
						newVj += newU[i]*M[i][j];
					
					if (diff>10*epsilon) w[j] = v[j]-newVj;
					
					v[j] = newVj;
				}
			
				//Update S
				S[si]= normalize(v);
				
				if (S[si]/S[0]<epsilon) break;
				
				count++;
			}
			//System.out.println(count);
			
			//Update M
			for (int i=0;i<M.length;i++)
				for (int j=0;j<M[0].length;j++)
					M[i][j] = M[i][j]-S[si]*U[i][si]*v[j];
		}
	}
}
