package ast;

import java.util.List;

import enums.Tokens;
import parser.FParam;

public interface Stmt {
	
	public class Read implements Stmt {
		final String var;
		public Read(String var) {
			this.var = var;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitRead(this);
		}

	}

	public class Gander implements Stmt {
		
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitGander(this);
		}

	}


	public Object accept(NodeVisitor visitor);
	
	class Expression implements Stmt {
		public Expression(Expr expr) {
			this.expr = expr;
		}
		public Object accept(NodeVisitor visitor) {
			return visitor.visitExprStmt(this);
		}
		final Expr expr;		
	}
	
	class Print implements Stmt {
		public Print(Expr expr, int i) {
			this.expr = expr;
			this.type = i;
		}
		public Object accept(NodeVisitor visitor) {
			return visitor.visitPrintStmt(this);
		}
		final Expr expr;	
		final int type;
	}
	//DEF VAR name(string), type(tokens), value(expr)
	//Parser works out var type if not implicitly defined
	class DefVar implements Stmt {
		public DefVar(String name, Expr init) {
			this.name = name;
			this.init = init;
		}
		public Object accept(NodeVisitor visitor) {
			return visitor.visitDefVar(this);
		}
		final String name;		
		Expr init;
		
	}
	class ForgetVar implements Stmt {
		public ForgetVar(String name) {
			this.name = name;
			
		}
		public Object accept(NodeVisitor visitor) {
			return visitor.visitForgetVar(this);
		}
		final String name;		
		
	}
	class If implements Stmt{
		Expr condition;
		Stmt thenStmt,elseStmt;
		public If(Expr condition, Stmt thenStmt, Stmt elseStmt) {
			this.condition = condition;
			this.thenStmt = thenStmt;
			this.elseStmt = elseStmt;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitIf(this);
		}
		
	}
	class When implements Stmt{
		List<Expr> conditions;
		List<Stmt> stmts;
		Stmt elseStmt;
		public When(List<Expr> conditions, List<Stmt> stmts, Stmt elseStmt) {
			this.conditions = conditions;
			this.stmts = stmts;
			this.elseStmt = elseStmt;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitWhen(this);
		}
		
	}
	class While implements Stmt{
		Expr condition;
		List<Stmt> body;
		int form;
		
		public While(Expr condition, List<Stmt> body, int i) {
			this.condition = condition;
			this.body=body;
			form = i;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitWhile(this);
		}
		
	}
	
	class Function implements Stmt{
		String name;
		public List<FParam> params;
		public Block body;
		public Tokens type;
		public Function(String name, List<FParam> params, List<Stmt> body, Tokens type) {
			this.name = name;
			this.params = params;
			this.body = new Block(body); 
			this.type = type;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitFunction(this);
		}
	}
	class Block implements Stmt{
		public List<Stmt> stmts;
		public Block(List<Stmt> stmts) {
			this.stmts = stmts; 
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitBlock(this);
		}
	}
	class Return implements Stmt{
		Expr value;
		public Return(Expr value) {
			this.value = value; 
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitReturn(this);
		}
	}
	class EyupCall implements Stmt{
		String name;
		public EyupCall(String name) {
			this.name = name;
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitEyup(this);
		}
	}
	class SitheCall implements Stmt{
		
		public SitheCall() {
			
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitSithe(this);
		}
	}
	
	
	class Bodger implements Stmt{
		String name;
		public Bodger(String name) {
			this.name = name;
		
		}
		@Override
		public Object accept(NodeVisitor visitor) {
			return visitor.visitBodger(this);
		}
	}
	
	
}
























