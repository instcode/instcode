package me.instcode.common.map;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

/**
 * To prevent buffer underflow, make sure you have the correct record
 * length. Another way is to manually update the buffer position as
 * following:
 * 
 * <pre>
 * int pos = buffer.position();
 * // ... your serialization code goes here...
 * buffer.position(pos + getRecordSize());
 * </pre>
 * 
 * @author instcode
 * 
 * @param <K> Key
 * @param <V> Value
 */
public interface Serializer<K, V> {

	Entry<K, V> readObject(ByteBuffer buffer);

	void writeObject(Entry<K, V> value, ByteBuffer buffer);
	
	int getRecordLength();
}
