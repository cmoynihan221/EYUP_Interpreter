package core;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import ast.Interpreter;
import ast.Resolver;
//import ast.NodePrintVisitor;
import ast.Stmt;
import enums.Tokens;
import lexer.Lexer;
import parser.Parser;


public class Loop {
	private static Lexer l = new Lexer();
	private static Parser p = new Parser();
	private static Interpreter i = new Interpreter();
	private static Resolver resolver = new Resolver(i);
	//public static console.JavaConsole jc = new console.JavaConsole();
	
	private static Boolean checkTokens(lexer.OutputTuple ot){
		Tokens[] tokens = {Tokens.IF, Tokens.WHEN,Tokens.WHILE, Tokens.FETTLE};
		for(Tokens t : tokens) {
			if(ot.tokens.contains(t)) {
				return true;
			}
		}
		return false; 
	}
	public static void main(String[] args) {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		Integer callStack = 0;
		while(true) {
			String inputString;
			try {	
				lexer.OutputTuple lexed = new lexer.OutputTuple();		
				System.out.print(">");
				inputString = input.readLine();		
				
				//System.out.println(inputString);
				
				lexer.OutputTuple lexedLine = l.lexString(inputString);
				lexed.tokens.addAll( lexedLine.tokens);
				lexed.tokenValues.addAll( lexedLine.tokenValues);			
				if(checkTokens(lexed)&&!lexed.tokens.contains(Tokens.OVER)) {
					callStack ++;
					while(callStack > 0) {
						for(int i = 0;i<callStack;i++) {
							System.out.print("\t");
						}
						//System.out.print(callStack);
						System.out.print(">");
						inputString = input.readLine();		
						lexedLine = l.lexString(inputString);						
						lexed.tokens.addAll( lexedLine.tokens);
						lexed.tokenValues.addAll( lexedLine.tokenValues);
						if(lexedLine.tokens.contains(Tokens.OVER)) callStack --;
						if (checkTokens(lexedLine)) callStack ++;
					}
				}
				lexed.tokens.add(Tokens.EOI);
				
				//System.out.println(lexed.tokens);
				
				if (lexed.tokens.contains(Tokens.ERROR)) {
					Error.tokenError((String)lexed.tokenValues.get(0));
				}
				else {
					ArrayList<Stmt> stmts = p.parseInput(lexed);
					try {
				    resolver.resolve(stmts);
				    i.interpret(stmts);
				    }catch(RuntimeException e) {
				    	
				    }
					
					
				}
			} catch(IOException e) {
				System.out.print("IO Error");	
			}
			
		}
	}
}
