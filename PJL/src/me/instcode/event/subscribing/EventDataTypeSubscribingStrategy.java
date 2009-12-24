package me.instcode.event.subscribing;

import java.util.List;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;

public class EventDataTypeSubscribingStrategy extends AbstractSubscribingStrategy {

	@Override
	public ModifyListener[] getListeners(ModifyEvent event) {
		Object key = event.getData().getClass();
		List<ModifyListener> listeners = map.get(key);
		if (listeners == null) {
			// If data is an array, get the first element 
			// as a representative for the rest.
			if (key.getClass().isArray()) {
				Object[] keys = (Object[]) key;
				if (keys.length > 0) {
					listeners = map.get(keys[0].getClass());
				}
			}
		}
		
		return listeners.toArray(new ModifyListener[listeners.size()]);
	}
}
