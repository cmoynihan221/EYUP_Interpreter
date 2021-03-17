package ast;

import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Group;
import ast.Expr.Primary;
import ast.Expr.Unary;
import ast.Expr.Var;
import ast.Stmt.DefVar;
import ast.Stmt.Expression;
import ast.Stmt.ForgetVar;
import ast.Stmt.Print;

public class NodePrintVisitor implements NodeVisitor{
	
	@Override
	public String visitBinaryExpr(Binary expr) {
		System.out.print(expr.op);
		System.out.print("(");
		expr.left.accept(new NodePrintVisitor());
		System.out.print(",");
		expr.right.accept(new NodePrintVisitor());
		System.out.print(")");
		return null;
	}
	@Override
	public String visitGroupExpr(Group expr) {
		expr.expr.accept(new NodePrintVisitor());	
		return null;
	}
	@Override
	public String visitPrimaryExpr(Primary expr) {
		System.out.print(expr.type+":");
		System.out.print(expr.value);
		return null;
	}
	@Override
	public String visitUnaryExpr(Unary expr) {
		System.out.print(expr.op);
		System.out.print("(");
		expr.right.accept(new NodePrintVisitor());
		System.out.print(")");
		return null;
	}
	@Override
	public String visitExprStmt(Expression expr) {
		System.out.print("\nexpression(");
		expr.expr.accept(new NodePrintVisitor());
		System.out.print(")");
		return null;
	}
	@Override
	public String visitPrintStmt(Print stmt) {
		System.out.print("\nprint(");
		stmt.expr.accept(new NodePrintVisitor());
		System.out.print(")");
		return null;
		
	}
	@Override
	public String visitDefVar(DefVar assignVar) {
		System.out.print("\nAssign(");
		System.out.print(assignVar.name+":");
		assignVar.init.accept(new NodePrintVisitor());
		System.out.print(")");
		return null;
		
	}
	@Override
	public String visitVarExpr(Var var) {
		System.out.print(var.name);
		return null;
		
	}
	@Override
	public Object visitAssignmentExpr(Assignment assignment) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visitForgetVar(ForgetVar forgetVar) {
		// TODO Auto-generated method stub
		return null;
	}
}
