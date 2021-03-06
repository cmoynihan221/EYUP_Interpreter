package ast;

import ast.Expr.*;
import ast.Stmt.*;




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
	public Object visitIf(If if1);
	public Object visitLogicExpr(Logical logical);
	public Object visitWhen(When when);
	public Object visitWhile(While while1);
	public Object visitCallExpr(Call call);
	public Object visitFunction(Function function);
	public Object visitBlock(Block block);
	public Object visitReturn(Return return1);
	public Object visitBodger(Bodger bodger);
	public Object visitGetExpr(Get get);
	public Object visitEyup(EyupCall eyupCall);
	public Object visitSithe(SitheCall sitheCall);
	public Object visitMissenExpr(Missen missen);
	public Object visitGander(Gander gander);
	public Object visitRead(Read read);
	public Object visitInstanceExpr(Instance instance);
	public Object visitStringAccess(StringAccess stringAccess);
	public Object visitCharAccess(CharAccess charAccess);
	
}