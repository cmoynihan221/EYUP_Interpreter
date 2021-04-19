package runtime;
import java.util.HashMap;
import java.util.Map;

import ast.Expr.Get;
@SuppressWarnings("unused")
public class EyupInstance {
	private EyupBodger bodger;
	
	
	public EyupInstance(EyupBodger bodger) {
		this.bodger = bodger;
	}
	@Override
	public String toString() {
		return bodger.name+  " instance";
	}
	
	public Object get(String name, Get get) {
		Object var = bodger.lookUpVariable(name, get);
		if (var != null ) {
			if(var instanceof EyupFunction) {
				((EyupFunction)var).bind(this);
			}
			return var;
		}
		
		throw new RuntimeException("Undefined property " + name);
	}

}
