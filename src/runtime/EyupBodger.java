package runtime;
import java.util.List;
import java.util.Map;

import ast.Interpreter;
import enums.Tokens;

public class EyupBodger implements Callable {
	final String name;
	public EyupBodger(String name) {
		this.name = name;
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

}
