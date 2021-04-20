package runtime;

import java.util.List;

import ast.Interpreter;
import ast.Stmt;
import enums.Tokens;
import enums.Type;

public class EyupFunction implements Callable{
	private Stmt.Function declaration;
	private final EyupBodger closure;
	public EyupFunction(Stmt.Function dec,EyupBodger currentBodger) {
		this.declaration = dec;
		this.closure = currentBodger;
	}

	@Override
	public int arity() {
	Integer paramnum = 0;
	if(declaration.params!=null) {
		for (int i = 0; i<declaration.params.size();i++) {
			for (int n = 0; n<declaration.params.get(i).size();n++) {
				paramnum++;
			}
		}
	}
		return paramnum;
	}

	@Override
	public Tokens type() {
		return declaration.type;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> args) {
		EyupBodger envi = new EyupBodger(closure);
		Integer paramnum = 0;
		if(declaration.params!=null) {
			for (int i = 0; i<declaration.params.size();i++) {
				for (int n = 0; n<declaration.params.get(i).size();n++) {
					checkType(declaration.params.get(i).type, args.get(paramnum));
					envi.globals.define(declaration.params.get(i).params.get(paramnum), new EnvVar( args.get(paramnum),declaration.params.get(i).type));
					paramnum++;
				}
			}
		}
		try {
			interpreter.runBlock(declaration.body.stmts, envi);
		}catch(Return returnValue) {
			return returnValue.value;
		}
		return "nowt";
		
	}

	private void checkType(Tokens type, Object arg) {
		if(!(type == getType(arg))) {
			throw new RuntimeException("Param of wrong type");
		}
	}
	private Tokens getType(Object value) {
		switch(checkType(value)) {
		case Int: case Float: return Tokens.NUMBER;
		case Char: return Tokens.LETTER;
		case String: return Tokens.SCRIPT;
		case Bool: return Tokens.ANSWER;
		default:
			throw new RuntimeException("Unknown Type");
		}
		
	}
	private Type checkType(Object value) {
		if (value instanceof Integer) {
			return Type.Int;
		}
		if (value instanceof Float) {
			return Type.Float;
		}
		if (value instanceof String) {
			if (((String) value).length() == 1) {
				return Type.Char;
			}
			return Type.String;
		}
		if (value instanceof Boolean ) {
			return Type.Bool;
		}
		throw new RuntimeException("Unknown Type");
	}

	public EyupFunction bind(EyupInstance instance) {
		EyupBodger envi = 	new EyupBodger(closure);
		envi.globals.define("this", new EnvVar(instance, Tokens.BODGER));
		return new EyupFunction(declaration, envi);		
	}
}
