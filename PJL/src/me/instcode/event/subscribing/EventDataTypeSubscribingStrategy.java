package me.instcode.event.subscribing;

import java.util.List;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;

public class EventDataTypeSubscribingStrategy extends AbstractSubscribingStrategy {

	@Override
	public ModifyListener[] getListeners(ModifyEvent event) {
		Object key = event.getData().getClass();
		List<ModifyListener> listeners = map.get(key);
		return listeners.toArray(new ModifyListener[listeners.size()]);
	}
}
