package me.instcode.undo.support;

import java.util.HashMap;
import java.util.Map;

import me.instcode.undo.Node;
import me.instcode.undo.UndoAdvisor;

public class UndoAdvisorProvider {
	private Map<Class<?>, UndoAdvisor<?>> advisors = new HashMap<Class<?>, UndoAdvisor<?>>();
	
	public <T extends Node> void registerAdvisor(Class<T> clazz, UndoAdvisor<T> advisor) {
		advisors.put(clazz, advisor);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Node> UndoAdvisor<Node> unregisterUndoAdvisor(Class<T> clazz) {
		return (UndoAdvisor<Node>) advisors.remove(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Node> UndoAdvisor<Node> getUndoAdvisor(Class<T> clazz) {
		return (UndoAdvisor<Node>) advisors.get(clazz);
	}

	public void removeAll() {
		advisors.clear();
	}
}
