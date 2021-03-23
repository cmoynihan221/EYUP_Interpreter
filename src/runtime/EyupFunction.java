package runtime;

import java.util.List;

import ast.Interpreter;
import ast.Stmt;
import enums.Tokens;
import enums.Type;

public class EyupFunction implements Callable{
	private Stmt.Function declaration;
	public EyupFunction(Stmt.Function dec) {
		this.declaration = dec;
	}

	@Override
	public int arity() {
		
	Integer paramnum = 0;
		for (int i = 0; i<declaration.params.size();i++) {
			for (int n = 0; n<declaration.params.get(i).size();i++) {
				paramnum++;
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
		//type checking
		RTEnvironment envi = new RTEnvironment(interpreter.globals);
		Integer paramnum = 0;
		for (int i = 0; i<declaration.params.size();i++) {
			for (int n = 0; n<declaration.params.get(i).size();i++) {
				checkType(declaration.params.get(i).type, args.get(paramnum));
				paramnum++;
			}
			
			envi.define(declaration.params.get(i).params.get(paramnum), new EnvVar( args.get(paramnum),declaration.params.get(i).type));
		}
		interpreter.runBlock(declaration.body, envi);
		return null;
		
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
		}
		throw new RuntimeException("Unknown Type");
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
}