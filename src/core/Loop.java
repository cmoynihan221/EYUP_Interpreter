package core;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import ast.Interpreter;
import ast.Resolver;
//import ast.NodePrintVisitor;
import ast.Stmt;
//import ast.TypeChecker;
import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
import parser.Parser;


public class Loop {
	private static Lexer l = new Lexer();
	private static Parser p = new Parser();
	private static Interpreter i = new Interpreter();
	//private static TypeChecker t = new TypeChecker();
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
	static boolean start = false;
	public static void main(String[] args) {
		while(true) {
			if(start == true) {
				System.out.print(i.currentBodger.name+">");
				while(start == true) {
					try {
						OutputTuple lexed = getInput("");
						ArrayList<Stmt> stmts = p.parseInput(lexed);
						//t.check(stmts);
					    resolver.resolve(stmts);
					    i.interpret(stmts);
					    System.out.print(i.currentBodger.name+">");
						}
					catch(Leave l) {
						System.out.println("Leavin' Yorkshire v1.0 (flippin 'eck!)");
						start = false;
					}
					catch(RuntimeException e) {	
						System.out.print(i.currentBodger.name+">");
					  }
					
				}
			}else {
				
				System.out.print(">");
				try {
					String inputString = input.readLine();
					if(inputString.equals("eyup")) {
						start = true;
						System.out.println("Enterin' Yorkshire v1.0 (areyt tyke!)");
					}
				} catch (IOException e) {
					
				}
			}
		}
	}
}
