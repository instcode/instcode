package me.instcode.common.map;

/**
 * If you ever work with a project that uses up 90% RAM on the running machine
 * just to store [key, value] pairs you would appreciate me for implementing
 * this class :)).<br>
 * <br>
 * It's not always true if someone moans that Java is slower than C but
 * unfortunately, this is not the case when comparing memory usage between Java
 * & C implementations. Yes, be prepared that a simple Java program can easily
 * eat up 100MB of your memory without hesitating. The problem is object storing
 * in Java costs lot of memory. Say, if you use a List for an array of Integers,
 * you would end up 4 times memory cost of what it was in a primitive integer
 * array in the first place.<br>
 * <br>
 * No time for NATO, this class implements a very efficient map data structure
 * for storing long to integer key pair (long -> int). The implementation uses
 * all primitive types, no Object to reduce the memory overhead of object
 * storing. The current implementation doesn't support removing entry (it was
 * intent to use in a read-only manner) as well as resizing on-the-fly (memory
 * would not be enough to do this action). In conclusion, this class is best fit
 * in case that you need a map/dictionary that stores key/value pairs (long to
 * int) for read-only purpose.<br>
 * 
 * @author instcode
 */
public class TinyMap {
	public static final int NOT_FOUND = Integer.MIN_VALUE;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. A power of two so the entire
	 * index can fit into the memory.
	 */
	public static final int MAXIMUM_CAPACITY = 1 << 27;

	int[] index;
	long[] keys;
	int[] values;
	int[] nexts;
	
	/**
	 * The number of key-value mappings contained in this map.
	 */
	int size;

	/**
	 * @param capacity This is a very important parameter. This map
	 * can't resize on the fly. You MUST know the capacity of your
	 * dataset in advance. 
	 */
	public TinyMap(int capacity) {
		keys = new long[capacity];
		values = new int[capacity];
		nexts  = new int[capacity];
		
		int indexCapacity = 1;
        while (indexCapacity < capacity)
        	indexCapacity <<= 1;
        
		index = new int[indexCapacity];
		fill(index, -1);
		fill(nexts, -1);
	}

	static void fill(int[] table, int value) {
		int len = table.length;
		if (len > 0) table[0] = value;
		for (int i = 1; i < len; i <<= 1) {
			System.arraycopy(table, 0, table, i, ((len - i) < i) ? (len - i) : i);
		}
	}
	
	static int hash(long v) {
        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
		int h = (int) (v ^ (v >>> 32));
        h += (h <<  15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h <<   3);
        h ^= (h >>>  6);
        h += (h <<   2) + (h << 14);
        return h ^ (h >>> 16);
    }
	
	static int hash64(long v) {
		long h = (~v) + (v << 18); // h = (h << 18) - h - 1;
		h = h ^ (h >>> 31);
		h = h * 21; // h = (h + (h << 2)) + (h << 4);
		h = h ^ (h >>> 11);
		h = h + (h << 6);
		h = h ^ (h >>> 22);
		return (int) h;
	}

	/**
	 * Returns index for hash code h.
	 */
	int indexFor(int h, int length) {
		return h & (length - 1);
	}

	/**
	 * Look for the given key, if it exists, return the value, otherwise, create
	 * a new entry with the given value. This should be a perfect replacement of
	 * get then put the value in.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public int lookUpdate(long key, int value) {
		int hash = hash(key);
		int i = indexFor(hash, index.length);
		int v = index[i];
		if (v >= 0) {
			do {
				if (keys[v] == key) {
					return values[v];
				}
				if (nexts[v] < 0) {
					break;
				}
				v = nexts[v];
			}
			while (true);
			nexts[v] = size;
		}
		else {
			index[i] = size;
		}
		keys[size] = key;
		values[size] = value;
		size++;
		return value;
	}

	public void put(long key, int value) {
		int hash = hash(key);
		int i = indexFor(hash, index.length);
		int v = index[i];
		if (v >= 0) {
			do {
				if (keys[v] == key) {
					values[v] = value;
					return;
				}
				if (nexts[v] < 0) {
					break;
				}
				v = nexts[v];
			}
			while (true);
			nexts[v] = size;
		}
		else {
			index[i] = size;
		}
		keys[size] = key;
		values[size] = value;
		size++;
	}

	/**
	 * @param key
	 * @return
	 */
	public int remove(long key) {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(long key) {
		return get(key) >= 0;
	}

	public int get(long key) {
		int hash = hash(key);
		int v = index[indexFor(hash, index.length)];
		if (v < 0) {
			return NOT_FOUND;
		}
		do {
			if (keys[v] == key) {
				return values[v];
			}
			v = nexts[v];
		}
		while (v > 0);
		return NOT_FOUND;
	}

	int size() {
		return size;
	}
}
