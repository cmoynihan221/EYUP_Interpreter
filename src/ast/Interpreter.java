package ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Call;
import ast.Expr.Group;
import ast.Expr.Logical;
import ast.Expr.Missen;
import ast.Expr.Primary;
import ast.Expr.Unary;
import ast.Expr.Var;
import ast.Expr.Get;
import ast.Stmt.Block;
import ast.Stmt.Bodger;
import ast.Stmt.DefVar;
import ast.Stmt.Expression;
import ast.Stmt.EyupCall;
import ast.Stmt.ForgetVar;
import ast.Stmt.Print;
import ast.Stmt.Read;
import ast.Stmt.Return;
import ast.Stmt.SitheCall;
import ast.Stmt.When;
import ast.Stmt.While;
import ast.Stmt.Function;
import ast.Stmt.Gander;
import enums.Tokens;
import enums.Type;
import lexer.Lexer;
import parser.Parser;
import runtime.Callable;
import runtime.EnvVar;
import runtime.EyupBodger;
import runtime.EyupFunction;
import runtime.EyupInstance;
import runtime.RTEnvironment;

public class Interpreter implements NodeVisitor {
	
	
	public EyupBodger currentBodger;
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	public Interpreter() {
		 this.currentBodger = new EyupBodger("Gaffer");
		 
	}
	public void interpret(ArrayList<Stmt> statements) {
		try {
			for(Stmt stmt:statements) {
				System.out.println(stmt.accept(this));
			}
		}catch(RuntimeException error) {
			System.out.println(error);
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
		default:
			return null;
		}
		
	}
	@SuppressWarnings("unused")
	private Boolean compareStrings(String left, String right, Tokens op) {
		int comp = left.compareTo(right);
		switch(comp) {
		case -1: switch(op) {
		case LESS:case LESS_EQ:return true;
		case GREAT:case GREAT_EQ: return false;
		default:
			break;
		}
		case 0: switch(op) {
		case GREAT_EQ:case LESS_EQ:return true;
		case GREAT:case LESS: return false;
		default:
			break;
		}
		case 1: switch(op) {
		case LESS:case LESS_EQ:return false;
		case GREAT:case GREAT_EQ: return true;
		default:
			break;
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
		default:
			return null;
		}
		
	}

	@Override
	public Object visitExprStmt(Expression exprstmt) {
		if(exprstmt.expr instanceof Expr.Var) {
			Object value = currentBodger.rt.get(((Expr.Var)(exprstmt.expr)).name);
			if(value==null) {
				value = "nowt";
			}
			return value;
		}
				
		Object value = exprstmt.expr.accept(this);
		
		return value;
	}

	@Override
	public Object visitPrintStmt(Print stmt) {
		Object value = stmt.expr.accept(this);
		if(stmt.type == 0) {
			System.out.println(toString(value));
		}
		if(stmt.type == 1) {
			System.out.print(toString(value));
		}
	    return toString(value);
		
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
			
			currentBodger.rt.define(defVar.name, new EnvVar(value,type));	
			
			
			return currentBodger.name +"." +defVar.name;
		}
		else {
			throw error("Variable definition inncorrect.");
		}
	}

	@Override
	public Object visitVarExpr(Var var) {
		Object val = currentBodger.lookUpVariable(var.name, var);
		if (val instanceof EnvVar) {
			val = ((EnvVar)val).value;
		}
		return val;
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
		currentBodger.rt.assign(assignment.name,new EnvVar(value, type));
		
		return value;
	}
	@Override
	public Object visitForgetVar(ForgetVar forgetVar) {
		String name = forgetVar.name;
		currentBodger.rt.remove(name);
		return name;
	}
	
	
	@Override
	public Object visitIf(Stmt.If if1) {
		if (isBool(if1.condition.accept(this))) {
			if1.thenStmt.accept(this);
		}
		else if(if1.elseStmt != null) {
			if1.elseStmt.accept(this);
		}
		return "nowt";
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
		return "nowt";
	}
	@Override
	public Object visitWhile(While while1) {
		Object condition = while1.condition.accept(this);
		switch(while1.form) {
			case 0: 
				while(isBool(condition)) {
					for(int i=0;i<while1.body.size();i++) {
						while1.body.get(i).accept(this);
					}
					condition = while1.condition.accept(this);
				}
				break;
			case 1:
				do {
					for(int i=0;i<while1.body.size();i++) {
						while1.body.get(i).accept(this);
					}
					condition = while1.condition.accept(this);
				}while(isBool(condition));
				break;
		}
		return "nowt";
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
		EyupFunction fun = new EyupFunction(function,currentBodger);
		currentBodger.rt.define(function.name, new EnvVar(fun, fun.type()));
		return currentBodger.name +"." +function.name;
	}
	@Override
	public Object visitBlock(Block block) {
		runBlock(block.stmts, new EyupBodger(currentBodger));
		return null;
	}
	
