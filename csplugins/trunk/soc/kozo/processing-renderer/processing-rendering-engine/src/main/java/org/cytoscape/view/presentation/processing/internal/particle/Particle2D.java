package org.cytoscape.view.presentation.processing.internal.particle;

import java.nio.FloatBuffer;

public class Particle2D {

	float posX, posY;

	public Particle2D(int _id, float _posX, float _posY) {
		posX = _posX;
		posY = _posY;
	}

	public void writeToBuffer(FloatBuffer _buffer, int index) {
		_buffer.put(index, posX);
		_buffer.put(index + 1, posY);
	}
}
