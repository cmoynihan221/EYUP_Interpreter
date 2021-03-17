package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ast.Interpreter;
import ast.NodePrintVisitor;
import ast.Stmt;
import enums.Tokens;
import lexer.Lexer;
import parser.Parser;


public class Loop {
	private static Lexer l = new Lexer();
	private static Parser p = new Parser();
	private static Interpreter i = new Interpreter();
	
	public static void main(String[] args) {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			String inputString;
			try {				
				inputString = input.readLine();
				lexer.OutputTuple lexed = l.lexString(inputString);
				if (lexed.tokens.contains(Tokens.ERROR)) {
					Error.tokenError((String)lexed.tokenValues.get(0));
				}
				else {
					ArrayList<Stmt> stmts = p.parseInput(lexed);
					i.interpret(stmts);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch(RuntimeException r) {
				
			}
			//System.out.print("\n");	
		}
	}
}
