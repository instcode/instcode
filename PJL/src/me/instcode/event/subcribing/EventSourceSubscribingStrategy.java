package me.instcode.event.subcribing;

import java.util.List;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;

public class EventSourceSubscribingStrategy extends AbstractSubscribingStrategy {

	@Override
	public ModifyListener[] getListeners(ModifyEvent event) {
		Object key = event.getSource();
		List<ModifyListener> listeners = map.get(key);
		return listeners.toArray(new ModifyListener[listeners.size()]);
	}
}
