/*
 * @(#)SpringEmbeddedLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004, Sven Luzar All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package csplugins.layout.jgraphaddons;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;
import cytoscape.layout.*;

import cern.colt.map.*;
import cern.colt.list.*;

/**
 * Arranges the nodes with the Spring Embedded Layout Algorithm.<br>
 *
 * The algorithm takes O(|V|^2 * |E|) time.
 *
 *
 *<br>
 *<br>
 * @author <a href="mailto:Sven.Luzar@web.de">Sven Luzar</a>
 * @since 1.2.2
 * @version 1.0 init
 */
public class SpringEmbeddedLayoutAlgorithm extends AbstractLayout {

	/** Key for an attribute. The value for this key is
	 *  a Rectangle object and specifies the disposement.
	 */
	public static final String SPRING_EMBEDDED_DISP = "SpringEmbeddedDisp";

	/** Key for an attribute. The value for this key is
	 *  a Rectangle object and specifies the calculated position.
	 */
	public static final String SPRING_EMBEDDED_POS = "SpringEmbeddedPos";

  public SpringEmbeddedLayoutAlgorithm ( CyNetworkView view ) {
    super( view );
  }

  protected void initializeLocation() {
    // don't go random
  }

	/**
	 * The implementation of the layout algorithm.
	 *
	 * @see LayoutAlgorithm
	 *
	 */
	public Object construct () {

    initialize();
    int[] nodes = network.getNodeIndicesArray();
    int[] edges = network.getEdgeIndicesArray();


		// Width of the selectionFrame
		double W = getCurrentSize().getWidth();
		// Height of the selectionFrame
		double L = getCurrentSize().getHeight();
		// area of the selectionFrame
		double area = W * L;


    OpenIntDoubleHashMap xdisp = new OpenIntDoubleHashMap();
    OpenIntDoubleHashMap ydisp = new OpenIntDoubleHashMap();

    	
		//---------------------------------------------------------------------------
		// start the iterations
		//---------------------------------------------------------------------------

		// calculate the field length for the area
		double k = Math.sqrt( (area) / nodes.length );

		int iterations = 10;
		lengthOfTask = iterations;
    currentProgress = 0;
    double percent;
    this.currentProgress++;
    percent = (this.currentProgress * 100 )/this.lengthOfTask;
    this.statMessage = "Completed " + percent + "%";


    for (int i = 0; i < iterations; i++) {

      this.currentProgress++;
      percent = (this.currentProgress * 100 )/this.lengthOfTask;
      this.statMessage = "Completed " + percent + "%";

			//---------------------------------------------------------------------------
			// calculate the repulsive forces
			//---------------------------------------------------------------------------

			// calculate the repulsive forces
			for (int vCount = 0; vCount < nodes.length; vCount++) {
				
        double vx = xdisp.get( nodes[vCount] );
        double vy = ydisp.get( nodes[vCount] );

				for (int uCount = 0; uCount < nodes.length; uCount++) {
				
					if ( nodes[uCount] != nodes[vCount] ) {
				
            double ux = networkView.getNodeDoubleProperty( nodes[uCount], CyNetworkView.NODE_X_POSITION );
            double uy = networkView.getNodeDoubleProperty( nodes[uCount], CyNetworkView.NODE_Y_POSITION );

						//Rectangle delta = new Rectangle();
						double deltax = vx - ux;
						double deltay = vy - uy;

						double fr = fr(norm(deltax, deltay), k);
						double deltaNormX = deltax / norm(deltax, deltay);
						double dispX = deltaNormX * fr;
						double deltaNormY = deltay / norm(deltax, deltay);
						double dispY = deltaNormY * fr;

						vx = vx + dispX;
						vy = vy + dispY;

					}
				}
			
        xdisp.put( nodes[vCount], vx );
        ydisp.put( nodes[vCount], vy );
      }

			//---------------------------------------------------------------------------
			// calculate the attractive forces
			//---------------------------------------------------------------------------

			for ( int cellCount = 0; cellCount < edges.length; cellCount++) {
				        
        int v = network.getEdgeSourceIndex( edges[cellCount] );
        int u = network.getEdgeSourceIndex( edges[cellCount] );
        
        if (v == u)
          continue;

				

          double vPx = networkView.getNodeDoubleProperty( v, CyNetworkView.NODE_X_POSITION );
          double vPy = networkView.getNodeDoubleProperty( v, CyNetworkView.NODE_Y_POSITION );

          double uPx = networkView.getNodeDoubleProperty( u, CyNetworkView.NODE_X_POSITION );
          double  uPy = networkView.getNodeDoubleProperty( u, CyNetworkView.NODE_Y_POSITION );


          double vDx = xdisp.get( v );
          double vDy = ydisp.get( v );

          double uDx = xdisp.get( u );
          double uDy = ydisp.get( u );


					// calculate the delta
					
					double deltax = vPx - uPx;
					double deltay = vPy - uPy;

					// calculate the attractive forces
					double fa = fa(norm( deltax, deltay), k);
					double deltaNormX = deltax / norm(deltax, deltay);
					double deltaNormY = deltay / norm(deltax, deltay);
					double dispX = deltaNormX * fa;
					double dispY = deltaNormY * fa;

					vDx = vDx - dispX;
					vDy = vDy - dispY;
					uDx = uDx + dispX;
					uDy = uDy + dispY;

					// store the new values
          xdisp.put( v, vDx );
          ydisp.put( v, vDy );
          xdisp.put( u, uDx );
          ydisp.put( u, uDy );

			}

			//---------------------------------------------------------------------------
			// calculate the new positions
			//---------------------------------------------------------------------------

			// limit the maximum displacement to the temperature buttonText
			// and then prevent from being displacement outside frame
			double t =
				Math.sqrt(W * W + L * L)
					* ((((double) iterations) / ((double) (i + 1)))
						/ ((double) iterations));

			for (int vCount = 0; vCount < nodes.length; vCount++) {
			
        double vPx = networkView.getNodeDoubleProperty( nodes[vCount], CyNetworkView.NODE_X_POSITION );
        double vPy = networkView.getNodeDoubleProperty( nodes[vCount], CyNetworkView.NODE_Y_POSITION );
         double vDx = xdisp.get( nodes[vCount] );
         double vDy = ydisp.get( nodes[vCount] );

				double dispNormX = vDx / norm(vDx, vDy);
				double minX = Math.min(Math.abs(vDx), t);

				double dispNormY = vDy / norm(vDx, vDy);
				double minY = Math.min(Math.abs(vDy), t);

				vPx =  (vPx + dispNormX * minX);
				vPy =  (vPy + dispNormY * minY);

        networkView.setNodeDoubleProperty( nodes[vCount], CyNetworkView.NODE_X_POSITION, vPx );
        networkView.setNodeDoubleProperty( nodes[vCount], CyNetworkView.NODE_Y_POSITION, vPy );
			}
		}

    Iterator nod = networkView.getNodeViewsIterator();
    while ( nod.hasNext() ) {
      ( ( NodeView )nod.next() ).setNodePosition( true );
    }

    return null;
	}

	/** calculates the attractive forces
	 */
	protected double fa(double x, double k) {
		double force = (x * x / k);
		return force;
	}

	/** calculates the repulsive forces
	 */
	protected double fr(double x, double k) {
		double force = (k * k) / x;
		return force;
	}

	/** Calculates the euklidische Norm
	 *  for the point p.
	 *
	 */
	protected double norm( double x, double y) {
    double norm = Math.sqrt(x * x + y * y);
		return norm;
	}

}
