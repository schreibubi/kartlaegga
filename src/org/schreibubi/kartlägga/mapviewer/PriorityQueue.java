/**
 * Copyright (C) 2009 joerg <schreibubi@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.schreibubi.kartlägga.mapviewer;

import org.schreibubi.kartlägga.extend.Comparator;

/**
 * PriorityQueue holds elements on a priority heap, which orders elements
 * according to the comparator specified at construction or their natural order.
 * If the queue uses natural order, any element that is not comparable is not
 * permitted to insert to the queue.
 * 
 * The least element of the specified ordering is stored at the head of the
 * queue and the greatest element is stored at the tail of the queue.
 * 
 * PriorityQueue is not synchronized. If multiple threads will access it
 * concurrently, use the PriorityBlockingQueue.
 */
public class PriorityQueue {

	private static final int DEFAULT_CAPACITY_RATIO = 2;

	private int size;

	private Comparator comparator;

	private transient Object[] elements;

	/**
	 * Constructs a priority queue with specified capacity and comparator.
	 * 
	 * @param initialCapacity
	 *            the specified capacity.
	 * @param comparator
	 *            the specified comparator. If it is null, the natural ordering
	 *            will be used.
	 * @throws IllegalArgumentException
	 *             if the initialCapacity is less than 1
	 */
	public PriorityQueue(int initialCapacity, Comparator comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		elements = newElementArray(initialCapacity);
		this.comparator = comparator;
	}

	/**
	 * Gets the size of the priority queue. If the size of the queue is greater
	 * than the Integer.MAX, then it returns Integer.MAX.
	 * 
	 * @return the size of the priority queue.
	 */
	public int size() {
		return size;
	}

	/**
	 * Removes all the elements of the priority queue.
	 */
	public void clear() {
		synchronized (elements) {
			for (int i = 0; i < size; i++)
				elements[i] = null;
			size = 0;
		}
	}

	/**
	 * Inserts the element to the priority queue.
	 * 
	 * @return true
	 * @throws ClassCastException
	 *             if the element cannot be compared with the elements in the
	 *             priority queue using the ordering of the priority queue.
	 * @throws NullPointerException
	 *             if the element is null.
	 */
	public boolean add(Object o) {
		if (null == o) {
			throw new NullPointerException();
		}
		synchronized (elements) {
			growToSize(size + 1);
			elements[size] = o;
			siftUp(size++);
			elements.notify();
		}
		return true;
	}

	/**
	 * Gets and removes the head of the queue.
	 * 
	 * @return the head of the queue. Null if the queue is empty.
	 */
	public Object poll() {
		Object result;
		synchronized (elements) {
			try {
				while (size == 0)
					elements.wait();
			} catch (InterruptedException e) {
			}
			result = elements[0];
			removeAt(0);
		}
		return result;
	}

	public boolean isEmpty() {
		synchronized (elements) {
			return size == 0;
		}
	}

	/**
	 * Removes the specified object of the priority queue.
	 * 
	 * @param o
	 *            the object to be removed.
	 * @return true if the object is in the priority queue, false if the object
	 *         is not in the priority queue.
	 */
	public boolean remove(Object o) {
		if (o == null) {
			return false;
		}
		synchronized (elements) {
			int targetIndex;
			for (targetIndex = 0; targetIndex < size; targetIndex++) {
				if (0 == this.compare(o, elements[targetIndex])) {
					break;
				}
			}
			if (size == 0 || size == targetIndex) {
				return false;
			}
			removeAt(targetIndex);
		}
		return true;
	}

	private Object[] newElementArray(int capacity) {
		return new Object[capacity];
	}

	private void removeAt(int index) {
		size--;
		elements[index] = elements[size];
		siftDown(index);
		elements[size] = null;
	}

	private int compare(Object o1, Object o2) {
		if (null != comparator) {
			return comparator.compare(o1, o2);
		}
		return ((org.schreibubi.kartlägga.extend.Comparable) o1).compareTo(o2);
	}

	private void siftUp(int childIndex) {
		Object target = elements[childIndex];
		int parentIndex;
		while (childIndex > 0) {
			parentIndex = (childIndex - 1) / 2;
			Object parent = elements[parentIndex];
			if (compare(parent, target) <= 0) {
				break;
			}
			elements[childIndex] = parent;
			childIndex = parentIndex;
		}
		elements[childIndex] = target;
	}

	private void siftDown(int rootIndex) {
		Object target = elements[rootIndex];
		int childIndex;
		while ((childIndex = rootIndex * 2 + 1) < size) {
			if (childIndex + 1 < size
					&& compare(elements[childIndex + 1], elements[childIndex]) < 0) {
				childIndex++;
			}
			if (compare(target, elements[childIndex]) <= 0) {
				break;
			}
			elements[rootIndex] = elements[childIndex];
			rootIndex = childIndex;
		}
		elements[rootIndex] = target;
	}

	private void growToSize(int size) {
		if (size > elements.length) {
			Object[] newElements = newElementArray(size * DEFAULT_CAPACITY_RATIO);
			System.arraycopy(elements, 0, newElements, 0, elements.length);
			elements = newElements;
		}
	}
}
