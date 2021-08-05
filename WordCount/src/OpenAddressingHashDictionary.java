/**
 *
 * @author luj8
 */
public class OpenAddressingHashDictionary<K,V> implements Dictionary<K,V> {
    private Object[] table;
    private static final int DEFAULT_CAPACITY = 19;
    private int capacity;
    private int size;
    private static final double LOAD_CAPACITY = 0.5;
    
    public OpenAddressingHashDictionary() {
        this(DEFAULT_CAPACITY);
    }
    
    public OpenAddressingHashDictionary(int initialCapacity) {
        capacity = nextPrime(initialCapacity);
        table = new Object[capacity];
    }
    
    //Go through all the entries in the table. If null, skip to the next entry.
    //If not null, add all keys from chain into the List
    //Verify that the list size is equal to the dictionary size, then
    //return the List.
    @Override
    public List<K> keys() {
        List<K> keys = new AList<>();
        for(int i =0; i < capacity; i++){
            Entry n = (Entry) table[i];
            if(n != null && !n.isRemoved())
                keys.add(n.getKey());
        }
        assert keys.size() == size;
        return keys;
    }

    //Same as keys method, but return the list of values.
    @Override
    public List<V> values() {
        List<V> values = new AList<>();
        for(int i =0; i < capacity; i++){
            Entry n = (Entry)table[i];
            if(n != null && !n.isRemoved())
                values.add(n.getValue());            
        }
        assert values.size() == size;
        return values;        
    }

    @Override
    public V get(Object key) {
        int index = hashIndex((K)key);
        index = probe(index, (K)key);
        Entry n = (Entry)table[index];
        if (n != null && !n.isRemoved()) {
            return n.getValue();
        } else {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    } 

    @Override
    public V put(K key, V value) {
        int index = probe(hashIndex(key), key);
        Entry current = (Entry)table[index];
        if (current == null) {
            if (isOverloaded()) {
                rehash(capacity * 2);
                index = probe(hashIndex(key), key);
            }
            table[index] = new Entry(key, value);
            size++;
            return null;
        } else {  
            if (current.isRemoved()){
                size++;
            }
            V old = current.getValue();
            current.setKey(key);
            current.setValue(value);
            return old;
        }
    }
    
    @Override
    public V remove(Object key) {
        int index = hashIndex((K)key);
        index = probe(index, (K)key);
        Entry n = (Entry)table[index];
        if (n != null && !n.isRemoved()) {
            V value = n.getValue();
            n.remove();
            size--;            
            return value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(K key) {
        int index = hashIndex((K)key);
        index = probe(index, (K)key);
        Entry n = (Entry)table[index];
        return (n != null && !n.isRemoved());  
    }
    
    private int probe(int index, K key) {
        boolean found = false;
        int firstRemovedIndex = -1;
        int inc = 1;
        
        while (!found && (table[index] != null)){
            Entry n = (Entry)table[index];
            if (!n.isRemoved()) {
                if (key.equals(n.getKey())) {
                    found = true;
                } else {
                    index = (index + inc) % capacity;
                    inc += 2;
                }                
            } else {
                if (firstRemovedIndex == -1)
                    firstRemovedIndex = index;
                index = (index + inc) % capacity;
                inc += 2;
            }
        }
        
        if (found || (firstRemovedIndex == -1))
            return index;
        else
            return firstRemovedIndex;
    }
    
    private int hashIndex(K key) {
        int index = key.hashCode() % capacity;
        return (index < 0)?(index+capacity):index;
    }
    
    private boolean isOverloaded() {
        return (double)size/capacity > LOAD_CAPACITY;
    }
    
    private void rehash(int newCapacity) {
        newCapacity = nextPrime(newCapacity);
        Object[] oldTable = table;
        table = new Object[newCapacity];
        int count = 0;
        for (int i = 0; i < oldTable.length; i++){
            Entry n = (Entry) oldTable[i];
            if (n != null && !n.isRemoved()) {
                int newIndex = probe(hashIndex(n.getKey()), n.getKey());
                table[newIndex] = n;
                count++;
            }
        }
        assert count == size;
    }
    
    private int nextPrime(int n) {
        int i = n;  
        while(!isPrime(i))
            i++;
        return i;
    }
    
    private boolean isPrime(int n) {
        if (n<2) {
            return false;        
        } else if (n == 2) {
            return true;
        } else if (n%2 == 0){
            return false;
        } else {
            for (int i = 3; i <= Math.sqrt(n); i+=2) {
                if (n % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
        
    private class Entry {
        private K key;
        private V value;
        private boolean removed;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            removed = false;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
            reactivate();
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
            reactivate();
        }
        
        public boolean isRemoved() {
            return removed;
        }

        public void remove() {
            key = null;
            value = null;
            removed = true;
        }
        
        public void reactivate() {
            removed = false;
        }
    }
}
