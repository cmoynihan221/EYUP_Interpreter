package ast;

import java.util.ArrayList;
import java.util.List;

import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Call;
import ast.Expr.Group;
import ast.Expr.Logical;
import ast.Expr.Primary;
import ast.Expr.Unary;
import ast.Expr.Var;
import ast.Stmt.Block;
import ast.Stmt.DefVar;
import ast.Stmt.Expression;
import ast.Stmt.ForgetVar;
import ast.Stmt.If;
import ast.Stmt.Print;
import ast.Stmt.When;
import ast.Stmt.While;
import ast.Stmt.Function;
import enums.Tokens;
import enums.Type;
import lexer.Lexer;
import parser.Parser;
import runtime.Callable;
import runtime.EnvVar;
import runtime.EyupFunction;
import runtime.RTEnvironment;

public class Interpreter implements NodeVisitor {
	public final RTEnvironment globals = new RTEnvironment();
	private RTEnvironment rt = globals;
	
	public Interpreter() {
		// TODO Auto-generated constructor stub
	}
	public void interpret(ArrayList<Stmt> statements) {
		try {
			for(Stmt stmt:statements) {
				stmt.accept(this);
			}
		}catch(RuntimeException error) {
			error("runtime error at interpret");
		}
		
	}

	private String toString(Object value) {
		
		//TODO write code for handling floats and ints
		if (value == null) return "nowt";

	    if (value instanceof Integer) {
	      return ((Integer) value).toString();
	    }
	    if (value instanceof Boolean) {
		      if ((boolean) value)return "aye";
		      return "nay";
		    }

	    return value.toString();
	}
	@Override
	public Object visitBinaryExpr(Binary expr) {
		Object left = expr.left.accept(this);
		Object right = expr.right.accept(this);
		
		switch(expr.op){
		case EXCLA_EQ: return !isEqual(left, right);
      	case EQUAL: return isEqual(left, right);
      	
      	// TODO edit these to work with strings, letters, nums and bool, float
		case GREAT:
			checkNumOperands(expr.op,left,right);
			return (Integer)left > (Integer)right;
		case GREAT_EQ:
			checkNumOperands(expr.op,left,right);
			return (Integer)left >= (Integer)right;
      	case LESS:
      		checkNumOperands(expr.op,left,right);
      		return (Integer)left < (Integer)right;
      	case LESS_EQ:
      		checkNumOperands(expr.op,left,right);
      		return (Integer)left <= (Integer)right;
		case MINUS:
			checkNumOperands(expr.op,left,right);
			return (Integer)left - (Integer)right;
		case PLUS:
			checkNumOperands(expr.op,left,right);
			return (Integer)left + (Integer)right;
		case STAR:
			checkNumOperands(expr.op,left,right);
			return (Integer)left * (Integer)right;
		case FSLASH:
			checkNumOperands(expr.op,left,right);
			return (Integer)left / (Integer)right;
		case DOLLA:
			checkStringOperands(expr.op,left,right);
			return (String)left+(String)right;
		}
		return null;
	}
	private Boolean compareStrings(String left, String right, Tokens op) {
		int comp = left.compareTo(right);
		switch(comp) {
		case -1: switch(op) {
		case LESS:case LESS_EQ:return true;
		case GREAT:case GREAT_EQ: return false;
		}
		case 0: switch(op) {
		case GREAT_EQ:case LESS_EQ:return true;
		case GREAT:case LESS: return false;
		}
		case 1: switch(op) {
		case LESS:case LESS_EQ:return false;
		case GREAT:case GREAT_EQ: return true;
		}
		default: throw error("Comparison error: "+left+" : "+right);
		}
	}
	
	private boolean isEqual(Object left, Object right) {
		if (left == null && right == null) return true;
	    if (left == null) return false;

	    return left.equals(right);
	}

	@Override
	public Object visitGroupExpr(Group expr) {
		return expr.expr.accept(this);
	}

	@Override
	public Object visitPrimaryExpr(Primary expr) {
		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		Object right = expr.right.accept(this);
		
		switch(expr.op) {
		case MINUS:
			checkNumOperand(Tokens.MINUS,right);
			return -(Integer)right;
		case EXCLAMATION:
			return !isBool(right);
		}
		return null;
	}

	@Override
	public Object visitExprStmt(Expression exprstmt) {
		if(exprstmt.expr instanceof Expr.Var) {
			Object value = rt.get(((Expr.Var)(exprstmt.expr)).name);
			if(value==null) {
				value = "nowt";
			}
			System.out.println(value);
		}
				
		exprstmt.expr.accept(this);
		
		return null;
	}

	@Override
	public Object visitPrintStmt(Print stmt) {
		Object value = stmt.expr.accept(this);
	    System.out.println(toString(value));
	    return null;
		
	}

	@Override
	public Object visitDefVar(DefVar defVar) {
		Object value = null;
		Tokens type = Tokens.NONE;
		if(defVar.init != null ) {
			value = defVar.init.accept(this);
			if(defVar.init instanceof Expr.Primary) {
				type = ((Expr.Primary)(defVar.init)).type;
			}else {
				type = getType(value);
			}
			
			rt.define(defVar.name, new EnvVar(value,type));	
			System.out.println(defVar.name);
			return null;
		}
		else {
			throw error("Variable definition inncorrect.");
		}
	}

