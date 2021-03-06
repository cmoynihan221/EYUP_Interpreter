package runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class RTEnvironment {
	private final RTEnvironment enclosed;
	private final Map<String,EnvVar> values = new HashMap<>();
	
	public RTEnvironment() {
		enclosed = null;
	}
	public RTEnvironment(RTEnvironment enclosed) {
		this.enclosed = enclosed;
	}
	public void define(String name, EnvVar value) {
		values.put(name, value);
	}
	public Object get(String name) {
		if(values.containsKey(name)) {
			return values.get(name).value;
		}
		if (enclosed != null) return enclosed.get(name);
		throw new RuntimeException("Flummoxed: weerz "+name+"?");
	}
	public void assign(String name, EnvVar value) {
		if(values.containsKey(name)) {	
			EnvVar stored = values.get(name);
			if(checkType(value,stored)) {
				values.put(name, value);
				return;
			}
			throw new RuntimeException("Vexed: "+ name + " wi' bad'un "+ value.value+":"+value.type);
			
		}
		if (enclosed != null) {
			enclosed.assign(name,value);
			
			return;
		}
		throw new RuntimeException("Flummoxed: weerz "+name+"?");
	}
	public void remove(String name) {
		if(values.containsKey(name)) {
			values.remove(name);
			return;
		}
		if(enclosed != null) {
			enclosed.remove(name);
			return;
		}
		throw new RuntimeException("Flummoxed: weerz "+name+"?");
	}
	private Boolean checkType(EnvVar newVal, EnvVar stored) {
		if(newVal.type == stored.type) {
			return true;
		}
		else {
			return false; 
		}
	}
	public Object getAt(int dist, String name) {
		return prior(dist).values.get(name);
	}
	public RTEnvironment prior(int dist) {
		RTEnvironment env = this;
		for(int i=0;i<dist;i++) {
			env = env.enclosed;
		}
		return env;
	}
	public Set<String> getValues() {
		return values.keySet();
	}
	public boolean notEmpty() {
		
		return !values.isEmpty();
	}
}
