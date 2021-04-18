package parser;

import java.util.ArrayList;
import java.util.List;

import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
import ast.*;
import ast.Stmt;


public class Parser {
	int current ; 
	int currentValue; 
	OutputTuple input;
	String currentFunction = null;
	
	
	
	public ArrayList<Stmt> parseInput(OutputTuple output) {
		ArrayList<Stmt> parsedStatements = new ArrayList<>();
		input = output;
		current = 0; 
		currentValue = 0; 
		
		try {
			while (!isAtEnd()) {
				parsedStatements.add(definition());
			}
			return parsedStatements;
			
		} catch(RuntimeException error) {
			
			return parsedStatements;
			
		}
	}
	private Stmt definition() {
		
		Tokens[] summat = {Tokens.SUMMAT};
		Tokens[] fettle = {Tokens.FETTLE};
		Tokens[] bodger = {Tokens.BODGER};
		try {
			if(match(fettle))return function("function");
			if(match(summat)) return varDef();
			if(match(bodger)) return classDef();
			return statement();
		} catch(RuntimeException error) {
			System.out.println(error);
			throw error("assignment error");
			
		}
	}
	private Stmt classDef() {
		takeToken(Tokens.ID, "Expected identifier");
		String name = advanceValue();
		return new Stmt.Bodger(name);
	}
	private Stmt.Function function(String string) {
		takeToken(Tokens.ID,"Expect function name");
		String name = advanceValue();
		currentFunction = name;
		List<FParam> params = null;
		Integer total = 0;
		//If match with (
		if(match(new Tokens[]{Tokens.L_PAREN})) {
			params = new ArrayList<>();
			//Loop for parameters of different types
			do {
				List<String> p = new ArrayList<>(); 
				//Loop for all parameters of a type
				do {
					//Check not larger than max
					if(total >= 255) {
						error("255 too many params.");
					}
					if(match(new Tokens[]{Tokens.ID})) {
						p.add(advanceValue());
						total ++;
					}
				//Loop while match comma
				}while(match(new Tokens[]{Tokens.COMMA}));
				
				Tokens[] tokens = {Tokens.SCRIPT, Tokens.LETTER, Tokens.NUMBER, Tokens.ANSWER, Tokens.NONE};
				Tokens type = null;
				//If match type definition
				if(match(new Tokens[]{Tokens.COLON})) {
					//Match to type
					if(match(tokens)) {
						type = previous();
					}else {
						error("Incorrect Type definition");
					}
				}
				//Add to params
				FParam pType = new FParam(p, type);
				params.add(pType);
			} while(match(new Tokens[]{Tokens.COMMA}));
			takeToken(Tokens.R_PAREN,"Expect ')'");
			
		}
		//check type of function return
		Tokens type = null;
		
		if(match(new Tokens[]{Tokens.COLON})) {
			//Match to type
			Tokens[] tokens = {Tokens.SCRIPT, Tokens.LETTER, Tokens.NUMBER, Tokens.ANSWER, Tokens.NONE};
			if(match(tokens)) {
				type = previous();
			}else {
				error("Incorrect Type definition");
			}
		}
		List<Stmt>  body = null;
		if(match(new Tokens[] {Tokens.GIZ})) {
			body = block();
		}
		else if(match(new Tokens[] {Tokens.GIVEOVER})) {
			body = null;
		} else {
			error("Expected giz or gizoer");
		}
		currentFunction = null;
		return new Stmt.Function(name, params, body, type);
	}
	private List<Stmt> block(){
		List<Stmt> statements = new ArrayList<>();
		
	    while (!check(Tokens.OVER) && !isAtEnd()) {
	    	
	      statements.add(definition());
	    }
	    
	    takeToken(Tokens.OVER, "Expect 'oer' after block.");
	    
	    return statements;
	}
	//Declaring a variable in EYUP
	private Stmt varDef() {
		//Check for ID
		if (match(new Tokens[]{Tokens.ID})) {
			String name = (String) advanceValue();
			//If : then look for type declaration otherwise throw type error 
			if(match(new Tokens[]{Tokens.COLON})) {
				//Check for type declaration
				Tokens[] tokens = {Tokens.SCRIPT, Tokens.LETTER, Tokens.NUMBER, Tokens.ANSWER, Tokens.NONE};
				if(match(tokens)) {
					Tokens type = previous();
					return new Stmt.DefVar(name,new Expr.Primary(type, null));
				}else {
					//type error
					throw error("Variable Definition Error, Incorrect Type Defition.");
				}
			//if := look for expression otherwise throw definition error	
			}
			//Check for assignment
			if(match(new Tokens[]{Tokens.COLON_EQ})) {
				Expr value = expression();
				return new Stmt.DefVar(name, value);
				
			}else {
				throw error("Incorrect variable definition.");
			}
		}
		else {throw error("Error at variable name");}	
	}
	
