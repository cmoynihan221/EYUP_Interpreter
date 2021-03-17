package ast;

import ast.Stmt.DefVar;
import ast.Expr.Assignment;
import ast.Expr.Binary;
import ast.Expr.Group;
import ast.Expr.Primary;
import ast.Expr.Unary;
import ast.Expr.Var;
import ast.Stmt.Expression;
import ast.Stmt.ForgetVar;
import ast.Stmt.Print;

public interface NodeVisitor {
	public Object visitBinaryExpr(Binary expr);
	public Object visitGroupExpr(Group expr);
	public Object visitPrimaryExpr(Primary expr);
	public Object visitUnaryExpr(Unary expr);
	public Object visitExprStmt(Expression expr);
	public Object visitPrintStmt(Print stmt);
	public Object visitDefVar(DefVar defVar);
	public Object visitVarExpr(Var var);
	public Object visitAssignmentExpr(Assignment assignment);
	public Object visitForgetVar(ForgetVar forgetVar);
}