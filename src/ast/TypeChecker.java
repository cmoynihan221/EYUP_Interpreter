package ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Call;
import ast.Expr.CharAccess;
import ast.Expr.Get;
import ast.Expr.Group;
import ast.Expr.Instance;
import ast.Expr.Logical;
import ast.Expr.Missen;
import ast.Expr.Primary;
import ast.Expr.StringAccess;
import ast.Expr.Unary;
import ast.Expr.Var;
import ast.Stmt.Block;
import ast.Stmt.Bodger;
import ast.Stmt.DefVar;
import ast.Stmt.Expression;
import ast.Stmt.EyupCall;
import ast.Stmt.ForgetVar;
import ast.Stmt.Function;
import ast.Stmt.Gander;
import ast.Stmt.If;
import ast.Stmt.Print;
import ast.Stmt.Read;
import ast.Stmt.Return;
import ast.Stmt.SitheCall;
import ast.Stmt.When;
import ast.Stmt.While;
import enums.Tokens;
import enums.Type;
import parser.FParam;
import runtime.EyupBodger;
import runtime.EyupInstance;

public class TypeChecker implements NodeVisitor {
	private  Map<String, Object> current = new HashMap<>();
	private final Stack<Map<String, Object>> scopes = new Stack<>();
	
	public void check(List<Stmt> statements) {
		try {
	    for (Stmt statement : statements) {
	        checkStmt(statement);
	      }}
		catch(RuntimeException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
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
		if (value instanceof EyupInstance ) {
			return Type.Instance;
		}
		if (value instanceof EyupBodger ) {
			return Type.Bodger;
		}
		throw new RuntimeException("Unknown Type");
	}
	private Tokens getType(Object value) {

		switch(checkType(value)) {
		case Int: case Float: return Tokens.NUMBER;
		case Char: return Tokens.LETTER;
		case String: return Tokens.SCRIPT;
		case Bool: return Tokens.ANSWER;
		case Instance: return Tokens.INSTANCE;
		case Bodger: return Tokens.BODGER;
		}
		throw new RuntimeException("Unknown Type");
	}
	
	public TypeChecker()  {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object visitBinaryExpr(Binary expr) {
		Object left = expr.left.accept(this);
		Object right = expr.right.accept(this);
		if(left.equals(right)) {
			return left;
		}
		else {
			throw new RuntimeException("Vexed: summat missmatch");
		}
	}

	@Override
	public Object visitGroupExpr(Group expr) {
		return checkExpr(expr.expr);
	}

	@Override
	public Object visitPrimaryExpr(Primary expr) {
		return expr.type;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		return checkExpr(expr.right);
	}

	@Override
	public Object visitExprStmt(Expression expr) {
		return checkExpr(expr.expr);
	}

	@Override
	public Object visitPrintStmt(Print stmt) {
		return checkExpr(stmt.expr);
	}

	@Override
	public Object visitDefVar(DefVar defVar) {
		String name = defVar.name;
		if(this.current.containsKey(name)) {
			throw new RuntimeException("Vexed: "
					+ defVar.name
					+ " already 'ere. ");
		}
		
		this.current.put(defVar.name, (Tokens)checkExpr(defVar.init));
		return null;
		
	}

	@Override
	public Object visitVarExpr(Var var) {
		if(this.current.containsKey(var.name)){
			return current.get(var.name);
		}
		
		throw new RuntimeException("Flummoxed: "
					+ var.name
					+ " aint 'ere");
	}	
	
	@Override
	public Object visitAssignmentExpr(Assignment assignment) {
		String var = assignment.name;
		if(this.current.containsKey(var)){
			Object left =(current.get(var));
			Object right = checkExpr(assignment.value);
			if(!(left == right)) {
				throw new RuntimeException("Vexed: "
						+ assignment.name
						+ " wi' bad'un: "
						+ right);
			}
			return null;
		}
		else {
			throw new RuntimeException("Flummoxed: "
					+ assignment.name
					+ " aint 'ere");
		}
		
	}

	@Override
	public Object visitForgetVar(ForgetVar forgetVar) {
		this.current.remove(forgetVar.name);
		return null;
	}

	@Override
	public Object visitIf(If if1) {
		checkExpr(if1.condition);
		checkStmt(if1.thenStmt);
		checkStmt(if1.elseStmt);
		return null;
	}

	@Override
	public Object visitLogicExpr(Logical logical) {
		try {
			Boolean left = (Boolean) checkExpr(logical.left);
			Boolean right = (Boolean) checkExpr(logical.right);
			return Tokens.ANSWER;
		}
		catch(ClassCastException c) {
			throw new RuntimeException("Vexed: needs Answer");
		}
	}

	@Override
	public Object visitWhen(When when) {
		for (Expr expr: when.conditions) {
			checkExpr(expr);
		}
		for (Stmt stmts: when.stmts) {
			checkStmt(stmts);
		}
		checkStmt(when.elseStmt);
		return null;
	}

	@Override
	public Object visitWhile(While while1) {
		checkExpr(while1.condition);
		for (Stmt stmts: while1.body) {
			checkStmt(stmts);
		}
		return null;
	}

	@Override
	public Object visitCallExpr(Call call) {
		
		Object called = checkExpr(call.called);
		if(called instanceof FunctionCall) {
			int counter= 0; 
			List<FParam> params = ((FunctionCall)called).parameters;
			if(params != null) {
				for(FParam param: params) {
					for(String p:param.params) {
						Tokens paramType = (Tokens) checkExpr(call.args.get(counter));
						if( paramType != param.type) {
							throw new RuntimeException("Vexed: "
									+ "bad'un: "
									+ paramType);
						}
						counter++;
					}
				}
				
			}
			return (((FunctionCall)called).returnType);	
		}
		throw new RuntimeException("Vexed: ain't a program");
		
	}

	private void addParams(Map<String, Object> scope, List<FParam> params) {
		for(FParam param: params) {
			for(String p:param.params) {
				scope.put(p,param.type);
			}
		}
	}
	
	
	@Override
	public Object visitFunction(Function function) {
		this.current.put(function.name, new FunctionCall(function.params,function.type));
		Map<String, Object> functionScope = new HashMap<>();
		functionScope.putAll(current);
		if(function.params != null) {
			addParams(functionScope, function.params);
		}
		scopes.push(current);
		current = functionScope;
		if (function.type == null) {
			checkStmt(function.body);
			current = scopes.pop();
			return null;
		}
		Tokens returnType = null;
		for (Stmt stmts: function.body.stmts) {
			if(stmts instanceof Return) {
				returnType = (Tokens)checkStmt(stmts);
				if(returnType != function.type) {
					throw new RuntimeException("Vexed: bad'un return type: "
							+ returnType);
				}
			}else {
				checkStmt(stmts);
			}
		}
		if(returnType == null) {
			throw new RuntimeException("Vexed: nowt return type: "
					+ function.type);
		}else {
			return null;
		}
		
	}

	@Override
	public Object visitBlock(Block block) {
		for (Stmt stmts: block.stmts) {
			checkStmt(stmts);
		}
		return null;
	}

	@Override
	public Object visitReturn(Return return1) {
		return checkExpr(return1.value);
	}

	@Override
	public Object visitBodger(Bodger bodger) {
		Tokens type = Tokens.BODGER;
		return type;
	}

	@Override
	public Object visitGetExpr(Get get) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEyup(EyupCall eyupCall) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSithe(SitheCall sitheCall) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMissenExpr(Missen missen) {
		return Tokens.BODGER;
	}

	@Override
	public Object visitGander(Gander gander) {
		return null;
	}

	@Override
	public Object visitRead(Read read) {
		return Tokens.SCRIPT;
	}

	@Override
	public Object visitInstanceExpr(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}
	public Object checkExpr(Expr expr) {
		return expr.accept(this);
	}
	public Object checkStmt(Stmt stmt) {
		return stmt.accept(this);
	}
	@Override
	public Object visitStringAccess(StringAccess stringAccess) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visitCharAccess(CharAccess charAccess) {
		// TODO Auto-generated method stub
		return null;
	}

}
