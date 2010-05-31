package me.instcode.common.map;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

/**
 * Read/Write object from/to the given buffer.
 * 
 * Examples:
 * <pre>
 * class TripSerializer implements Serializer&lt;Long, Trip&gt; {
 * 	&#064;Override
 * 	public Entry&lt;Long, Trip&gt; readObject(ByteBuffer buffer) {
 * 		Trip trip = new Trip();
 * 		Long key = Long.valueOf(buffer.getLong());
 * 		trip.count = buffer.getLong();
 * 		trip.sumFare = buffer.getLong();
 * 		trip.sumTime = buffer.getLong();
 * 		return new AbstractMap.SimpleEntry&lt;Long, Trip&gt;(key, trip);
 * 	}
 * 
 * 	&#064;Override
 * 	public void writeObject(Entry&lt;Long, Trip&gt; value, ByteBuffer buffer) {
 * 		Trip trip = value.getValue();
 * 		buffer.putLong(value.getKey().longValue());
 * 		buffer.putLong(trip.count);
 * 		buffer.putLong(trip.sumFare);
 * 		buffer.putLong(trip.sumTime);
 * 	}
 * 
 * 	&#064;Override
 * 	public int getRecordLength() {
 * 		return 32;
 * 	}
 * }
 * </pre>
 *  * To prevent buffer underflow, make sure you have a correct record length.
 * Another way is to manually update the buffer position as following:
 * 
 * <pre>
 * int pos = buffer.position();
 * // ... your serialization code goes here...
 * buffer.position(pos + getRecordSize());
 * </pre>  
 * @author instcode
 * 
 * @param <K>
 *            Key
 * @param <V>
 *            Value
 */
public interface Serializer<K, V> {

	Entry<K, V> readObject(ByteBuffer buffer);

	void writeObject(Entry<K, V> value, ByteBuffer buffer);
	
	int getRecordLength();
}
