package core;

import java.util.ArrayList;
import java.util.HashMap;

import enums.Scope;

public class SymbolTable {
	
	Scope scope;
	HashMap<String, Attributes> items = new HashMap<String, Attributes>();
	ArrayList<SymbolTable> Children = new ArrayList<SymbolTable>();
	
	public SymbolTable(Scope scope) {
		this.scope = scope;
	}
	
	public void insert(String name, Attributes attributes) {
		items.put(name, attributes);
	}
	
	public Attributes lookup(String name) {
		if (items.containsKey(name)) {
			return items.get(name);
		}
		else {
			return null;
		}
	}
	
	

}
