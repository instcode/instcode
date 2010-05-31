package me.instcode.common.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This implementation of {@link Map} interface allows to store data in both
 * inner memory & external memory. It has a simple swap in/swap out mechanism to
 * partially migrate data in primary memory to hard disk and vice versa.
 * 
 * There are a lot of hard code snips in this class and it would be a signal of
 * bad implementation :) Yup, don't use it, use {@link FileMap} instead.
 * 
 * @author instcode
 * 
 * @param <K>
 * @param <V>
 */
public class MapEx<K, V> extends AbstractMap<K, V> {

	Map<K, Integer> mapu = new HashMap<K, Integer>(15000000);
	Entry<K, V>[] table;
	
	Serializer<K, V> serializer;
	FileChannel swapFileChannel;
	ByteBuffer buffer;

	int start;
	int end;
	
	int swapFactor;
	int swapSize;

	@SuppressWarnings("unchecked")
	public MapEx(int size, Serializer<K, V> serializer) {
		this.serializer = serializer;
		table = new Entry[size];
		start = 0;
		end = table.length;
		swapFactor = table.length / 4;
		try {
			File createTempFile = File.createTempFile("instcode", "mapex");
			createTempFile.deleteOnExit();
			RandomAccessFile swapFile = new RandomAccessFile(createTempFile, "rw");
			swapFileChannel = swapFile.getChannel();
			buffer = ByteBuffer.allocate(100 * serializer.getRecordLength());
			//buffer= swapFileChannel.map(FileChannel.MapMode.READ_WRITE, 1, 200000000);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	HashIterator iterator = new HashIterator();
	
	class HashIterator implements Iterator<java.util.Map.Entry<K, V>> {
		Entry<K, V> current;
		int index;
		
		public HashIterator() {
		}
		
		HashIterator get() {
			index = 0;
			buffer.clear();
			buffer.limit(0);
			return this;
		}
		
		@Override
		public String toString() {
			return current + " " + index + " " + buffer;
		}
		
		@Override
		public boolean hasNext() {
			return index < mapu.size();
		}

		@Override
		public java.util.Map.Entry<K, V> next() {
			current = null;
			if (index < table.length) {
				current = fromMemory();
			}
			if (current == null) {
				current = fromSwapFile();
			}
			return current;
		}

		@Override
		public void remove() {
			mapu.remove(current.getKey());
		}
		
		private Entry<K, V> fromMemory() {
			Entry<K, V> next = null;
			while (next == null && index < table.length) {
				// (A && (B || C)) || (!A && B && C) == (B && C) || (A && (B || C)) 
				if ((start <= end && (index < start || index >= end)) ||
					(index < start && index >= end)) {
					Entry<K, V> entry = table[index];
					if (entry != null && mapu.containsKey(entry.getKey())) {
						next = entry;
					}
				}
				index++;
			}
			return next;
		}

		private Entry<K, V> fromSwapFile() {
			Entry<K, V> next = null;
			while (next == null && hasNext()) {
				if (!buffer.hasRemaining()) {
					buffer.clear();
					try {
						int offset = index - table.length;
						long position = (long)offset * (long)serializer.getRecordLength();
						swapFileChannel.read(buffer, position);
						buffer.flip();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				Entry<K, V> entry = serializer.readObject(buffer);
				if (mapu.containsKey(entry.getKey())) {
					next = entry;
				}
				index++;
			}
			return next;
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		return mapu.containsKey(key);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new AbstractSet<java.util.Map.Entry<K, V>>() {
			@Override
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				return iterator.get();
			}

			@Override
			public int size() {
				return mapu.size();
			}
		};
	}

	@Override
	public V get(Object key) {
		Integer index = mapu.get(key);
		if (index == null) {
			return null;
		}
		return getValue(index.intValue());
	}

	private V getValue(int index) {
		int offset = index;
		if (offset < table.length) {
			return table[offset].getValue();
		}
		// In the swap file
		offset -= table.length;
		buffer.clear();
		buffer.limit(serializer.getRecordLength());
		try {
			long position = (long)offset * (long)serializer.getRecordLength();
			swapFileChannel.read(buffer, position);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		buffer.flip();
		Entry<K, V> entry = serializer.readObject(buffer);
		return entry.getValue();
	}

	@Override
	public V put(K key, V value) {
		Integer index = mapu.get(key);
		if (index != null) {
			int offset = index.intValue();
			if (offset < table.length) {
				// In memory => Replace the value content
				table[offset].setValue(value);
				return value;
			}
			// In swap file, overwrite existing content
			offset -= table.length;
			try {
				buffer.clear();
				serializer.writeObject(new AbstractMap.SimpleEntry<K, V>(key, value), buffer);
				buffer.flip();
				long position = (long)offset * (long)serializer.getRecordLength();
				swapFileChannel.write(buffer, position);
				return value;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (start == end || (start % table.length) == end) {
			try {
				swapOut();
			}
			catch (IOException e) {
			}
		}
		start = start % table.length;
		index = new Integer(start);
		mapu.put(key, index);
		table[index.intValue()] = new AbstractMap.SimpleEntry<K, V>(key, value);
		start++;
		return value;
	}

	private void swapOut() throws IOException {
		buffer.clear();
		long position = (long)swapSize * (long)serializer.getRecordLength();
		swapFileChannel.position(position);
		for (int i = 0; i < swapFactor; i++) {
			int index = (end + i) % table.length;
			Entry<K, V> entry = table[index];
			serializer.writeObject(entry, buffer);
			mapu.put(entry.getKey(), Integer.valueOf((table.length + swapSize++)));
			if (!buffer.hasRemaining()) {
				buffer.flip();
				swapFileChannel.write(buffer);
				buffer.clear();
			}
		}
		end = (end + swapFactor) % table.length; 
		buffer.flip();
		swapFileChannel.write(buffer);
	}

	@Override
	public String toString() {
		return "[" + mapu.size() + ", " + swapSize + "]";
	}
	
	@Override
	public V remove(Object key) {
		Integer index = mapu.remove(key);
		if (index == null) {
			return null;
		}
		return getValue(index.intValue());
	}
}
