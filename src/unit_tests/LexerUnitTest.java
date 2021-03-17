package unit_tests;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import enums.Tokens;
import lexer.Lexer;
import lexer.OutputTuple;
public class LexerUnitTest {
	Lexer l = new Lexer();
	
	@Test 
	public void testCombinations() {
		OutputTuple target = new OutputTuple();
		target = new OutputTuple();
		target.tokens =  new ArrayList<Tokens>(Arrays.asList(Tokens.WRITE, Tokens.L_PAREN, Tokens.STRING,Tokens.R_PAREN, Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("HELLO WORLD"));
		assertEquals(target.toString(),l.lexString("write(\"HELLO WORLD\")").toString());
	}
	
	

	@Test 
	public void testLexString() {
		OutputTuple target = new OutputTuple();
		target = new OutputTuple();
		target.tokens =  new ArrayList<Tokens>(Arrays.asList(Tokens.STRING,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("HELLO WORLD"));
		assertEquals(target.toString(),l.lexString("\"HELLO WORLD\"").toString());
		
		target = new OutputTuple();
		target.tokens =  new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("\""));
		assertEquals(target.toString(),l.lexString("").toString());
		
		
	}
	@Test 
	public void testLexId() {
		//Tests a series of IDs
		OutputTuple target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID,Tokens.ID,Tokens.ID,Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("hello","HELLO","Wolrd","World"));
		assertEquals(target.toString(),l.lexString("hello HELLO  Wolrd World ").toString());
		
		//
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("hello"));
		assertEquals(target.toString(),l.lexString(" hello").toString());
		
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("hello"));
		assertEquals(target.toString(),l.lexString(" 		hello		").toString());
		
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID,Tokens.ID,Tokens.ID,Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("world0","c5","cw546d","H123lLO"));
		assertEquals(target.toString(),l.lexString("world0 c5 cw546d H123lLO").toString());
		//System.out.println(l.lexString(" ").toString());
		
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID,Tokens.ID,Tokens.ID,Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("A","B","a","b"));
		assertEquals(target.toString(),l.lexString("A B a b").toString());
		//System.out.println(l.lexString(" ").toString());
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.IF,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList());
		assertEquals(target.toString(),l.lexString("if").toString());
	}
	
	@Test
	public void testLexInteger() {
		OutputTuple target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.INTEGER,Tokens.INTEGER,Tokens.INTEGER,Tokens.INTEGER,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("1234","5678","90","1"));
		assertEquals(target.toString(),l.lexString("1234 5678 90 1").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.INTEGER,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("0"));
		assertEquals(target.toString(),l.lexString("0").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.INTEGER,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("0"));
		assertEquals(target.toString(),l.lexString("0	").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.INTEGER,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("101"));
		assertEquals(target.toString(),l.lexString("101").toString());
	}
	
	@Test
	public void testLexFloat() {
		OutputTuple target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.FLOAT,Tokens.FLOAT,Tokens.FLOAT,Tokens.FLOAT,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("0.1234","456.345","34.3","0.011000"));
		assertEquals(target.toString(),l.lexString("0.1234 456.345 34.3 0.011000").toString());
	}
	
	@Test
	public void testLexSymbol() {
		OutputTuple target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.LESS_EQ,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList());
		assertEquals(target.toString(),l.lexString("<=").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.LESS,Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("test"));
		assertEquals(target.toString(),l.lexString("<test").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.LESS,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList());
		assertEquals(target.toString(),l.lexString("<").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ID, Tokens.GREAT,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("THE"));
		assertEquals(target.toString(),l.lexString("THE>").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.LESS_EQ, Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("THE"));
		assertEquals(target.toString(),l.lexString("<=THE").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.PERIOD,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>();
		assertEquals(target.toString(),l.lexString(".").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.PERIOD,Tokens.ID,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("TEST"));
		assertEquals(target.toString(),l.lexString(".TEST").toString());
	}
	
	
	
	@Test
	public void errors() {
		OutputTuple target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("."));
		assertEquals(target.toString(),l.lexString("0.ab").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("."));
		assertEquals(target.toString(),l.lexString("0.@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("."));
		assertEquals(target.toString(),l.lexString("0. ").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("a"));
		assertEquals(target.toString(),l.lexString("0.4a ").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("0.4@ ").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("10@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("0"));
		assertEquals(target.toString(),l.lexString("00").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("0@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("A"));
		assertEquals(target.toString(),l.lexString("1234A").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("<@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("\""));
		assertEquals(target.toString(),l.lexString("\"").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("O"));
		assertEquals(target.toString(),l.lexString("\"HELLO").toString());
		
		target = new OutputTuple();
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("HELLO@").toString());
		
		target.tokens = new ArrayList<Tokens>(Arrays.asList(Tokens.ERROR,Tokens.EOI));
		target.tokenValues = new ArrayList<Object>(Arrays.asList("@"));
		assertEquals(target.toString(),l.lexString("HELLO5@").toString());
	}


}
