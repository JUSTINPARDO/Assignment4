import edu.princeton.cs.algs4.SequentialSearchST;

public class CustomSequentialSearchST<Key, Value> extends SequentialSearchST<Key, Value> {
    private int comparisons;

    private class Node {
        private Key key;
        private Value val;
        private Node next;

        public Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    private Node first;

    public int getComparisons() {
        return comparisons;
    }

    public void resetComparisons() {
        comparisons = 0;
    }

    @Override
    public Value get(Key key) {
        comparisons = 0;
        for (Node x = first; x != null; x = x.next) {
            comparisons++;
            if (key.equals(x.key))
                return x.val;
        }
        return null;
    }
}