package runtime;

import java.util.List;

import ast.Interpreter;
import enums.Type;

public interface Callable{
	int arity();
	Type type();
	Object call(Interpreter interpreter, List<Object> args);
}
