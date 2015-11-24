package com.addongaming.hcessentials.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class AtomicArrayList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public synchronized boolean add(T e) {
		return super.add(e);
	}

	@Override
	public synchronized T remove(int index) {
		return super.remove(index);
	}

	@Override
	public synchronized boolean contains(Object o) {
		return super.contains(o);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized AtomicArrayList<T> clone() {
		AtomicArrayList<T> clone = new AtomicArrayList<T>();
		Iterator<T> iter = super.iterator();
		while (iter.hasNext())
			clone.add(iter.next());
		return clone;
	}
}
