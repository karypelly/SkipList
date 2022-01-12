import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class SkipList<E> implements List<E> {
	/**
	* Nested Node class for SkipList Nodes
	* Uses code for initialization from: 
	*	https://opendatastructures.org/ods-java/4_3_SkiplistList_Efficient_.html
	*/
	private static class Node<E> {
		private E element; // the element data for the node
		private Node<E>[] next; // the next node 
		private int index; // index for the node
		private int[] length; // length between nodes
	
		@SuppressWarnings("unchecked")
		private Node(E element, int h, int index) {
			this.element = element; // set element field
			this.next = (Node<E>[])Array.newInstance(Node.class, h+1); // set next field
			this.length = new int[h+1]; // Initialize length between nodes
			this.index = index; // set index field
		}
	}
	
	/**
	* Class for comparing elements in the SkipList. Uses code from: 
	*	https://github.com/zqhxuyuan/tutorials/blob/master/algs/src/main/java/com/github/michalskuza/ods/DefaultComparator.java
	*/
	static class DefaultComparator<E> implements Comparator<E> {
		@SuppressWarnings("unchecked")
		// compare two elements
		public int compare(E a, E b) {
			return ((Comparable<E>)a).compareTo(b);
		}
	}
	
	// Private fields of a SkipList
	private Node<E> head; //head node
	private int height; // height of a node
	private Random r; // random number for calculating height
	private int size; // size of the SkipList
	private Comparator<E> c; // for comparing elements in the SkipList

	// Initializes the SkipList
	public SkipList() {
		this.c = new DefaultComparator<E>(); // for comparing SkipList elements
		this.head = new Node<E>(null, 10, 0); // Initialize the head
		this.height = 0; // set height to 0
		this.r = new Random(0); // Initialize random variable for level
		this.size = 0; // set size to 0
	}
	
	/**
	 * Insert a new node into the SkipList
	 * @param e the element for the new node
	 * @return true
	 * Uses logic from:
	 * 	https://opendatastructures.org/ods-java/4_2_SkiplistSSet_Efficient_.html
	 */
	public boolean add(E e) {
		int comp = 0;
        int index = 0;
        Node<E> currNode = head;
        // traverse through the lowest level of the list to find the appropriate index
        while (currNode.next[0] != null && (comp = c.compare((E) currNode.next[0].element, e)) < 0) {
			index++; // update the index
			Node<E> next = currNode.next[0]; // next node to be updated
			currNode = next; // set current node to next node
		}
        // call add(int index, E element) with specified new index
		add(index, e);
		return true;
	}
	
	/**
	 * Insert a new node into the SkipList
	 * @param index the index of the new node
	 * @param element the element for the new node
	 * Runs in O(logn) time
	 * Uses logic from:
	 * 	https://opendatastructures.org/ods-java/4_2_SkiplistSSet_Efficient_.html
	 * 	https://opendatastructures.org/ods-java/4_3_SkiplistList_Efficient_.html
	 */
	public void add(int index, E element) {
		// check boundaries
		if (index > size() || index < 0) {
			throw new IndexOutOfBoundsException(); 
		}
		// get random level, imitates "flipping a coin"
		int lvl = 0;
		int x = 1;
        while ((r.nextInt() & x) != 0) {
            lvl++;
            x <<= 1;
        }
        // create new node with specified element, level and index
		Node<E> newNode = new Node<E>(element, lvl, index); 
		if (newNode.next.length - 1 > height) {
			height = newNode.next.length - 1;
		}
		Node<E> currNode = head; // start at head
		int currIndex = -1; // index starts at -1
		// loop through levels
		for (int i = height; i >= 0; i--) { 
			// traverse level until element index is greater than parameter index
			while (currNode.next[i] != null && currIndex + currNode.length[i] < index) { 
				int nextIndex = currIndex + currNode.length[i]; // index for the next node
				Node<E> nextNode = currNode.next[i]; // next node to be updated
				currIndex = nextIndex; // set current index to the next index
				currNode = nextNode; // set current node to the next node
			}
			currNode.length[i]++; // increment the length of the current node 
			if (i <= newNode.next.length - 1) {
				Node<E> next = currNode.next[i]; // next node to be updated
				newNode.next[i] = next; // update new nodes next node
				currNode.next[i] = newNode; // update current nodes next node
				int length = currNode.length[i] - (index - currIndex); // length to be updated
				newNode.length[i] = length; // update new nodes length
				currNode.length[i] = index - currIndex; // update current nodes length
			}
		}
		// update the size of the SkipList
		size++;
	}
	
	/**
	 * Remove a new node from the SkipList
	 * @param index the index of the node to be removed
	 * @return the element that is to be removed
	 * Runs in O(logn) time
	 * Uses logic from:
	 * 	https://opendatastructures.org/ods-java/4_3_SkiplistList_Efficient_.html
	 */
	public E remove(int index) {
		// check boundaries
		if (index > size() - 1 || index < 0) {
			throw new IndexOutOfBoundsException(); 
		}
		E element = null; // Initialize return variable
		Node<E> currNode = head; // start at head
		int nodeIndex = -1; // index starts at -1
		// loop through levels
		for (int h = height; h >= 0; h--) {
			// traverse level until element index is greater than parameter index
			while (currNode.next[h] != null && nodeIndex + currNode.length[h] < index) {
				int nextIndex = nodeIndex + currNode.length[h]; // index for the next node
				Node<E> next = currNode.next[h]; // next node to be updated
				nodeIndex = nextIndex; // set current index to the next index
				currNode = next; // set current node to the next node
			}
			currNode.length[h]--; // decrease length for the node removed
			// update the SkipList
			if (nodeIndex + currNode.length[h] + 1 == index && currNode.next[h] != null) {
				E removedElement = (E) currNode.next[h].element; // store removed element
				element = removedElement; // set return variable
				int length = currNode.length[h] + currNode.next[h].length[h]; // update length
				currNode.length[h] = length; // set current nodes length to updated length
				Node<E> next = currNode.next[h].next[h]; // update next node
				currNode.next[h] = next; // set current nodes new next node
				// check if only one element in list
				if (currNode == head && currNode.next[h] == null) {
					height--;
				}
			}
		}
		// decrease the size of the SkipList
		size--;
		// return element
		return element;
	}
	
	/**
	 * Get a node from the SkipList
	 * @param index the index of the node to be gotten
	 * @return E the element gotten
	 * Runs in O(logn) time
	 * Uses logic from:
	 * 	https://opendatastructures.org/ods-java/4_3_SkiplistList_Efficient_.html
	 */
	public E get(int index) {
		// check boundaries
		if (index > size() - 1 || index < 0) {
			throw new IndexOutOfBoundsException(); 
		}
		Node<E> currNode = head; // start at head
		int currIndex = -1; // index starts at -1
		// loop through levels
		for (int h = height; h >= 0; h--) {
			// traverse level until element index is greater than parameter index
			while (currNode.next[h] != null && currIndex + currNode.length[h] < index) {
				int nextIndex = currIndex + currNode.length[h]; // index for the next node
				Node<E> next = currNode.next[h]; // next node to be updated
				currIndex = nextIndex; // set current index to the next index
				currNode = next; // set current node to the next node
			}
			
		}
		// return the node
		E gotten = (E) currNode.next[0].element;
		return (E) gotten;
	}
	
	/**
	 * Get the size of the SkipList
	 * @return the size
	 */
	public int size() {
		return this.size; // return field
	}
	
	/**
	 * Clear the SkipList
	 */
	public void clear() {
		// call remove(int i) for each element in the list
	    for (int i = size() - 1; i >= 0; i--) {
		    remove(i); 
	    }
    }
	
	/**
	 * Return a String representation of the SkipList as outlined by the API
	 * Uses logic from:
	 * 	https://github.com/rohiniy/Skip-List/blob/master/src/skipList/SkipListImpl.java
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("["); // append square bracket to beginning
		Node<E> curr = head.next[0]; // set pointer to current node
		// traverse through lowest level of list
		while (curr != null) {
		    sb.append(curr.element); // append the element
		    curr = curr.next[0]; // update pointer to next node
		    while (curr != null) {
			    sb.append(", " + curr.element); // append comma
			    curr = curr.next[0]; // update pointer to next node
			    }
		    }
		sb.append("]"); // append square bracket to end
		return sb.toString(); // return the list as outlined by API
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) {
		
	}
}
