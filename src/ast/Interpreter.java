package ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Call;
import ast.Expr.CharAccess;
import ast.Expr.Group;
import ast.Expr.Instance;
import ast.Expr.Logical;
import ast.Expr.Missen;
import ast.Expr.Primary;
import ast.Expr.StringAccess;
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
				System.out.print(toString(stmt.accept(this))+"\n");
			}
		}
		catch(core.Leave l) {
			throw new core.Leave("Leaving!");
		}
		catch(runtime.Return r) {
			System.out.print(toString(r.getMessage())+"\n");
		}
		catch(RuntimeException error) {
			
			System.out.print(error.getMessage()+"\n");
		}
		
	}
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private String bstring(Object value) {
		if(value instanceof String) return (String)value;
		return toString(value);
	}
	private String toString(Object value) {
		
		//TODO write code for handling floats and ints
		if (value == null) return "nowt";

	    if (value instanceof Double) {
	      return df2.format((Double) value);
	    }
	    if (value instanceof Boolean) {
		      if ((boolean) value)return "aye";
		      return "nay";
		    }
	    if (value instanceof String) {
		      return "\""+ value+ "\"";
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
      	
      	
		case GREAT:
			return order(left,right, Tokens.GREAT);
		case GREAT_EQ:
			return order(left,right, Tokens.GREAT_EQ);
      	case LESS:
      		return order(left,right, Tokens.LESS);
      	case LESS_EQ:
      		return order(left,right, Tokens.LESS_EQ);
		case MINUS:
			checkNumOperands(expr.op,left,right);
			return (Double)left - (Double)right;
		case PLUS:
			checkNumOperands(expr.op,left,right);
			return (Double)left + (Double)right;
		case STAR:
			checkNumOperands(expr.op,left,right);
			return (Double)left * (Double)right;
		case FSLASH:
			checkNumOperands(expr.op,left,right);
			return (Double)left / (Double)right;
		case DOLLA:
			checkStringOperands(expr.op,left,new String("null"));
			if(right instanceof String) {
				return (String)left+(String)right;
			}
			return (String)left+toString(right );
		case PERCENT:
			checkNumOperands(expr.op,left,right);
			return (Double)left%(Double)right;
		default:
			return null;
		}
		
	}
	private Boolean order(Object left, Object right,Tokens op) {
		try {
			switch(getType(left)) {
			case LETTER: return interpretOrder(((Character)left).compareTo((Character)right),op);
			case SCRIPT: return interpretOrder(((String)left).compareTo((String)right),op);
			case NUMBER: return interpretOrder(((Double)left).compareTo((Double)right),op);
			case ANSWER: return interpretOrder(((Boolean)left).compareTo((Boolean)right),op);
			default:
				throw new RuntimeException("Vexed: Can't order tha'");
			}
		}catch(ClassCastException e) {
			throw new RuntimeException("Vexed: messin' wi types");
		}
	}
	
	
	private Boolean interpretOrder(int compareTo, Tokens op) {
		int value = compareTo;
		boolean output;
		switch(op) {
		case LESS_EQ: case GREAT: value = value*-1;
		default:
			break;
		}
		switch(value) {
		case 1: output = false; break;
		case 0: output = false; break;
		case -1: output = true; break;
		default:
			throw new RuntimeException("Flummoxed: comparison error.");
		}
		switch(op) {
		case LESS_EQ: case GREAT_EQ: return !output;
		default:
			return output;
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
			return -(Double)right;
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
			return value;
		}
				
		Object value = exprstmt.expr.accept(this);
		
		return value;
	}

	@Override
	public Object visitPrintStmt(Print stmt) {
		Object value = stmt.expr.accept(this);
		if(stmt.type == 0) {
			System.out.print(toString(value)+"\n");
		}
		if(stmt.type == 1) {
			System.out.print(toString(value)+"\n");
		}
		if(value instanceof String) {
			return new BString((String)value);
		}
	    return new BString(toString(value));
		
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
			
			
			return new BString(currentBodger.name +"." +defVar.name);
		}
		else {
			throw new RuntimeException("Flummoxed: Variable definition inncorrect.");
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
		return new BString(name);
	}
	
	
	@Override
	public Object visitIf(Stmt.If if1) {
		Object value = null;
		if (isBool(if1.condition.accept(this))) {
			value = if1.thenStmt.accept(this);
		}
		else if(if1.elseStmt != null) {
			value = if1.elseStmt.accept(this);
		}
		return value;
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
		return isBool(logical.right.accept(this));
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
		return  new BString("nowt");
	}
	@Override
	public Object visitWhile(While while1) {
		Object condition = while1.condition.accept(this);
		switch(while1.form) {
			case 0: 
				while(!isBool(condition)) {
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
				}while(!isBool(condition));
				break;
		}
		return new BString("nowt");
	}
	@Override
	public Object visitCallExpr(Call call) {
		Object called = call.called.accept(this);
		List<Object> args = new ArrayList<>();
		for (Expr arg : call.args) {
			args.add(arg.accept(this));
		}
		
		if(!(called instanceof EyupFunction)) {
			throw new RuntimeException("Flummoxed: Can only call functions");
		}
		EyupFunction callable = (EyupFunction)called;
		
		if(args.size() != callable.arity()) {
			throw new RuntimeException("Flummoxed: Expected " +
					callable.arity() + " arguments but got " +
			          args.size());
		}
		
		return callable.call(this, args);
		
	}

	@Override
	public Object visitInstanceExpr(Instance instance) {
		Object called = instance.called.accept(this);
		List<Object> args = new ArrayList<>();
		if(!(called instanceof EyupBodger)) {
			throw new RuntimeException("Vexed: Error not a Bodger!");
		}
		EyupBodger callable = (EyupBodger)called;
		Object bodger = callable.call(this,args);
		
		EyupInstance inst = ((EyupInstance)bodger);
		EyupBodger previous = currentBodger;
		if(!(instance.args.isEmpty())) {
			currentBodger =inst.getBodger();
			for (Expr arg : instance.args) {
				arg.accept(this);
			}
			currentBodger = previous;
		}
		
		return bodger;
		
	}
	 

	@Override
	public Object visitFunction(Function function) {
		EyupFunction fun = new EyupFunction(function,currentBodger);
		currentBodger.rt.define(function.name, new EnvVar(fun, fun.type()));
		return new BString(currentBodger.name +"." +function.name);
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
			currentBodger.rt = currentBodger.globals;
			for(Stmt stmt:stmts) {
				stmt.accept(this);
			}
		}finally {
			currentBodger = previous;
			currentBodger.rt = currentBodger.globals;
		}
		
		
	}
	@Override
	public Object visitReturn(Return return1) {
		
		Object value = null;
		
		if(return1.value!= null) value = return1.value.accept(this);
		throw new runtime.Return(value);
	}
	
	private void checkNumOperand(Tokens op, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeException("Flummoxed: bad'un Number : "+op);
	}
	private void checkStringOperands(Tokens op, Object left, Object right) {
		if (left instanceof String && right instanceof String) return;
		throw new RuntimeException("Flummoxed: bad'un Script : "+op);
	}
	private void checkNumOperands(Tokens op, Object left, Object right) {
	if (left instanceof Double && right instanceof Double) return;
	throw new RuntimeException("Flummoxed: bad'un Number : "+op);
	}
	private boolean isBool(Object object) {
	    if (object == null) throw new RuntimeException("Flummoxed: bad'un Answer");
	    if (object instanceof Boolean) return (boolean)object;
	    throw new RuntimeException("Flummoxed: bad'un Answer");
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
		if (value instanceof Double) {
			return Type.Double;
		}
		if (value instanceof String) {
			return Type.String;
		}
		if (value instanceof Character) {
			return Type.Char;
		}
		if (value instanceof Boolean ) {
			return Type.Bool;
		}
		if (value instanceof EyupInstance ) {
			return Type.Instance;
		}
		return null;
	}
	//Method to validate input to binary operators
	@SuppressWarnings("unused")
	private Boolean validate(Object left, Object right, Tokens op) {
		return null;
	}
	
	private Tokens getType(Object value) {

		switch(checkType(value)) {
		case Int: case Float: case Double: return Tokens.NUMBER;
		case Char: return Tokens.LETTER;
		case String: return Tokens.SCRIPT;
		case Bool: return Tokens.ANSWER;
		case Instance: return Tokens.INSTANCE;
		default: throw new RuntimeException("Flummoxed: unknown type");
		}
		
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
		
		
	}
	@Override
	public Object visitBodger(Bodger bodger) {
		currentBodger.rt.define(bodger.name, new EnvVar(null, Tokens.BODGER));
		EyupBodger bodg = new EyupBodger(bodger.name);
		currentBodger.rt.assign(bodger.name, new EnvVar(bodg, Tokens.BODGER));
		
		return new BString(currentBodger.name +": " +bodger.name);
	}
	@Override
	//Might need to adapt for static versions
	public Object visitGetExpr(Get get) {
		Object object = get.object.accept(this);
		if(object instanceof EyupInstance) {
			return ((EyupInstance) object).get(get.name,get); 
		}
		throw new RuntimeException("Flummoxed: "+ get.name+ " only instances have properties");
		
	}
	@Override
	public Object visitEyup(EyupCall eyupCall) {
		Object newBodger = currentBodger.rt.get(eyupCall.name); 
		if(newBodger instanceof EyupBodger) {
			EyupBodger bodge = (EyupBodger)newBodger;
			bodge.parent = currentBodger;
			this.currentBodger = bodge;
			return new BString(bodge.name+": "+"eyup");
		}
		
		throw new RuntimeException("Flummoxed: "+"not a bodger");
		
	}
	@Override
	public Object visitSithe(SitheCall sitheCall) {
		EyupBodger curBodge = currentBodger.parent;
		if(curBodge == null) {
			throw new core.Leave("Goodbye");
		}
		else {
			String name = currentBodger.name;
			this.currentBodger = curBodge;
			return new BString(name+": sithee");
		}
	}
	@Override
	public Object visitMissenExpr(Missen missen) {
		return currentBodger.lookUpVariable("this",missen);
		
	}
	@Override
	public Object visitGander(Gander gander) {
		String output = "nowt";
		if(!(gander.varName == null)) {
			Object ret = currentBodger.globals.get(gander.varName);
			if(ret instanceof EnvVar) {
				Tokens type = ((EnvVar)ret).type;
			}
			System.out.print(ret);
		}
		if (currentBodger.globals.notEmpty()) {
			Iterator<String> values = currentBodger.globals.getValues().iterator();
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
	@Override
	public Object visitStringAccess(StringAccess stringAccess) {
		Object value = stringAccess.name.accept(this);
		Object index = stringAccess.index.accept(this);
		if(value instanceof String && index instanceof Integer) {
			return ((String)value).charAt((Integer)index);
		}
		throw new RuntimeException("Vexed: "
				+ value + " aint String or "
						+ index + " aint Number");
	}
	/**
	@Override
	public Object visitCharAccess(CharAccess charAccess) {
		if(charAccess.name instanceof StringAccess) {
			if(charAccess.index instanceof Expr.Primary) {
				Object index = ((Expr.Primary)charAccess.).accept(this);
				if(index instanceof Integer) {
					Integer index = (Integer)index;
					Object name = (( Expr.StringAccess)charAccess.name).name;
					if(name instanceof Expr.Var) {
						Object thename = ((Expr.Var)name).accept(this);
						if(thename instanceof String) {
							StringBuilder newString = new StringBuilder(((String)thename));
							String newValue = (String)newString.setCharAt((Integer)index, (char)charAccess.value.accept(this));
						}
				}
				
				
			}
		}
			
			
		}
		return null;
	}**/
	@Override
	public Object visitCharAccess(CharAccess charAccess) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
