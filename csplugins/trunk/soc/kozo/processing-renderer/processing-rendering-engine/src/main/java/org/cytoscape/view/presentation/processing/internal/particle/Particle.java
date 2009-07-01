package org.cytoscape.view.presentation.processing.internal.particle;

import java.nio.FloatBuffer;

/**
 * Simple particle for huge graph rednering
 * 
 * @author kono
 *
 */
public class Particle {

	protected float x, y, z;

	public Particle (final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void put(final FloatBuffer buffer, final int index) {
		buffer.put(index, x);
		buffer.put(index + 1, y);
		buffer.put(index + 2, z);
	}
}