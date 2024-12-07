public class LinearProbingHashST<Key, Value> {
    private static final int INIT_CAPACITY = 4;

    private int n;
    private int m;
    private Key[] keys;
    private Value[] vals;
    private int comparisons;
    public LinearProbingHashST() {
        this(INIT_CAPACITY);
    }

    public LinearProbingHashST(int capacity) {
        m = capacity;
        n = 0;
        keys = (Key[])   new Object[m];
        vals = (Value[]) new Object[m];
    }

    private void resize(int capacity) {
        LinearProbingHashST<Key, Value> temp = new LinearProbingHashST<Key, Value>(capacity);
        for (int i = 0; i < m; i++) {
            if (keys[i] != null) {
                temp.put(keys[i], vals[i]);
            }
        }
        keys = temp.keys;
        vals = temp.vals;
        m    = temp.m;
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
        comparisons = 0;
        int i = oldHash(key);
        while (keys[i] != null) {
            comparisons++;
            if (keys[i].equals(key))
                return vals[i];
            i = (i + 1) % m;
        }
        comparisons++;  // Count the null check that ended our search
        return null;
    }

    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        comparisons = 0;
        int i = hash(key);
        while (keys[i] != null) {
            comparisons++;
            if (keys[i].equals(key))
                return vals[i];
            i = (i + 1) % m;
        }
        comparisons++;
        return null;
    }

    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");

        if (val == null) {
            delete(key);
            return;
        }

        if (n >= m/2) resize(2*m);

        int i;
        for (i = hash(key); keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
        }
        keys[i] = key;
        vals[i] = val;
        n++;
    }

    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key)) return;

        int i = hash(key);
        while (!key.equals(keys[i])) {
            i = (i + 1) % m;
        }

        keys[i] = null;
        vals[i] = null;

        i = (i + 1) % m;
        while (keys[i] != null) {
            Key   keyToRehash = keys[i];
            Value valToRehash = vals[i];
            keys[i] = null;
            vals[i] = null;
            n--;
            put(keyToRehash, valToRehash);
            i = (i + 1) % m;
        }

        n--;

        if (n > 0 && n <= m/8) resize(m/2);
    }

    public int getComparisons() {
        return comparisons;
    }

    public void resetComparisons() {
        comparisons = 0;
    }
}