package core;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

import ast.Interpreter;
import ast.Resolver;
//import ast.NodePrintVisitor;
import ast.Stmt;
import ast.TypeChecker;
import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
import parser.Parser;


public class Loop {
	private static Lexer l = new Lexer();
	private static Parser p = new Parser();
	private static Interpreter i = new Interpreter();
	private static TypeChecker t = new TypeChecker();
	private static Resolver resolver = new Resolver(i);
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	public static  OutputTuple getInput(String pre) {
		try {
			System.out.print(pre);
			String inputString = input.readLine();
			try {				
				lexer.OutputTuple lexed = l.lexString(inputString);	
				lexed.tokens.add(Tokens.EOI);
				if (lexed.tokens.contains(Tokens.ERROR)) {
					Error.tokenError((String)lexed.tokenValues.get(0));
				}
				return lexed;
			}catch(RuntimeException e) {
				System.out.println("Flummoxed: Nowt input pal");
				throw e;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
	public static void main(String[] args) {
		System.out.print(i.currentBodger.name+">");
		while(true) {
			try {
				OutputTuple lexed = getInput("");
				ArrayList<Stmt> stmts = p.parseInput(lexed);
				t.check(stmts);
			    resolver.resolve(stmts);
			    i.interpret(stmts);
			    System.out.print(i.currentBodger.name+">");
				}
			catch(RuntimeException e) {	
				System.out.print(i.currentBodger.name+">");
			  }
			
		}
	}
}
