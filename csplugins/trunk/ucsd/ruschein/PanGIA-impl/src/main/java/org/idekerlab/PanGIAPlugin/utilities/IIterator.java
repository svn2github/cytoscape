package org.idekerlab.PanGIAPlugin.utilities;

import java.util.Iterator;

public final class IIterator<T> implements Iterable<T> {
	private final Iterator<T> iter;

	public IIterator(Iterator<T> iterator) {
		this.iter = iterator;
	}

	public Iterator<T> iterator() {
		return iter;
	}

	public T next() {
		return iter.next();
	}
}