	private Stmt statement() {
		
		if(peek() == Tokens.ID) {
			
			//System.out.println(peekValue().compareTo(currentFunction)==0);
			if (currentFunction!=null &&peekValue().compareTo(currentFunction)==0) {
				match(new Tokens[]{Tokens.ID});
				return returnStmt();
			}
		}
		if (match(new Tokens[]{Tokens.WRITE})) {
			return printStmt();
		}
		if(match(new Tokens[]{Tokens.FORGET})) {
			return forgetStmt();
		}
		if(match(new Tokens[]{Tokens.IF})) {
			return ifStatement();
		}
		if(match(new Tokens[]{Tokens.WHEN})) {
			return whenStatement();
		}
		if(match(new Tokens[]{Tokens.WHILE})) {
			return whileStatement();
		}
		if(match(new Tokens[]{Tokens.GOWON})) {
			return gowanStatement();
		}
		if(match(new Tokens[] {Tokens.EYUP})) {
			return eyupStatement();
		}
		if(match(new Tokens[] {Tokens.SITHE})) {
			return sitheStmt();
		}
		
		return exprStmt();
		
	}
	
	private Stmt sitheStmt() {
		return new Stmt.SitheCall();
			
	}
	
	private Stmt eyupStatement() {
		if(match(new Tokens[] {Tokens.ID})) {
		String name = advanceValue(); 
		return new Stmt.EyupCall(name);}
		throw error("NOT A NAME");
	}
	private Stmt returnStmt() {
		
		String name = advanceValue(); 
		Expr value = null;
		takeToken(Tokens.COLON_EQ, "Expect  ':=' for return");
		
		value = expression();
		
		return new Stmt.Return(value);
	}
	private Stmt forgetStmt() {
		if (match(new Tokens[]{Tokens.ID})) {
			String name = (String) advanceValue();
			return new Stmt.ForgetVar(name);
		}
		throw error("Error at Variable name");
	}
	private Stmt exprStmt() {
		Expr expr = expression();
		return new Stmt.Expression(expr);
	}

	private Stmt printStmt() {
		Tokens[] token = {Tokens.L_PAREN};
		if (match(token)) {
			Expr literal = expression();
			takeToken(Tokens.R_PAREN,"Ey up! Was expecting ')'");
			return new Stmt.Print(literal);
		}
		else {
			throw error("Print");
		}
	}
	
	private Stmt ifStatement() {
		Expr condition = expression();
		takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
		Stmt branch1 = statement();
		Stmt branch2 = null;
		if(match(new Tokens[]{Tokens.ELSE})) {
			branch2 = statement();
		}
		takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		return new Stmt.If(condition, branch1, branch2);
	}
	private Stmt whenStatement() {
		List<Expr> conditions = new ArrayList<>();
		List<Stmt> branches= new ArrayList<>();
		conditions.add(expression());
		takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
		branches.add(statement());
		while(match(new Tokens[]{Tokens.WHEN})) {
			conditions.add(expression());
			takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
			branches.add(statement());
		}
		Stmt branch2 = null;
		if(match(new Tokens[]{Tokens.ELSE})) {
			branch2 = statement();
		}
		takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		return new Stmt.When(conditions, branches, branch2);
		
	}
	private Stmt whileStatement() {
		Expr condition = expression();
		List<Stmt> statements = new ArrayList<>();
		takeToken(Tokens.GOWON,"Ey up! Was expecting 'gowon'");
		statements = block();
		//takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		return new Stmt.While(condition, statements);
	}
	
