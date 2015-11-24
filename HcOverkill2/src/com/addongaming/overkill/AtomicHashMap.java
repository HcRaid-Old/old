package com.addongaming.overkill;

import java.util.HashMap;
import java.util.Iterator;

public class AtomicHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	@Override
	public synchronized V put(K k, V v) {
		return super.put(k, v);
	}

	@Override
	public synchronized V remove(Object k) {
		return super.remove(k);
	}

	@Override
	public synchronized boolean containsKey(Object k) {
		return super.containsKey(k);
	}

	@Override
	public synchronized boolean containsValue(Object v) {
		return super.containsValue(v);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized AtomicHashMap<K, V> clone() {
		AtomicHashMap<K, V> clone = new AtomicHashMap<K, V>();
		Iterator<K> iter = super.keySet().iterator();
		while (iter.hasNext()) {
			K next = iter.next();
			clone.put(next, super.get(next));
		}
		return clone;
	}
}