package ast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import ast.Stmt.Function;
import ast.Stmt.Gander;
import ast.Stmt.If;
import ast.Stmt.Print;
import ast.Stmt.Return;
import ast.Stmt.SitheCall;
import ast.Stmt.When;
import ast.Stmt.While;
import enums.Tokens;
import parser.FParam;

@SuppressWarnings("unused")
public class Resolver implements NodeVisitor {
	private final Interpreter interpreter;
	  private final Stack<Map<String, Boolean>> scopes = new Stack<>();

	public Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Object visitBinaryExpr(Binary expr) {
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}
	
	@Override
	public Object visitGroupExpr(Group expr) {
		resolve(expr.expr);
		return null;
	}
	
	@Override
	public Object visitPrimaryExpr(Primary expr) {
		return null;
	}
	
	@Override
	public Object visitUnaryExpr(Unary expr) {
		resolve(expr.right);
		return null;
	}
	
	@Override
	public Object visitExprStmt(Expression expr) {
		resolve(expr.expr);
		return null;
	}
	
	@Override
	public Object visitPrintStmt(Print stmt) {
		resolve(stmt.expr);
		return null;
	}
	
	@Override
	public Object visitDefVar(DefVar defVar) {
		declare(defVar.name);
		if(defVar.init != null) {
			resolve(defVar.init);
		}
		define(defVar.name);
		return null;
	}
	
	@Override
	public Object visitVarExpr(Var var) {
		if(!scopes.isEmpty() && scopes.peek().get(var.name) == Boolean.FALSE) {
			throw new RuntimeException("Can't read local variable in its own initializer.");
		}
		resolveLocal(var, var.name);
		return null;
	}
	
	@Override
	public Object visitAssignmentExpr(Assignment assignment) {
		resolve(assignment.value);
		resolveLocal(assignment, assignment.name);
		return null;
	}
	
	@Override
	public Object visitForgetVar(ForgetVar forgetVar) {
		
		return null;
	}
	
	@Override
	public Object visitIf(If if1) {
		resolve(if1.condition);
		resolve(if1.thenStmt);
		if(if1.elseStmt != null) resolve(if1.elseStmt);
		return null;
	}
	
	@Override
	public Object visitLogicExpr(Logical logical) {
		resolve(logical.left);
		resolve(logical.right);
		return null;
	}
	
	@Override
	public Object visitWhen(When when) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visitWhile(While while1) {
		resolve(while1.condition);
		resolve(while1.body);
		return null;
	}
	
	@Override
	public Object visitCallExpr(Call call) {
		resolve(call.called);
		for(Expr arg : call.args) {
			resolve(arg);
		}
		return null;
	}
	
	@Override
	public Object visitFunction(Function function) {
		declare(function.name);
		define(function.name);
		resolveFunction(function);
		return null;
	}
	
	@Override
	public Object visitBlock(Block block) {
		beginScope();
		resolve(block.stmts);
		endScope();
		return null;
	}
	
	
	@Override
	public Object visitReturn(Return return1) {
		if(return1.value != null) {
			resolve(return1.value);
		}
		return null;
	}
	private void resolveLocal(Expr expr, String name) {
		for(int i = scopes.size()-1;i>=0;i--) {
			if(scopes.get(i).containsKey(name)) {
				interpreter.resolve(expr,scopes.size()-1-i);
				return;
			}
		}
	}
	private void declare(String name) {
		if(scopes.isEmpty()) return;
		Map<String, Boolean> scope = scopes.peek();
		if(scope.containsKey(name)) {
			throw new RuntimeException("Scope already contains variable.");
		}
		scope.put(name,false);
	}
	private void define(String name) {
		if(scopes.isEmpty()) return;
		scopes.peek().put(name, true);
	}
	private void resolveFunction(Stmt.Function function) {
		beginScope();
		if(function.params !=null) {
		for(FParam paraType: function.params) {
			for(String para:paraType.params ) {
				declare(para);
				define(para);
			}
			resolve(function.body);
			endScope();
		}}
		else {
			endScope();
		}
	}
	
	private void beginScope() {
	    scopes.push(new HashMap<String, Boolean>());
	  	}
	private void endScope() {
		    scopes.pop();
	 	}
	public void resolve(List<Stmt> statements) {
	    for (Stmt statement : statements) {
	        resolve(statement);
	      }
	    }
	private void resolve(Stmt stmt) {
	    stmt.accept(this);
	    }
	private void resolve(Expr expr) {
	    expr.accept(this);
	    }

	@Override
	public Object visitBodger(Bodger bodger) {
		declare(bodger.name);
		define(bodger.name);
		beginScope();
		scopes.peek().put("this", true);
		endScope();
		
		return null;
	}

	@Override
	public Object visitGetExpr(Get get) {
		resolve(get.object);
		return null;
	}

	@Override
	public Object visitEyup(EyupCall eyupCall) {
		
		return null;
	}

	@Override
	public Object visitSithe(SitheCall sitheCall) {
		
		return null;
	}

	@Override
	public Object visitMissenExpr(Missen missen) {
		resolveLocal(missen, "this");
		return null;
	}

	@Override
	public Object visitGander(Gander gander) {
		
		return null;
	}

}
