package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
import ast.*;
import ast.Stmt;
import core.Loop;


public class Parser {
	int spaceStack = 0;
	int current ; 
	int currentValue; 
	OutputTuple input;
	String currentFunction = null;
	Stack<String> functions = new Stack<>();
	
	private String spaces() {
		String returnString = "";
		for(int i=0; i<spaceStack;i++) {
			returnString = returnString + "  ";
		}
		return returnString;
	}
	
	private void requestInput() {
		if(isAtEnd()) {
			OutputTuple newInput = Loop.getInput(spaces());
			input.tokens.addAll(newInput.tokens);
			input.tokenValues.addAll(newInput.tokenValues);
			current++;
		}
	}
	
	public ArrayList<Stmt> parseInput(OutputTuple output) {
		ArrayList<Stmt> parsedStatements = new ArrayList<>();
		input = output;
		current = 0; 
		currentValue = 0; 	
		spaceStack = 0;
		try {
			while (!isAtEnd()) {
				parsedStatements.add(definition());
			}
			return parsedStatements;			
		} catch(RuntimeException error) {
			spaceStack = 0;
			System.out.print(error.getMessage()+"\n");
			return parsedStatements;
			
		}
	}
	private Stmt definition() {
		
		Tokens[] summat = {Tokens.SUMMAT};
		Tokens[] fettle = {Tokens.FETTLE};
		Tokens[] bodger = {Tokens.BODGER};
		try {
			if(match(fettle)) { this.spaceStack ++;return function("function"); }
			if(match(summat)) return varDef();
			if(match(bodger)) return classDef();
			return statement();
		} catch(RuntimeException error) {
			
			throw new RuntimeException(error.getMessage());
			
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
		if (currentFunction != null) {
			functions.push(currentFunction);
			currentFunction = name;
		}else {
			currentFunction = name;
		}
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
			requestInput();
			body = block();
		}
		else if(match(new Tokens[] {Tokens.GIVEOVER})) {
			body = null;
		} else {
			error("Expected giz or gizoer");
		}
		if(functions.empty()) {
			currentFunction = null;
		}else {
			currentFunction = functions.pop();
		}
		
		return new Stmt.Function(name, params, body, type);
	}
	private List<Stmt> block(){
		List<Stmt> statements = new ArrayList<>();
	    while (!check(Tokens.OVER) && !isAtEnd()) {
	      statements.add(definition());
	      requestInput();
	    } 
	    takeToken(Tokens.OVER, "Expect 'oer' after block.");	 
	    this.spaceStack --;
	    return statements;
	}
	private List<Stmt> gowonBlock(){
		List<Stmt> statements = new ArrayList<>();	
	    while (!check(Tokens.WHILE) && !isAtEnd()) {
	      statements.add(definition());
	      requestInput();
	    }
	    takeToken(Tokens.WHILE, "Expect 'WHILE' after block.");
	    
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
				Tokens[] tokens = {Tokens.SCRIPT, Tokens.LETTER, Tokens.NUMBER, Tokens.ANSWER, Tokens.NONE, Tokens.BODGER};
				if(match(tokens)) {
					Tokens type = previous();
					return new Stmt.DefVar(name,new Expr.Primary(type, null));
				}else {
					//type error
					throw new RuntimeException("Vexed: Variable Definition Error, Incorrect Type Defition.");
				}
			//if := look for expression otherwise throw definition error	
			}
			//Check for assignment
			if(match(new Tokens[]{Tokens.COLON_EQ})) {
				Expr value = expression();
				return new Stmt.DefVar(name, value);
				
			}else {
				throw new RuntimeException("Vexed: Incorrect variable definition.");
			}
		}
		else {throw new RuntimeException("Vexed: Error at variable name");}	
	}
	
	private Stmt statement() {
		
		if(peek() == Tokens.ID) {
			
			//System.out.println(peekValue().compareTo(currentFunction)==0);
			if (currentFunction!=null &&peekValue().compareTo(currentFunction)==0) {
				match(new Tokens[]{Tokens.ID});
				if(match(new Tokens[]{Tokens.L_PAREN})) {
					System.out.print("optiontaken");
					return new Stmt.Expression(new Expr.Var((String)advanceValue()));
				}
				return returnStmt();
			}
		}
		if (match(new Tokens[]{Tokens.WRITE})) {
			return printStmt(0);
		}
		if(match(new Tokens[]{Tokens.PROMPT})) {
			return printStmt(1);
		}
		if(match(new Tokens[]{Tokens.READ})) {
			return readStmt();
		}
		if(match(new Tokens[]{Tokens.FORGET})) {
			return forgetStmt();
		}
		if(match(new Tokens[]{Tokens.IF})) {
			this.spaceStack ++;
			return ifStatement();
		}
		if(match(new Tokens[]{Tokens.WHEN})) {
			this.spaceStack ++;
			return whenStatement();
		}
		if(match(new Tokens[]{Tokens.WHILE})) {
			this.spaceStack ++;
			return whileStatement();
		}
		if(match(new Tokens[]{Tokens.GOWON})) {
			this.spaceStack ++;
			return gowanStatement();
		}
		if(match(new Tokens[] {Tokens.EYUP})) {
			return eyupStatement();
		}
		if(match(new Tokens[] {Tokens.SITHE})) {
			return sitheStmt();
		}
		if(match(new Tokens[] {Tokens.GANDER})) {
			return ganderStmt();
		}
		
		return exprStmt();
		
	}
	
	private Stmt readStmt() {
		takeToken(Tokens.L_PAREN,"Ey up! Was expecting '('");
		if(match(new Tokens[] {Tokens.ID})) {
			String name = advanceValue(); 
			takeToken(Tokens.R_PAREN,"Ey up! Was expecting ')'");
			return new Stmt.Read(name);
		}
		throw error("Flummoxed: ain't a name?");
		
	}

	private Stmt ganderStmt() {
		String varName = null;
		if(match(new Tokens[] {Tokens.ID})){
			varName = advanceValue();
		}
		return new Stmt.Gander(varName);
	}

	private Stmt sitheStmt() {
		return new Stmt.SitheCall();
			
	}
	
	private Stmt eyupStatement() {
		if(match(new Tokens[] {Tokens.ID})) {
			String name = advanceValue(); 
			return new Stmt.EyupCall(name);
		}
		throw error("Flummoxed: ain't a name?");
	}
	private Stmt returnStmt() {
		
		advanceValue(); 
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

	private Stmt printStmt(int type) {
		Tokens[] token = {Tokens.L_PAREN};
		if (match(token)) {
			Expr literal = expression();
			takeToken(Tokens.R_PAREN,"Ey up! Was expecting ')'");
			return new Stmt.Print(literal, type);
		}
		else {
			throw error("Print");
		}
	}
	
	private Stmt ifStatement() {
		Expr condition = expression();
		requestInput();
		takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
		Stmt branch1 = statement();
		requestInput();
		Stmt branch2 = null;
		if(match(new Tokens[]{Tokens.ELSE})) {
			branch2 = statement();
			requestInput();
		}
		this.spaceStack --;
		takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		
		return new Stmt.If(condition, branch1, branch2);
	}
	private Stmt whenStatement() {
		List<Expr> conditions = new ArrayList<>();
		List<Stmt> branches= new ArrayList<>();
		conditions.add(expression());
		requestInput();
		takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
		branches.add(statement());
		requestInput();
		while(match(new Tokens[]{Tokens.WHEN})) {
			conditions.add(expression());
			requestInput();
			takeToken(Tokens.THEN,"Ey up! Was expecting 'then'");
			branches.add(statement());
			requestInput();
		}
		Stmt branch2 = null;
		if(match(new Tokens[]{Tokens.ELSE})) {
			branch2 = statement();
			requestInput();
		}
		takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		this.spaceStack --;
		return new Stmt.When(conditions, branches, branch2);
		
	}
	private Stmt whileStatement() {
		Expr condition = expression();
		requestInput();
		List<Stmt> statements = new ArrayList<>();
		takeToken(Tokens.GOWON,"Ey up! Was expecting 'gowon'");
		requestInput();
		statements = block();
		return new Stmt.While(condition, statements, 0);
	}
	
	private Stmt gowanStatement() {
		requestInput();
		List<Stmt> statements = new ArrayList<>();
		statements = gowonBlock();
		Expr condition = expression();
		requestInput();
		takeToken(Tokens.OVER,"Ey up! Was expecting 'oer'");
		this.spaceStack --;
		return new Stmt.While(condition, statements, 1);
	}
	
	private Expr expression() {
		return assignment();
	}
	
	
	private Expr assignment() {
		Tokens[] tokens = {Tokens.COLON_EQ};
		Expr expr = stringAccess();
		if(expr instanceof Expr.StringAccess) {
			if(match(tokens)) {
				Expr value = assignment();
				if(expr instanceof Expr.Var) {
					return new Expr.CharAccess(expr, value);
				}
				error("char assign error");
			}
		}
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
	private Expr stringAccess() {
		Expr name = or();
		if(match(new Tokens[]{Tokens.LS_BRACKET})) {
			Expr index = primary();
			takeToken(Tokens.RS_BRACKET,"Ey up! Was expecting ']'");
			return new Expr.StringAccess(name,index);
		}
		return name;
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
		Tokens[] tokens = {Tokens.STAR, Tokens.FSLASH, Tokens.PERCENT};
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
				expr = endInstanceCall(expr);
				return expr;
			}else{
				return new Expr.Instance(expr,arguments);
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
	 private Expr endInstanceCall(Expr called) {
		    List<Expr> arguments = new ArrayList<>();
	
		    if (!check(Tokens.R_PAREN)) {
		      do {
		    	  if(arguments.size()>=255) {
		    		  error("Can't have more than 255 arguments. ");
		    	  }
		    	Expr expr = expression();
		    	if (expr instanceof Expr.Assignment) {
		    		arguments.add(expr);
		    	}else {
		    		throw error("Not an assignment!");
		    	}
		        
		      } while (match(new Tokens[]{Tokens.COMMA}));
		    }

		    takeToken(Tokens.R_PAREN,"Expect ')' after arguments.");

		    return new Expr.Instance(called, arguments);
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
		if (match(new Tokens[]{Tokens.STRING})) {
			String value = (String)advanceValue();
			if(value.length() == 1) {
				return new Expr.Primary(Tokens.LETTER,value.charAt(0));
			}
			else {
			return new Expr.Primary(Tokens.SCRIPT,value);}
		}
		if (match(new Tokens[]{Tokens.INTEGER})) {
			return new Expr.Primary(Tokens.NUMBER,Double.parseDouble((String)advanceValue()));}
		if (match(new Tokens[]{Tokens.FLOAT})) {
			return new Expr.Primary(Tokens.NUMBER,Double.parseDouble((String)advanceValue()));}
		if (match(new Tokens[]{Tokens.TRUE})) {
			return new Expr.Primary(Tokens.ANSWER, true);}
		if (match(new Tokens[]{Tokens.FALSE})) {
			return new Expr.Primary(Tokens.ANSWER,false);}
		if (match(new Tokens[]{Tokens.NONE})) {
			return new Expr.Primary(Tokens.NONE,null);}
		if (match(new Tokens[]{Tokens.ID})) {
			return new Expr.Var((String)advanceValue());
			}
		if (match(new Tokens[]{Tokens.MISSEN})) {
			return new Expr.Missen();
			}
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