	private Stmt gowanStatement() {
		return null;
	}
	
	private Expr expression() {
		return assignment();
	}
	private Expr assignment() {
		Tokens[] tokens = {Tokens.COLON_EQ};
		Expr expr = or();
		if(match(tokens)) {
			Expr value = assignment();
			if(expr instanceof Expr.Var) {
				String name = ((Expr.Var)expr).name;
				return new Expr.Assignment(name, value);
			} else if(expr instanceof Expr.Get) {
				error("Vexed: faffin' wi' " + ((Expr.Get)expr).name);
			}
			
			error("Assignment error");
		}
		return expr;
		
	}
	private Expr or() {
		Expr expr = and();
	
	    while (match(new Tokens[]{Tokens.OR})) {
	      Tokens op = previous();
	      Expr right = and();
	      expr = new Expr.Logical(expr, op, right);
	    }
	
	    return expr;
	}
	private Expr and() {
		Expr expr = equality();
		
	    while (match(new Tokens[]{Tokens.AND})) {
	      Tokens op = previous();
	      Expr right = equality();
	      expr = new Expr.Logical(expr, op, right);
	    }
	
	    return expr;
	}
	private Expr equality() {
		Tokens[] tokens = {Tokens.EQUAL,Tokens.EXCLA_EQ};
		Expr expr = comparison();
		while (match(tokens)) {
			Tokens op = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, op, right);
		}
		return expr;
	}
	private Expr comparison() {
		Tokens[] tokens = {Tokens.LESS,Tokens.GREAT,Tokens.GREAT_EQ,Tokens.LESS_EQ};
		Expr expr = concat();
		while(match(tokens)) {
			Tokens op = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, op, right);
		}
		return expr;
		
	}
	private Expr concat() {
		Tokens[] tokens = {Tokens.DOLLA};
		Expr expr = term();
		while(match(tokens)) {
			Tokens op = previous();
			Expr right = primary();
			expr = new Expr.Binary(expr, op, right);
		}
		return expr;
	}
	private Expr term() {
		Tokens[] tokens = {Tokens.MINUS, Tokens.PLUS};
		Expr expr = factor();
		while(match(tokens)) {
			Tokens op = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, op, right);
		}
		return expr;
	}
	private Expr factor() {
		Tokens[] tokens = {Tokens.STAR, Tokens.FSLASH};
		Expr expr = unary();
		while(match(tokens)) {
			Tokens op = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, op, right);
		}
		return expr;
	}
	private Expr unary() {
		Tokens[] tokens = {Tokens.EXCLAMATION, Tokens.MINUS};
		
		while(match(tokens)) {
			Tokens op = previous();
			Expr right = group();
			return new Expr.Unary(op, right);
		}
		return instantiateCall();
	}
	private Expr instantiateCall() {
		if(match(new Tokens[]{Tokens.EYUP})) {
			List<Expr> arguments = new ArrayList<>();
			Expr expr = group();
			
			if (match(new Tokens[]{Tokens.L_PAREN})) {
				expr = endCall(expr);
				return expr;
			}else{
				return new Expr.Call(expr,arguments);
			}
		}
		return call();
	}
	private Expr call(){
		
		Expr expr = group();
		while(true) {
			if (match(new Tokens[]{Tokens.L_PAREN})) {
				expr = endCall(expr);
			}else if (match(new Tokens[]{Tokens.PERIOD})) {
				takeToken(Tokens.ID, "Expected name.");
				String name = advanceValue();
				expr = new Expr.Get(expr, name);
			}else {
				break;
			}
		}
		return expr;
	}
	 private Expr endCall(Expr called) {
		    List<Expr> arguments = new ArrayList<>();
	
		    if (!check(Tokens.R_PAREN)) {
		      do {
		    	  if(arguments.size()>=255) {
		    		  error("Can't have more than 255 arguments. ");
		    	  }

		        arguments.add(expression());
		      } while (match(new Tokens[]{Tokens.COMMA}));
		    }

		    takeToken(Tokens.R_PAREN,"Expect ')' after arguments.");

		    return new Expr.Call(called, arguments);
		  }
	private Expr group() {
		if (match(new Tokens[]{Tokens.L_PAREN})) {
			Expr expr = expression();
			takeToken(Tokens.R_PAREN,"group Expect ')'.");
			return new Expr.Group(expr);
			}
		else {
			return primary();
		}
	}
	
	
	private Expr primary() {	
	//debug("primaryt");
		if (match(new Tokens[]{Tokens.STRING})) {
			String value = (String)advanceValue();
			if(value.length() == 1) {
				return new Expr.Primary(Tokens.LETTER,value);
			}
			else {
			return new Expr.Primary(Tokens.SCRIPT,value);}
		}
		if (match(new Tokens[]{Tokens.INTEGER})) {
			return new Expr.Primary(Tokens.NUMBER,Integer.parseInt((String)advanceValue()));}
		if (match(new Tokens[]{Tokens.FLOAT})) {
			return new Expr.Primary(Tokens.NUMBER,Float.parseFloat((String)advanceValue()));}
		if (match(new Tokens[]{Tokens.TRUE})) {
			return new Expr.Primary(Tokens.ANSWER, true);}
		if (match(new Tokens[]{Tokens.FALSE})) {
			return new Expr.Primary(Tokens.ANSWER,false);}
		if (match(new Tokens[]{Tokens.NONE})) {
			return new Expr.Primary(Tokens.NONE,null);}
		if (match(new Tokens[]{Tokens.ID})) {
			return new Expr.Var((String)advanceValue());
			}
		//debug("primaryt");
		throw error("primary");
	}
	public boolean match(Tokens[] tokens) {
		for (Tokens type: tokens) {
			  if(check(type)) {
				  advance();
				  return true;
			  }
			}
		return false;
	}

	private Tokens advance() {
		if(!isAtEnd()) {
			current++;
		}	
		return previous();
		
	}
	private String advanceValue() {
		String value = (String)input.tokenValues.get(currentValue);
		currentValue++;
		return value; 
	}

	private boolean check(Tokens token) {
		if(isAtEnd()) {
			return false;
		}
		return(peek() == token);
		
	}

	private Tokens peek() {
		return input.tokens.get(current);
	}
	private String peekValue() {
		return (String)input.tokenValues.get(currentValue);
	}

	private boolean isAtEnd() {
		return (peek() == Tokens.EOI);
	}
	private Tokens previous() {
		return input.tokens.get(current-1);
	} 
	private Tokens takeToken(Tokens type, String message) {
		if(check(type)) return advance();
		throw error(message);
	}
	
	private RuntimeException error(String error) {
	    core.Error.parseError(error);
		return new RuntimeException();
	}
	@SuppressWarnings("unused")
	private void debug(String function) {
		System.out.println("Function: "+function);
		System.out.println("Token: "+peek());
	}
	
	
	//Not Printing program
	public static void main(String[] args) {
		Parser p = new Parser();
		Lexer l = new Lexer();
		lexer.OutputTuple lexed = l.lexString("fettle program(n:Number):Letter giz write(\"Hello\") program = \"a\" oer");
		lexed.tokens.add(Tokens.EOI);
		p.parseInput(lexed);
		
		
		
		}
}
