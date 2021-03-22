package runtime;

import java.util.HashMap;
import java.util.Map;

import ast.Expr;



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
		throw error("Flummoxed: weerz "+name+"?");
	}
	private RuntimeException error(String error) {
		core.Error.runtimeError(error);
		return new RuntimeException();
	}
	public void assign(String name, EnvVar value) {
		if(values.containsKey(name)) {
			EnvVar stored = values.get(name);
			if(checkType(value,stored)) {
				values.put(name, value);
				return;
			}
			throw error("Vexed: "+ name + " wi' bad'un "+ value.value+":"+value.type);
			
		}
		if (enclosed != null) {
			enclosed.assign(name,value);
			return;
		}
		throw error("Flummoxed: weerz "+name+"?");
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
		throw error("Flummoxed: weerz "+name+"?");
	}
	private Boolean checkType(EnvVar newVal, EnvVar stored) {
		if(newVal.type == stored.type) {
			return true;
		}
		else {
			return false; 
		}
	}
}
