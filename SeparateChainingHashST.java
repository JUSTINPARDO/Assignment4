import edu.princeton.cs.algs4.SequentialSearchST;
import edu.princeton.cs.algs4.Queue;

public class SeparateChainingHashST<Key, Value> {
    private static final int INIT_CAPACITY = 4;

    private int n;
    private int m;
    private SequentialSearchST<Key, Value>[] st;
    private int comparisons;

    public SeparateChainingHashST() {
        this(INIT_CAPACITY);
    }

    public SeparateChainingHashST(int m) {
        this.m = m;
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
        for (int i = 0; i < m; i++)
            st[i] = new SequentialSearchST<Key, Value>();
    }

    private void resize(int chains) {
        SeparateChainingHashST<Key, Value> temp = new SeparateChainingHashST<Key, Value>(chains);
        for (int i = 0; i < m; i++) {
            for (Key key : st[i].keys()) {
                temp.put(key, st[i].get(key));
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.st = temp.st;
    }


    private int hash(Key key) {
        String str = key.toString();
        int hash = 0;
        for (int i = 0; i < str.length(); i++)
            hash = (hash * 31) + str.charAt(i);
        return Math.abs(hash) % m;
    }


    private int oldHash(Key key) {
        String str = key.toString();
        int hash = 0;
        int skip = Math.max(1, str.length() / 8);
        for (int i = 0; i < str.length(); i += skip)
            hash = (hash * 37) + str.charAt(i);
        return Math.abs(hash) % m;
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    public Value getWithOldHash(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        int i = oldHash(key);
        comparisons = 0;
        for (Key k : st[i].keys()) {
            comparisons++;
            if (k.equals(key)) {
                return st[i].get(key);
            }
        }
        return null;
    }

    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        int i = hash(key);
        comparisons = 0;
        for (Key k : st[i].keys()) {
            comparisons++;
            if (k.equals(key)) {
                return st[i].get(key);
            }
        }
        return null;
    }

    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) {
            delete(key);
            return;
        }

        if (n >= 10*m) resize(2*m);

        int i = hash(key);
        if (!st[i].contains(key)) n++;
        st[i].put(key, val);
    }

    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");

        int i = hash(key);
        if (st[i].contains(key)) n--;
        st[i].delete(key);

        if (m > INIT_CAPACITY && n <= 2*m) resize(m/2);
    }

    public Iterable<Key> keys() {
        Queue<Key> queue = new Queue<Key>();
        for (int i = 0; i < m; i++) {
            for (Key key : st[i].keys())
                queue.enqueue(key);
        }
        return queue;
    }

    public Iterable<Key> getChain(int index) {
        if (index < 0 || index >= m) throw new IllegalArgumentException("Invalid chain index");
        return st[index].keys();
    }

    public int getComparisons() {
        return comparisons;
    }

    public void resetComparisons() {
        comparisons = 0;
    }
}