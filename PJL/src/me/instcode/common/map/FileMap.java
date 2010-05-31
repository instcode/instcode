package me.instcode.common.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation that uses Memory Mapped File for storing values
 * and use {@link TinyMap} for storing keys in-memory. This class supports key
 * with {@link Long} type only and doesn't allow to remove entries once they were
 * added to the map. It is best for "write-and-forget, read-forever"... :)) 
 * 
 * @author instcode
 * 
 * @param <V>
 */
public class FileMap<V> extends AbstractMap<Long, V> {
	// Size of memory mapped file. Plz note that on x86 machine, one process
	// should have less than 2GB memory, you should modify the following value
	// to an appropriate value to be used, i.e. 512MB...
	private static final int MAPPING_SIZE = Integer.MAX_VALUE; // 2GB
	
	private TinyMap tinyMap;

	private Serializer<Long, V> serializer;
	private ByteBuffer[] buffers;

	private int size;
	private final int RECORDS_PER_MAPPING;

	public FileMap(int capacity, Serializer<Long, V> serializer) {
		this.serializer = serializer;
		this.tinyMap = new TinyMap(capacity);
		this.RECORDS_PER_MAPPING = MAPPING_SIZE / serializer.getRecordLength();
		this.buffers = new ByteBuffer[capacity / RECORDS_PER_MAPPING + 1];
	}

	class HashIterator implements Iterator<java.util.Map.Entry<Long, V>> {
		Entry<Long, V> current;
		ByteBuffer buffer;
		int index;
		
		public HashIterator() {
			buffer = buffers[0];
			if (buffer != null) {
				buffer.position(0);
			}
		}

		@Override
		public boolean hasNext() {
			return index < tinyMap.size();
		}

		@Override
		public java.util.Map.Entry<Long, V> next() {
			if (!buffer.hasRemaining()) {
				buffer = buffers[index / RECORDS_PER_MAPPING];
				buffer.position(0);
			}
			index++;
			current = serializer.readObject(buffer);
			return current;
		}

		@Override
		public void remove() {
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		return tinyMap.containsKey(((Long)key).longValue());
	}

	@Override
	public Set<java.util.Map.Entry<Long, V>> entrySet() {
		return new AbstractSet<java.util.Map.Entry<Long, V>>() {
			@Override
			public Iterator<java.util.Map.Entry<Long, V>> iterator() {
				return new HashIterator();
			}

			@Override
			public int size() {
				return tinyMap.size();
			}
		};
	}

	@Override
	public V get(Object key) {
		int index = tinyMap.get(((Long)key).longValue());
		if (index < 0) {
			return null;
		}
		return getValue(index);
	}

	private V getValue(int index) {
		ByteBuffer buffer = buffers[index / RECORDS_PER_MAPPING];
		int offset = index % RECORDS_PER_MAPPING;
		int position = offset * serializer.getRecordLength();
		buffer.position(position);
		Entry<Long, V> entry = serializer.readObject(buffer);
		return entry.getValue();
	}

	@Override
	public V put(Long key, V value) {
		ByteBuffer buffer = getBuffer(key.longValue());
		serializer.writeObject(new AbstractMap.SimpleEntry<Long, V>(key, value), buffer);
		return value;
	}

	private ByteBuffer getBuffer(long key) {
		int index = tinyMap.lookUpdate(key, size);
		if (index == size) {
			size++;
		}
		ByteBuffer buffer = buffers[index / RECORDS_PER_MAPPING];
		if (buffer == null) {
			try {
				File mappingFile = File.createTempFile("instcode-", ".swp");
				mappingFile.deleteOnExit();
				FileChannel swapFileChannel = new RandomAccessFile(mappingFile, "rw").getChannel();
				int mappingSize = MAPPING_SIZE;
				buffer = swapFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, mappingSize);
				buffers[index / RECORDS_PER_MAPPING] = buffer;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			int offset = index % RECORDS_PER_MAPPING;
			int position = offset * serializer.getRecordLength();
			buffer.position(position);
		}
		return buffer;
	}

	@Override
	public String toString() {
		return tinyMap.toString();
	}
	
	@Override
	public V remove(Object key) {
		return null;
	}
}
