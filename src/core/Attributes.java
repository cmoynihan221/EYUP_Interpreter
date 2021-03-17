package core;

import enums.Scope;
import enums.Type;

public class Attributes {

	private Object value;
	private Scope scope;
	private Type type; 
	
	public Attributes(Object value,Scope scope,Type type) {
		this.value = value;
		this.scope = scope;
		this.type = type; 
	}
	
	public Object value() {
		return value;
	}
	
	public Scope scope() {
		return scope;
	}
	
	public Type type() {
		return type;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public void setType(Type type) {
		this.type = type; 
	}

}