	@Override
	public Object visitVarExpr(Var var) {
		return rt.get(var.name);
	}

	@Override
	public Object visitAssignmentExpr(Assignment assignment) {
		Object value  = assignment.value.accept(this);
		Tokens type = Tokens.NONE;
		//needs to check type of variable
		if(assignment.value instanceof Expr.Primary) {
			type = ((Expr.Primary)(assignment.value)).type;
		}else {
			type = getType(value);
		}
		rt.assign(assignment.name,new EnvVar(value, type));
		System.out.println(value);
		return null;
	}
	@Override
	public Object visitForgetVar(ForgetVar forgetVar) {
		String name = forgetVar.name;
		rt.remove(name);
		return null;
	}
	
	private void checkNumOperand(Tokens op, Object operand) {
		if (operand instanceof Integer) return;
		throw error( "Operand must be a number. Error on: "+ op);
	}
	private void checkStringOperands(Tokens op, Object left, Object right) {
		if (left instanceof String && right instanceof String) return;
		throw error("Operands must be Script. Error on: "+ op);
	}
	private void checkNumOperands(Tokens op, Object left, Object right) {
	if (left instanceof Integer && right instanceof Integer) return;
	throw error("Operands must be numbers. Error on: "+ op);
	}
	private boolean isBool(Object object) {
	    if (object == null) return false;
	    if (object instanceof Boolean) return (boolean)object;
	    return true;
	 }
	private RuntimeException error(String error) {
	    core.Error.runtimeError(error);
		return new RuntimeException();
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
		throw error("Unknown Type");
	}
	//Method to validate input to binary operators
	private Boolean validate(Object left, Object right, Tokens op) {
		return null;
	}
	
	private Tokens getType(Object value) {
		switch(checkType(value)) {
		case Int: case Float: return Tokens.NUMBER;
		case Char: return Tokens.LETTER;
		case String: return Tokens.SCRIPT;
		case Bool: return Tokens.ANSWER;
		}
		throw error("Unknown Type");
	}
	
	public static void main(String[] args) {
		Parser p = new Parser();
		Lexer l = new Lexer();
		Interpreter i = new Interpreter();
		lexer.OutputTuple lexed = l.lexString("if x<7 then write(5) else write(6) oer");
		lexed.tokens.add(Tokens.EOI);
		i.interpret(p.parseInput(lexed));
		//lexed = l.lexString("");
		//.tokens.add(Tokens.EOI);
		//.interpret(p.parseInput(lexed));
		//lexed = l.lexString("");
		//lexed.tokens.add(Tokens.EOI);
		//i.interpret(p.parseInput(lexed));
			
	}
	@Override
	public Object visitIf(Stmt.If if1) {
		if (isBool(if1.condition.accept(this))) {
			if1.thenStmt.accept(this);
		}
		else if(if1.elseStmt != null) {
			if1.elseStmt.accept(this);
		}
		return null;
	}
	@Override
	public Object visitLogicExpr(Logical logical) {
		Object left = logical.left.accept(this);
		if(logical.op == Tokens.OR) {
			if (isBool(left)) {
				return left;
			}
		}else {
			if(!isBool(left)) {
				return left;
			}
		}
		return logical.right.accept(this);
	}
	@Override
	public Object visitWhen(When when) {
		for(int i=0;i<when.conditions.size();i++) {
			if(isBool(when.conditions.get(i).accept(this))) {
				when.stmts.get(i).accept(this);
				return null;
			}
		}if (when.elseStmt != null) {
			when.elseStmt.accept(this);
		}
		return null;
	}
	@Override
	public Object visitWhile(While while1) {
		Object condition = while1.condition.accept(this);
		while(isBool(condition)) {
			for(int i=0;i<while1.body.size();i++) {
				while1.body.get(i).accept(this);
			}
			condition = while1.condition.accept(this);
		}
		return null;
	}
	@Override
	public Object visitCallExpr(Call call) {
		Object called = call.called.accept(this);
		List<Object> args = new ArrayList<>();
		for (Expr arg : call.args) {
			args.add(arg.accept(this));
		}
		if(!(called instanceof Callable)) {
			throw new RuntimeException("Can only call functions and bodgers");
		}
		Callable function = (Callable)called;
		if(args.size() != function.arity()) {
			throw new RuntimeException("Expected " +
			          function.arity() + " arguments but got " +
			          args.size());
		}
		return function.call(this, args);
		
	}
	@Override
	public Object visitFunction(Function function) {
		EyupFunction fun = new EyupFunction(function);
		rt.define(function.name, new EnvVar(fun, fun.type()));
		return null;
	}
	@Override
	public Object visitBlock(Block block) {
		runBlock(block.stmts, new RTEnvironment(rt));
		return null;
	}
	public void runBlock(List<Stmt> stmts, RTEnvironment rt) {
		RTEnvironment previous = this.rt;
		try {
			this.rt = rt;
			for(Stmt stmt:stmts) {
				stmt.accept(this);
			}
		}finally {
			this.rt = previous;
		}
		
	}
	
	
	

}
