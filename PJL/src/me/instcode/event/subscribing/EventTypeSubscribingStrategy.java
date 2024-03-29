package me.instcode.event.subscribing;

import java.util.List;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;

public class EventTypeSubscribingStrategy extends AbstractSubscribingStrategy {

	@Override
	public ModifyListener[] getListeners(ModifyEvent event) {
		Object key = Integer.valueOf(event.getType());
		List<ModifyListener> listeners = map.get(key);
		return listeners.toArray(new ModifyListener[listeners.size()]);
	}
}
