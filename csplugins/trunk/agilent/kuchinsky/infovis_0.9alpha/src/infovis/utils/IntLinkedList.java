/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.util.*;
import java.util.NoSuchElementException;

import cern.colt.list.AbstractIntList;

/**
 * Class IntLinkedList
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IntLinkedList extends AbstractIntList {
    private int modCount = 0;

    private Entry header = new Entry(0, null, null);

    public IntLinkedList() {
        header.next = header.previous = header;
    }
    
    public void ensureCapacity(int minCapacity) {
    }
    
    public int getQuick(int index) {
        return get(index);
    }
    
    public void setQuick(int index, int element) {
        set(index, element);
    }

    /**
     * Returns the first element in this list.
     * 
     * @return the first element in this list.
     * @throws NoSuchElementException
     *             if this list is empty.
     */
    public int getFirst() {
        if (size == 0)
            throw new NoSuchElementException();

        return header.next.element;
    }

    /**
     * Returns the last element in this list.
     * 
     * @return the last element in this list.
     * @throws NoSuchElementException
     *             if this list is empty.
     */
    public int getLast() {
        if (size == 0)
            throw new NoSuchElementException();

        return header.previous.element;
    }

    /**
     * Removes and returns the first element from this list.
     * 
     * @return the first element from this list.
     * @throws NoSuchElementException
     *             if this list is empty.
     */
    public int removeFirst() {
        int first = header.next.element;
        remove(header.next);
        return first;
    }

    /**
     * Removes and returns the last element from this list.
     * 
     * @return the last element from this list.
     * @throws NoSuchElementException
     *             if this list is empty.
     */
    public int removeLast() {
        int last = header.previous.element;
        remove(header.previous);
        return last;
    }

    /**
     * Inserts the given element at the beginning of this list.
     * 
     * @param o
     *            the element to be inserted at the beginning of this list.
     */
    public void addFirst(int o) {
        addBefore(o, header.next);
    }

    /**
     * Appends the given element to the end of this list. (Identical in function
     * to the <tt>add</tt> method; included only for consistency.)
     * 
     * @param o
     *            the element to be inserted at the end of this list.
     */
    public void addLast(int o) {
        addBefore(o, header);
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one element <tt>e</tt> such that <tt>(o==null ? e==null
     * : o.equals(e))</tt>.
     * 
     * @param o
     *            element whose presence in this list is to be tested.
     * @return <tt>true</tt> if this list contains the specified element.
     */
    public boolean contains(int o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list.
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * @param o
     *            element to be appended to this list.
     */
    public void add(int o) {
        addBefore(o, header);
    }

    /**
     * Removes the first occurrence of the specified element in this list. If
     * the list does not contain the element, it is unchanged. More formally,
     * removes the element with the lowest index <tt>i</tt> such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if such an
     * element exists).
     * 
     * @param o
     *            element to be removed from this list, if present.
     */
    public void delete(int o) {
        for (Entry e = header.next; e != header; e = e.next) {
            if (o == e.element) {
                remove(e);
                return;
            }
        }
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator. The behavior of this operation is undefined if the
     * specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this list, and this list is nonempty.)
     * 
     * @param c
     *            the elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws NullPointerException
     *             if the specified collection is null.
     */
    public boolean addAll(AbstractIntList c) {
        return addAll(size, c);
    }

    /**
     * Inserts all of the elements in the specified collection into this list,
     * starting at the specified position. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (increases
     * their indices). The new elements will appear in the list in the order
     * that they are returned by the specified collection's iterator.
     * 
     * @param index
     *            index at which to insert first element from the specified
     *            collection.
     * @param c
     *            elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws IndexOutOfBoundsException
     *             if the specified index is out of range (
     *             <tt>index &lt; 0 || index &gt; size()</tt>).
     * @throws NullPointerException
     *             if the specified collection is null.
     */
    public boolean addAll(int index, AbstractIntList c) {
        int[] a = c.elements();
        int numNew = a.length;
        if (numNew == 0)
            return false;
        modCount++;

        Entry successor = (index == size ? header : entry(index));
        Entry predecessor = successor.previous;
        for (int i = 0; i < numNew; i++) {
            Entry e = new Entry(a[i], successor, predecessor);
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        size += numNew;
        return true;
    }

    /**
     * Removes all of the elements from this list.
     */
    public void clear() {
        modCount++;
        header.next = header.previous = header;
        size = 0;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of element to return.
     * @return the element at the specified position in this list.
     * 
     * @throws IndexOutOfBoundsException
     *             if the specified index is is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public int get(int index) {
        return entry(index).element;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     * @throws IndexOutOfBoundsException
     *             if the specified index is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public void set(int index, int element) {
        Entry e = entry(index);
        e.element = element;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     * 
     * @param index
     *            index at which the specified element is to be inserted.
     * @param element
     *            element to be inserted.
     * 
     * @throws IndexOutOfBoundsException
     *             if the specified index is out of range (
     *             <tt>index &lt; 0 || index &gt; size()</tt>).
     */
    public void add(int index, int element) {
        addBefore(element, (index == size ? header : entry(index)));
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     * 
     * @param index
     *            the index of the element to removed.
     * @return the element previously at the specified position.
     * 
     * @throws IndexOutOfBoundsException
     *             if the specified index is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public int removeAt(int index) {
        Entry e = entry(index);
        remove(e);
        return e.element;
    }

    /**
     * Return the indexed entry.
     */
    private Entry entry(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index
                    + ", Size: " + size);
        Entry e = header;
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++)
                e = e.next;
        } else {
            for (int i = size; i > index; i--)
                e = e.previous;
        }
        return e;
    }

    // Search Operations

    /**
     * Returns the index in this list of the first occurrence of the specified
     * element, or -1 if the List does not contain this element. More formally,
     * returns the lowest index i such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if there
     * is no such index.
     * 
     * @param o
     *            element to search for.
     * @return the index in this list of the first occurrence of the specified
     *         element, or -1 if the list does not contain this element.
     */
    public int indexOf(int o) {
        int index = 0;
        for (Entry e = header.next; e != header; e = e.next) {
            if (o == e.element)
                return index;
            index++;
        }
        return -1;
    }

    /**
     * Returns the index in this list of the last occurrence of the specified
     * element, or -1 if the list does not contain this element. More formally,
     * returns the highest index i such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if there
     * is no such index.
     * 
     * @param o
     *            element to search for.
     * @return the index in this list of the last occurrence of the specified
     *         element, or -1 if the list does not contain this element.
     */
    public int lastIndexOf(int o) {
        int index = size;
        
        for (Entry e = header.previous; e != header; e = e.previous) {
            index--;
            if (o == e.element)
                return index;
        }
        return -1;
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list. Obeys the
     * general contract of <tt>List.listIterator(int)</tt>.
     * <p>
     * 
     * The list-iterator is <i>fail-fast </i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own <tt>remove</tt> or <tt>add</tt>
     * methods, the list-iterator will throw a
     * <tt>ConcurrentModificationException</tt>. Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     * 
     * @param index
     *            index of first element to be returned from the list-iterator
     *            (by a call to <tt>next</tt>).
     * @return a ListIterator of the elements in this list (in proper sequence),
     *         starting at the specified position in the list.
     * @throws IndexOutOfBoundsException
     *             if index is out of range (
     *             <tt>index &lt; 0 || index &gt; size()</tt>).
     * @see List#listIterator(int)
     */
    public RowIterator listIterator(int index) {
        return new ListItr(index);
    }

    private class ListItr implements RowIterator {
        private Entry lastReturned = header;

        private Entry next;

        private int nextIndex;

        private int expectedModCount = modCount;

        ListItr(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: " + index
                        + ", Size: " + size);
            if (index < (size >> 1)) {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++)
                    next = next.next;
            } else {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--)
                    next = next.previous;
            }
        }
        
        ListItr(ListItr other) {
            lastReturned = other.lastReturned;
            next = other.next;
            nextIndex = other.nextIndex;
            expectedModCount = other.expectedModCount;
        }
        
        public RowIterator copy() {
            return new ListItr(this);
        }

        public boolean hasNext() {
            return nextIndex != size;
        }
        
        public Object next() {
            return new Integer(nextRow());
        }

        public int nextRow() {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }
        
        public int peekRow() {
            return next.element;
        }

        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        public int previous() {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            try {
                IntLinkedList.this.remove(lastReturned);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }
            if (next == lastReturned)
                next = lastReturned.next;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        public void set(int o) {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = o;
        }

        public void add(int o) {
            checkForComodification();
            lastReturned = header;
            addBefore(o, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Entry {
        int element;

        Entry next;

        Entry previous;

        Entry(int element, Entry next, Entry previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    private Entry addBefore(int element, Entry e) {
        Entry ne = new Entry(element, e, e.previous);
        ne.previous.next = ne;
        ne.next.previous = ne;
        size++;
        modCount++;
        return ne;
    }

    private void remove(Entry e) {
        if (e == header) {
            throw new NoSuchElementException();
        }
        e.previous.next = e.next;
        e.next.previous = e.previous;
        size--;
        modCount++;
    }
}