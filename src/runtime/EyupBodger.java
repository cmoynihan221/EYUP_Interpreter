package runtime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.Expr;
import ast.Interpreter;
import enums.Tokens;

@SuppressWarnings("unused")
public class EyupBodger implements Callable {
	public final String name;
	public final RTEnvironment globals;
	public RTEnvironment rt;
	public final  Map<Expr, Integer> locals;
	public EyupBodger parent = null;
	
	public EyupBodger(String name) {
		this.name = name;
		locals = new HashMap<>();
		this.globals = new RTEnvironment();
		this.rt = globals;
	}
	
	public EyupBodger(String name,Map<Expr, Integer> locals ) {
		this.name = name;
		this.locals = locals;
		this.globals = new RTEnvironment();
		this.rt = globals;
	}
	
	public EyupBodger(EyupBodger copy ) {
		this.name = copy.name;
		this.locals = new HashMap<>(copy.locals);
		this.globals = new RTEnvironment(copy.globals);
		this.rt = copy.rt;
		this.parent = copy.parent;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int arity() {
		
		return 0;
	}

	@Override
	public Tokens type() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> args) {
		EyupInstance instance = new EyupInstance(this);
		return instance;
		
	}
	
	public Object lookUpVariable(String name, Expr expr) {
		Integer dist = this.locals.get(expr);
		if(dist != null) {
			return this.rt.getAt((dist-1), name);
		}else {
			return this.globals.get(name);
		}
	}
	

}
