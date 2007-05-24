// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package gpml.util;

import java.awt.geom.Point2D;

/**
 * Helper class for rotation calculations.
 *
 */
public class LinAlg 
{
	
	public static double angle(Point2D p1, Point2D p2) 
	{
		//Angle:
		//					p1.p2	
        //cos(angle) = --------------
        //          	||p1||*||p2||
		double cos = dot(p1,p2) / (len(p1) * len(p2));
		return direction(p1,p2) * Math.acos(cos);
	}
		
	/**
	 * negative: ccw positive: cw
	 */
	public static double direction(Point2D p1, Point2D p2) 
	{
		return Math.signum(p1.getX() * p2.getY() - p1.getY() * p2.getX());
	}
	
	public static double dot(Point2D v1, Point2D v2) 
	{
		double[] d1 = asArray(v1);
		double[] d2 = asArray(v2);
		double sum = 0;
		for(int i = 0; i < Math.min(d1.length, d2.length); i++) sum += d1[i]*d2[i];
		return sum;
	}
	
	public static Point2D project(Point2D p1, Point2D p2) 
	{
		//Projection of p1 on p2:
		// p1.p2
		// ----- . p2
		// p2.p2
		double c = dot(p1, p2) / dot(p2, p2);
		return new Point2D.Double(p2.getX() * c, p2.getY() * c);
	}
	
	public static Point2D rotate(Point2D p, double angle) 
	{
		Point2D pr = new Point2D.Double(0,0);
		pr.setLocation(	p.getX() * Math.cos(angle) + p.getY() * Math.sin(angle),
						-p.getX() * Math.sin(angle) + p.getY() * Math.cos(angle));
		return pr;
	}
	
	public static double len(Point2D p) {
		return Math.sqrt(dot(p, p));
	}
	
	public static double[] asArray(Point2D p) { return new double[] { p.getX(), p.getY() }; }

}
