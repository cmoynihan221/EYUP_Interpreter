package unit_tests;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ast.Interpreter;
import ast.Resolver;
import ast.Stmt;
import ast.TypeChecker;
import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
import parser.Parser;

public class InterpreterTests {
	Parser p = new Parser();
	Lexer l = new Lexer();
	Interpreter i = new Interpreter();
	TypeChecker t = new TypeChecker();
	lexer.OutputTuple lexed;
	Resolver resolver = new Resolver(i);
	ArrayList<Stmt> stmts;
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));  
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut); 
	}
	
	
	private  ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	private void interpret(String testString) {
		lexed = l.lexString(testString);
		lexed.tokens.add(Tokens.EOI);
		stmts = p.parseInput(lexed);
		resolver.resolve(stmts);
		i.interpret(stmts);
	}
	
	public void evaluate(String output) {
		assertEquals(output, outContent.toString());
		outContent = new ByteArrayOutputStream();
		setUpStreams();
	}
	
	public void test(String testString, String output) {
		interpret(testString);
		evaluate(output);
	}
	
	@Test 
	public void testEquals() {
		test("3=3", "aye\n");
		test("\"a\"=\"a\"", "aye\n");
		test("\"hello\"=\"hello\"", "aye\n");
		test("nay=nay", "aye\n");
		test("3=4", "nay\n");
		test(" \"Hello\" = 4", "nay\n");
		test(" nowt = aye", "nay\n");
		test(" nowt = nowt", "aye\n");
	}
	@Test 
	public void testNotEquals() {
		test("3!=3", "nay\n");
		test("\"a\"!=\"a\"", "nay\n");
		test("\"hello\"!=\"hello\"", "nay\n");
		test("nay!=nay", "nay\n");
		test("3!=4", "aye\n");
		test(" \"Hello\" != 4", "aye\n");
	}
	@Test 
	public void testPlus() {
		test("3+3", "6\n");
		test("3.5+3", "6.5\n");
		test("2.5+3.5", "6\n");
		test("\"a\"+\"a\"", "Flummoxed: bad'un Number : PLUS\n");
	}
	@Test 
	public void testMinus() {
		test("3-3", "0\n");
		test("3.5-3", "0.5\n");
		test("2.5-3.5", "-1\n");
		test("\"a\"-\"a\"", "Flummoxed: bad'un Number : MINUS\n");
	}
	@Test 
	public void testTimes() {
		test("3*3", "9\n");
		test("3.5*3", "10.5\n");
		test("2.5*3.5", "8.75\n");
		test("\"a\"*\"a\"", "Flummoxed: bad'un Number : STAR\n");
	}
	@Test 
	public void testDivide() {
		test("3/3", "1\n");
		test("3.5/3", "1.17\n");
		test("2.5/3.5", "0.71\n");
		test("\"a\"/\"a\"", "Flummoxed: bad'un Number : FSLASH\n");
	}
	@Test
	public void testMod() {
		test("3%3", "0\n");
		test("3.5%3", "0.5\n");
		test("2.5%3.5", "2.5\n");
		test("\"a\"%\"a\"", "Flummoxed: bad'un Number : PERCENT\n");
	}
	
	@Test
	public void testLess() {
		test("1 < 3", "aye\n");
		test("aye < nay", "nay\n");
		test("\"hello\" < \"hello\"", "nay\n");
		test("\"a\" < \"b\"", "aye\n");
		test("\"a\" < 5", "Vexed: messin' wi types\n");
	}
	@Test
	public void testGreat() {
		test("1 > 3", "nay\n");
		test("aye > nay", "aye\n");
		test("\"hello\" > \"hello\"", "nay\n");
		test("\"a\" > \"b\"", "nay\n");
		test("\"a\" > 5", "Vexed: messin' wi types\n");
	}
	@Test
	public void testLessEq() {
		test("1 <= 3", "aye\n");
		test("aye <= nay", "nay\n");
		test("\"hello\" <= \"hello\"", "aye\n");
		test("\"a\" <= \"b\"", "aye\n");
		test("\"a\" <= 5", "Vexed: messin' wi types\n");
	}
	@Test
	public void testGreatEq() {
		test("1 >= 3", "nay\n");
		test("(1+2) >= 3", "aye\n");
		test("aye >= nay", "aye\n");
		test("\"hello\" >= \"hello\"", "aye\n");
		test("\"a\" >= \"b\"", "nay\n");
		test("\"a\" >= 5", "Vexed: messin' wi types\n");
	}
	@Test
	public void testStringConcat() {
		
		test("\"hello\" $ \"hello\"", "\"hellohello\"\n");
		test("\"a\" $ \"b\"", "Flummoxed: bad'un Script : DOLLA\n");
		test("\"hello\" $ 5", "\"hello5\"\n");
		test("\"hello\" $ \"a\"", "\"helloa\"\n");
		test("\"hello\" $ aye", "\"helloaye\"\n");
	}
	
	
	@Test
	public void testAnd() {
		test("aye and aye", "aye\n");
		test("aye and nay", "nay\n");
		test("nay and nay", "nay\n");
		test("\"a\" and \"a\"", "Flummoxed: bad'un Answer\n");
	}
	
	@Test
	public void testOR() {
		test("aye or aye", "aye\n");
		test("!aye or nay", "nay\n");
		test("nay or nay", "nay\n");
		test("nay or nay", "nay\n");
		test("\"a\" or \"a\"", "Flummoxed: bad'un Answer\n");
	}
	@Test
	public void testUnaryMinus() {
		test("-5", "-5\n");
		
	}
	@Test
	public void testGroup() {
		test("(5+5)", "10\n");
		
	}
	@Test
	public void testNot() {
		test("!aye or nay", "nay\n");
		test("!aye ", "nay\n");
		
	}
	@Test
	public void testExpression() {
		interpret("summat x := \"a\"");
		interpret("x");
		evaluate("Gaffer.x\n"
				+ "a\n");
		
		interpret("summat x : Number");
		interpret("x");
		evaluate("Gaffer.x\n"
				+ "nowt\n");
	}
	@Test
	public void testPrint() {
		interpret("write(5)");
		evaluate("5\n5\n");
		test("write(\"Hello\")", "\"Hello\"\n"
				+ "Hello\n");
	}
	@Test
	public void testDefVar() {
		interpret("summat x := 5<6");
		interpret("x");
		evaluate("Gaffer.x\n"
				+ "aye\n");
		
		interpret("summat x : Number");
		interpret("x");
		evaluate("Gaffer.x\n"
				+ "nowt\n");
		test("summat x : Fettle","Vexed: Variable Definition Error, Incorrect Type Defition.\n");
		test("summat 5 : Fettle","Vexed: Error at variable name\n");
		test("summat x > Fettle","Vexed: Incorrect variable definition.\n");
		
	}
	@Test
	public void testVarExpr() {
		interpret("summat x := 5<6");
		interpret("(x=x)");
		evaluate("Gaffer.x\n"
				+ "aye\n");
		interpret("summat y := 5");
		interpret("(y+y)");
		evaluate("Gaffer.y\n"
				+ "10\n");
	}
	@Test
	public void testVarAssignment() {
		interpret("summat x : Answer");
		interpret("x := 5<7");
		evaluate("Gaffer.x\n"
				+ "aye\n");
		interpret("summat y : Number");
		interpret("y := 5");
		evaluate("Gaffer.y\n"
				+ "5\n");
	
	}
	@Test
	public void testForget() {
		interpret("summat x : Answer");
		interpret("x := 5<7");
		evaluate("Gaffer.x\n"
				+ "aye\n");
		interpret("forget x");
		interpret("x := 5");
		evaluate("x\n"
				+ "Flummoxed: weerz x?\n");
	}
	@Test
	public void testIf() {
		interpret("if 5 < 6 then 5 else 6 oer");
		evaluate("5\n");
		
		interpret("if 6 < 4 then 5 else 6 oer");
		evaluate("6\n");
		
		interpret("if 5 < 6 then 5  oer");
		evaluate("5\n");
	}
	@Test
	public void testWhile() {
		interpret("summat x := 5");
		interpret("while x < 0 gowon write(x) x := x-1 oer ");
		evaluate("Gaffer.x\n"
				+ "5\n"
				+ "4\n"
				+ "3\n"
				+ "2\n"
				+ "1\n"
				+ "0\n"
				+ "nowt\n");
		
		
	}
	
	
	

	


}

