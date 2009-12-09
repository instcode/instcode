package me.instcode.event.subcribing;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;

public interface SubscribingStrategy {
	void register(Object interest, ModifyListener listener);
	void unregister(Object interest, ModifyListener listener);
	ModifyListener[] getListeners(ModifyEvent event);
}
