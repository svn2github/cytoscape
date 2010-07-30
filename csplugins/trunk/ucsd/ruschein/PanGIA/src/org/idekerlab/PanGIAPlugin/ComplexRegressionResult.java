package org.idekerlab.PanGIAPlugin;

import org.idekerlab.PanGIAPlugin.networks.*;

public class ComplexRegressionResult
{
	public final SFNetwork net;
	public final double[] x;
	public final double[] y;
	public final double coef;
	public final double intercept;
	
	public ComplexRegressionResult(SFNetwork net, double[] x, double[] y, double coef, double intercept)
	{
		this.net = net;
		this.x = x;
		this.y = y;
		this.coef = coef;
		this.intercept = intercept;
	}
}
