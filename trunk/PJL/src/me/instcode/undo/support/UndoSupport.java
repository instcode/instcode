package me.instcode.undo.support;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.instcode.undo.Node;
import me.instcode.undo.UndoAdvisor;
import me.instcode.undo.UndoEventListener;

public class UndoSupport implements UndoAdvisor<Node> {
	private List<UndoEventListener> listeners = new ArrayList<UndoEventListener>();
	private UndoAdvisorProvider provider;
	
	private Map<String, Node> nodes = new HashMap<String, Node>();
	private EditUndoManager manager = new EditUndoManager(this);
	private UndoManager undoManager;
	
	private NodeList nl;
	
	/**
	 * A container contains a list of Node(s) 
	 */
	private class NodeList implements Node {
		List<Node> nodes = new ArrayList<Node>();

		public String getKey() {
			return "";
		}

		public boolean equals(Node node) {
			return (node instanceof NodeList) && nodes == ((NodeList)node).nodes;
		}

		public Object getValue() {
			return "";
		}

		public void setValue(Object value) {
			// Do nothing
		}
	}
	
	/**
	 * An UndoAdvisor for handling NodeList
	 */
	private class NodeListUndoAdvisor implements UndoAdvisor<NodeList> {
		public void restore(NodeList nl) {
			for (Node node : nl.nodes) {
				UndoAdvisor<Node> advisor = provider.getUndoAdvisor(node.getClass());
				UndoSupport.this.restore(node, advisor);
			}
		}
	}

	public Map<String, Node> getNodes() {
		return nodes;
	}
	
	public UndoSupport(UndoManager undoManager) {
		this.undoManager = undoManager;
		this.nodes.put("", new NodeList());
	}

	public EditUndoManager getManager() {
		return manager;
	}
	
	public void setUndoAdvisorProvider(UndoAdvisorProvider provider) {
		if (this.provider != null) {
			this.provider.removeAll();
		}
		this.provider = provider;
		this.provider.registerAdvisor(NodeList.class, new NodeListUndoAdvisor());
	}

	private void restore(Node node, UndoAdvisor<Node> advisor) {
		Node saved = nodes.get(node.getKey());
		advisor.restore(node);
		if (saved != null) {
			Object value = saved.getValue();
			saved.setValue(node.getValue());
			node.setValue(value);
		}
	}

	/* (non-Javadoc)
	 * @see me.instcode.undo.UndoAdvisor#restore(me.instcode.undo.Node)
	 */
	public void restore(Node node) {
		UndoAdvisor<Node> advisor = provider.getUndoAdvisor(node.getClass());
		restore(node, advisor);
		fireUndoExecuted(new UndoEvent(node));
	}

	/**
	 * Collect changes by assembling node.
	 * 
	 * @param node
	 * 		The node that was changed.
	 */
	public void changed(Node node) {
		if (nl == null) {
			nl = new NodeList();
		}
		Node saved = nodes.get(node.getKey());
		if (saved == null) {
			saved = node;
		}
		else {
			nodes.put(node.getKey(), node);
		}
		nl.nodes.add(saved);
		
	}
	
	/**
	 * Post a change to undo repository
	 * 
	 * @param node
	 * 		The node that was changed.
	 */
	public void postChanged(Node node, String undoName, String redoName) {
		Node saved = nodes.get(node.getKey());
		if (!node.equals(saved)) {
			if (saved == null) {
				saved = node;
			}
			else {
				nodes.put(node.getKey(), node);
			}
			manager.postEdit(saved, undoName, redoName);
			undoManager.updateCmdUI();
		}
	}
	
	public void postChanged(Node node, String actionMessage) {
		Node saved = nodes.get(node.getKey());
		String undoName = MessageFormat.format(actionMessage, saved, node);
		String redoName = MessageFormat.format(actionMessage, node, saved);
		postChanged(node, undoName, redoName);
	}
	
	/**
	 * Post changes to undo repository
	 */
	public void postChanges(String undoName, String redoName) {
		if (nl != null && nl.nodes.size() > 0) {
			manager.postEdit(nl, undoName, redoName);
			nl = null;
			undoManager.updateCmdUI();
		}
	}
	
	protected void fireUndoExecuted(UndoEvent undoEvent) {
		for (UndoEventListener listener : listeners) {
			listener.undoExecuted(undoEvent);
		}
	}
	
	public void addUndoEventListener(UndoEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeUndoEventListener(UndoEventListener listener) {
		listeners.remove(listener);
	}

	public void clear() {
		nodes.clear();
		manager.clear();
	}
}
