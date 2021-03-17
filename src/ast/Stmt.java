package ast;

import enums.Tokens;

public interface Stmt {
	
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
		public Print(Expr expr) {
			this.expr = expr;
		}
		public Object accept(NodeVisitor visitor) {
			return visitor.visitPrintStmt(this);
		}
		final Expr expr;		
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

}
