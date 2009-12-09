package me.instcode.event.subscribing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.instcode.event.ModifyListener;

public abstract class AbstractSubscribingStrategy implements SubscribingStrategy {

	protected Map<Object, List<ModifyListener>> map = new HashMap<Object, List<ModifyListener>>();
	
	public void register(Object interest, ModifyListener listener) {
		List<ModifyListener> listeners = map.get(interest);
		if (listeners == null) {
			listeners = new ArrayList<ModifyListener>();
			map.put(interest, listeners);
		}
		listeners.add(listener);
	}
	
	public void unregister(Object interest, ModifyListener listener) {
		List<ModifyListener> listeners = map.get(interest);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.size() == 0) {
				map.remove(interest);
			}
		}
	}
	
	public void unregisterAll() {
		map.clear();
	}
}
