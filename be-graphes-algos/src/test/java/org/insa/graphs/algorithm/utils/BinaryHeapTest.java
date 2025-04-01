package org.insa.graphs.algorithm.utils;

public class BinaryHeapTest extends PriorityQueueTest {

    
    /** 
     * @return PriorityQueue<MutableInteger>
     */
    @Override
    public PriorityQueue<MutableInteger> createQueue() {
        return new BinaryHeap<>();
    }

    @Override
    public PriorityQueue<MutableInteger> createQueue(
            PriorityQueue<MutableInteger> queue) {
        return new BinaryHeap<>((BinaryHeap<MutableInteger>) queue);
    }

}
