package org.idekerlab.PanGIAPlugin;

import org.idekerlab.PanGIAPlugin.networks.*;

public final class ComplexRegressionResult
{
	public final SFNetwork net;
	public final double[] x;
	public final double[] y;
	public final double coef;
	public final double intercept;
	public final int absentHits;
	public final int absentMisses;
	public final double background;
	
	public ComplexRegressionResult(SFNetwork net, double[] x, double[] y, double coef, double intercept, int absentHits, int absentMisses, double background)
	{
		this.net = net;
		this.x = x;
		this.y = y;
		this.coef = coef;
		this.intercept = intercept;
		this.absentHits = absentHits;
		this.absentMisses = absentMisses;
		this.background = background;
	}
}
