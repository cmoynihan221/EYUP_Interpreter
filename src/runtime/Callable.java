package runtime;

import java.util.List;

import ast.Interpreter;
import enums.Tokens;

public interface Callable{
	int arity();
	Tokens type();
	Object call(Interpreter interpreter, List<Object> args);
}
