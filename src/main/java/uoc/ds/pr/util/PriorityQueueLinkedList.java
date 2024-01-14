package uoc.ds.pr.util;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.Queue;

import java.util.Comparator;

public class PriorityQueueLinkedList<E> extends LinkedList<E> implements Queue<E> {
    private final Comparator<E> comparator;

    public PriorityQueueLinkedList(Comparator<E> comparator){
        super();
        this.comparator = comparator;
    }

    @Override
    public void add(E e) {
        if (this.last == null) {
            super.insertBeginning(e);
        } else {
            if (comparator.compare(this.last.getElem(),e) > 0) {
                super.insertBefore(this.last, e);
            } else {
                super.insertAfter(this.last, e);
            }
        }
    }

    @Override
    public E poll() {
        return super.deleteFirst();
    }

    @Override
    public E peek() {
        return this.last.getNext().getElem();
    }
}