	public void runBlock(List<Stmt> stmts, EyupBodger funcBodge) {
		EyupBodger previous = currentBodger;
		try {
			currentBodger = funcBodge;
			for(Stmt stmt:stmts) {
				stmt.accept(this);
			}
		}finally {
			currentBodger = previous;
		}
		
		
	}
	@Override
	public Object visitReturn(Return return1) {
		
		Object value = null;
		
		if(return1.value!= null) value = return1.value.accept(this);
		throw new runtime.Return(value);
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
		if (value instanceof EyupInstance ) {
			return Type.Instance;
		}
		throw error("Unknown Type");
	}
	//Method to validate input to binary operators
	@SuppressWarnings("unused")
	private Boolean validate(Object left, Object right, Tokens op) {
		return null;
	}
	
	private Tokens getType(Object value) {

		switch(checkType(value)) {
		case Int: case Float: return Tokens.NUMBER;
		case Char: return Tokens.LETTER;
		case String: return Tokens.SCRIPT;
		case Bool: return Tokens.ANSWER;
		case Instance: return Tokens.INSTANCE;
		}
		throw error("Unknown Type");
	}
	public void resolve(Expr expr, int depth) {
		currentBodger.locals.put(expr,depth);
		EyupBodger parent = currentBodger.parent;
		while (parent != null) {
			parent.locals.put(expr,depth);
			parent = parent.parent;
		}
	}
	
	
	public static void main(String[] args) {
		Parser p = new Parser();
		Lexer l = new Lexer();
		Interpreter i = new Interpreter();
		lexer.OutputTuple lexed;
		Resolver resolver = new Resolver(i);
		lexed = l.lexString("fettle add giz summat x:Script read(x) oer");
		lexed.tokens.add(Tokens.EOI);
		ArrayList<Stmt> stmts = p.parseInput(lexed);
		resolver.resolve(stmts);
		i.interpret(stmts);
		
		lexed = l.lexString("add()");
		lexed.tokens.add(Tokens.EOI);
		stmts = p.parseInput(lexed);
		resolver.resolve(stmts);
		i.interpret(stmts);
		
		lexed = l.lexString("forget add");
		lexed.tokens.add(Tokens.EOI);
		stmts = p.parseInput(lexed);
		resolver.resolve(stmts);
		i.interpret(stmts);
		
		lexed = l.lexString("gander");
		lexed.tokens.add(Tokens.EOI);
		stmts = p.parseInput(lexed);
		resolver.resolve(stmts);
		i.interpret(stmts);
		
		
		
		
		
	}
	@Override
	public Object visitBodger(Bodger bodger) {
		currentBodger.rt.define(bodger.name, new EnvVar(null, Tokens.BODGER));
		EyupBodger bodg = new EyupBodger(bodger.name);
		currentBodger.rt.assign(bodger.name, new EnvVar(bodg, Tokens.BODGER));
		
		return currentBodger.name +": " +bodger.name;
	}
	@Override
	//Might need to adapt for static versions
	public Object visitGetExpr(Get get) {
		Object object = get.object.accept(this);
		if(object instanceof EyupInstance) {
			return ((EyupInstance) object).get(get.name,get); 
		}
		throw error(get.name+ " only instances have properties");
		
	}
	@Override
	public Object visitEyup(EyupCall eyupCall) {
		Object newBodger = currentBodger.rt.get(eyupCall.name); 
		if(newBodger instanceof EyupBodger) {
			EyupBodger bodge = (EyupBodger)newBodger;
			bodge.parent = currentBodger;
			this.currentBodger = bodge;
			return bodge.name+": "+"eyup";
		}
		
		throw error("not a bodger");
		
	}
	@Override
	public Object visitSithe(SitheCall sitheCall) {
		EyupBodger curBodge = currentBodger.parent;
		if(curBodge == null) {
			System.out.print("END PROG");
		}
		else {
			String name = currentBodger.name;
			this.currentBodger = curBodge;
			return name+": sithee";
		}
		return null;
	}
	@Override
	public Object visitMissenExpr(Missen missen) {
		return currentBodger.lookUpVariable("this",missen);
		
	}
	@Override
	public Object visitGander(Gander gander) {
		String output = "nowt";
		if (currentBodger.globals.notEmpty()) {
			Iterator values = currentBodger.globals.getValues().iterator();
			output = "";
			while(values.hasNext()) {
				output = output + currentBodger.name+"."+values.next();
				if(values.hasNext()) {
					output = output + "\n";
				}
			}
			
		}
		
		return output;
	}
	@Override
	public Object visitRead(Read read) {
		String value = "nowt";
		try {
			value = input.readLine();
			currentBodger.rt.assign(read.var,new EnvVar(value, Tokens.SCRIPT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	 

}
