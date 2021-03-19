package ast;
import enums.Tokens;

//Abstract Expression Cast With different expression types 
public interface Expr {
	
	//Visitor accept abstract method for expression class  
	public Object accept(NodeVisitor visitor);
	
	//Binary expression node
	//Left child, operator, right child
	class Binary implements Expr{
		public Binary(Expr left, Tokens op, Expr right) {
		      this.left = left;
		      this.op = op;
		      this.right = right;
		    }
		//accept method redirects to visitor 
		@Override
		public Object accept(NodeVisitor visitor) {
			 return visitor.visitBinaryExpr(this);
		}
	    final Expr left;
	    final Tokens op;
	    final Expr right;
	}
	//Group Expression Class
	 class Group implements Expr {
	    public Group(Expr expr) {
	      this.expr = expr;
	    }

	    @Override
	    public Object accept(NodeVisitor visitor) {
	    	return visitor.visitGroupExpr(this);
	    }

	    final Expr expr;
	  }
	//Unary Expression class
	 class Unary implements Expr {
	    public Unary(Tokens op, Expr right) {
	      this.op = op;
	      this.right = right;
	    }

	    @Override
	    public Object accept(NodeVisitor visitor) {
	    	return visitor.visitUnaryExpr(this);
	    }

	    final Tokens op;
	    final Expr right;
	  }
	//Primary expression class
	class Primary implements Expr {
	    public Primary(Tokens type, Object value) {
	      this.value = value;
	      this.type = type;
	    }
	    

	    @Override
	    public Object accept(NodeVisitor visitor) {
	    	return visitor.visitPrimaryExpr(this);
	    }
	    

	    Object value;
	    final Tokens type;
	  }
	 class Var implements Expr {
	    public Var(String name) {
	      this.name = name;
	    }

	    @Override
	    public Object accept(NodeVisitor visitor) {
	    	return visitor.visitVarExpr(this);
	    }

	    public final String name;
	  }
	 
	 class Assignment implements Expr{
		 public Assignment(String name, Expr value) {
			 this.name = name;
			 this.value = value;
		 }
		 
		 @Override
	    public Object accept(NodeVisitor visitor) {
			 return visitor.visitAssignmentExpr(this);
	    }
		 
		 final String name;
		 final Expr value;
	 }
	
	 class Logical implements Expr{
			public Logical(Expr left, Tokens op, Expr right) {
			      this.left = left;
			      this.op = op;
			      this.right = right;
			    }
			//accept method redirects to visitor 
			@Override
			public Object accept(NodeVisitor visitor) {
				 return visitor.visitLogicExpr(this);
			}
		    final Expr left;
		    final Tokens op;
		    final Expr right;
		}

	
	
}
