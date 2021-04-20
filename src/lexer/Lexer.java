package lexer;
import java.util.*;
import java.util.Scanner;
import enums.Tokens;

public class Lexer {
	ArrayList<String> lower_alpha = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"));
	ArrayList<String> upper_alpha = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"));
	ArrayList<String> symbols = new ArrayList<String>(Arrays.asList("(" ,")" ,"[" ,"]" ,"\"" ,"+" ,"-" ,"=" ,":" ,"*" ,"/" ,"%" ,"$" ,"<" ,">","!","{","}",","));
	ArrayList<String> non_zero = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9"));
	ArrayList<String> whitespace = new ArrayList<String>(Arrays.asList(" ", "\t", "\n"));
	
	HashMap<String, String> symbol_dict = new HashMap<String,String>(); 
	HashMap<String, Tokens> keywords = new HashMap<String,Tokens>();
	HashMap<String, Tokens> special = new HashMap<String,Tokens>();
	
	int current_pointer = 0;
	String cc_type;
	String current_character;
	String input;
	String current_token;
	Tokens ct_type;
	OutputTuple output = new OutputTuple();
	Boolean error = false;
	
	
	public Lexer() {
		for(String item: lower_alpha) {symbol_dict.put(item, "lchar");}
		for(String item: upper_alpha) {symbol_dict.put(item, "uchar");}
		for(String item: symbols) {symbol_dict.put(item, "symbol");}
		for(String item: non_zero) {symbol_dict.put(item, "non-zero");}
		for(String item: whitespace) {symbol_dict.put(item, "whitespace");}
		symbol_dict.put("0", "zero");
		symbol_dict.put(".", "period");
		keywords.put("aye", Tokens.TRUE);
		keywords.put("nay", Tokens.FALSE);
		keywords.put("giz", Tokens.GIZ);
		keywords.put("fettle", Tokens.FETTLE);
		keywords.put("bodger", Tokens.BODGER);
		keywords.put("Bodger", Tokens.BODGER);
		keywords.put("gioer", Tokens.GIVEOVER);
		keywords.put("oer", Tokens.OVER);
		keywords.put("nowt", Tokens.NONE);
		keywords.put("prompt", Tokens.PROMPT);
		keywords.put("read", Tokens.READ);
		keywords.put("write", Tokens.WRITE);
		keywords.put("and", Tokens.AND);
		keywords.put("or", Tokens.OR);
		keywords.put("not", Tokens.NOT);
		keywords.put("if", Tokens.IF);
		keywords.put("then", Tokens.THEN);
		keywords.put("else", Tokens.ELSE);
		keywords.put("when", Tokens.WHEN);
		keywords.put("gowon", Tokens.GOWON);
		keywords.put("wang", Tokens.WANG);
		keywords.put("while", Tokens.WHILE);
		keywords.put("allus", Tokens.ALLUS);
		keywords.put("gander", Tokens.GANDER);
		keywords.put("forget", Tokens.FORGET);
		keywords.put("eyup", Tokens.EYUP);
		keywords.put("summat", Tokens.SUMMAT);
		keywords.put("Number", Tokens.NUMBER);
		keywords.put("Letter", Tokens.LETTER);
		keywords.put("Script", Tokens.SCRIPT);
		keywords.put("Answer", Tokens.ANSWER);
		keywords.put("sithee", Tokens.SITHE);
		keywords.put("sithi", Tokens.SITHE);
		keywords.put("missen", Tokens.MISSEN);
		
		special.put(".", Tokens.PERIOD);
		special.put("(", Tokens.L_PAREN);
		special.put(")", Tokens.R_PAREN);
		special.put("\"", Tokens.SM);
		special.put(",", Tokens.COMMA);
		special.put(":", Tokens.COLON);
		special.put("$", Tokens.DOLLA);
		special.put("/", Tokens.FSLASH);
		special.put("=", Tokens.EQUAL);
		special.put("+", Tokens.PLUS);
		special.put("-", Tokens.MINUS);
		special.put("*", Tokens.STAR);
		special.put("%", Tokens.PERCENT);
		special.put("{", Tokens.LC_BRACKET);
		special.put("}", Tokens.RC_BRACKET);
		special.put("[", Tokens.LS_BRACKET);
		special.put("]", Tokens.RS_BRACKET);
		special.put("<", Tokens.LESS);
		special.put(">", Tokens.GREAT);
		special.put("!", Tokens.EXCLAMATION);
		special.put(":=", Tokens.COLON_EQ);
		special.put("<=", Tokens.LESS_EQ);
		special.put(">=", Tokens.GREAT_EQ);
		special.put("!=", Tokens.EXCLA_EQ);
		
		
	}
	
	private String getType(String symbol) {
		String type = symbol_dict.get(symbol);
		if (type == null) {
			type = "null";
		}
		return type;
	}
	
	public OutputTuple lexString(String input) {
		if(!input.equals("")) {
			this.input = input;
			current_pointer = 0;
			output = new OutputTuple();
			newToken();
			return output;
			}
		else {
			throw new RuntimeException();
		}
	}
	
	
	private void newToken() {
		current_token = new String();
		try {
		String character = String.valueOf(input.charAt(current_pointer));
		this.current_character = character;
		this.cc_type = getType(character);
		switch(this.cc_type) {
		case "lchar": case "uchar": string();break;
		case "whitespace": whitespace();break;
		case "symbol":case"period": symbol();break;
		case "zero":zero();break;
		case "non-zero": integer();break;
		default: error(false); 
		}}
		catch(Exception e) {
			error(false);
		}
		
		
	}
	
	private void endToken(Boolean need_value) {
		advance();
		output.tokens.add(ct_type);
		if (need_value) {
			output.tokenValues.add(current_token);
		}
		newToken();
	}
	private void endOfInput(Boolean need_value) {
		output.tokens.add(ct_type);
		if (need_value) {
			output.tokenValues.add(current_token);
		}
	}
	private void floating() {
		current_token += this.current_character;
		ct_type = Tokens.FLOAT;
		StringTuple next = peek();
		if(cc_type == "period" && (next.y != "zero" && next.y != "non-zero")) {
			error(false);
		}
		else {
			switch(next.y) {
			case"zero":case"non-zero":advance();floating();break;
			case"uchar":case"lchar":case"period": error(true);break;
			case"whitespace":case"symbol": endToken(true);break;
			case"end":endOfInput(true);break;
			default: error(true); 
			}
		}
	}
	private void zero() {
		current_token += this.current_character;
		ct_type = Tokens.INTEGER;
		StringTuple next = peek();
		switch(next.y) {
		case"zero":case"non-zero":case"uchar":case"lchar": error(true);break;
		case"whitespace":case"symbol": endToken(true);break;
		case"period": advance();floating(); break;
		case"end":endOfInput(true);break;
		default: error(true); 
		}
	}
	private void integer() {
		current_token += this.current_character;
		ct_type = Tokens.INTEGER;
		StringTuple next = peek();
		switch(next.y) {
		case"zero":case"non-zero":advance();integer();break;
		case"uchar":case"lchar": error(true);break;
		case"whitespace":case"symbol": endToken(true);break;
		case"period": advance();floating(); break;
		case"end":endOfInput(true);break;
		default: error(true); 
			
		}
	}
	private void symbol() {
		current_token += this.current_character;	
		//Switch on symbol
		switch(current_character) {
		//If one of these check for '='
		//IF next = null then output current symbol and end
		case"<":case">":case"!":case":": 
			String nextX = peek().x;
			if (nextX != null) {
			switch(nextX ) {
				case"=":advance();
					current_token += this.current_character;
					ct_type = special.get(current_token);
					nextX = peek().x;
					//check value after = is not null
					if (nextX != null) {
						endToken(false);break;
					}
					else {endOfInput(false);break;}
				default:
					ct_type = special.get(current_token);
					endToken(false);break;
			}}
			else{
				ct_type = special.get(current_token);
				endOfInput(false);break;
			}break;
		case "\"": speech();break;
		//If any other symbol
		default:
		nextX = peek().x;
		ct_type = special.get(current_token);
		if (nextX != null) {
			endToken(false);
		}
		else{
			endOfInput(false);
		}
		}
	}
	
	private void speech() {
		if (peek().y == "end") {
			error(false);
		}
		else {
			advance();
			//Wipe current token
			current_token = "";
			while(special.get(current_character)!=Tokens.SM) {	
				//Check if at end while speech mark not closed
				if (peek().y == "end") {
					error(false);
					break;
				}
				else {
					current_token+= current_character;
					advance();
				}
			}
			if(special.get(current_character)==Tokens.SM) {
				ct_type = Tokens.STRING;
				//Check if at end 
				if (peek().y == "end") {
					endOfInput(true);
				}
				else {
					endToken(true);
				}
			}		
		}	
	}
	
	private void string() {
		ct_type = Tokens.ID;
		current_token += this.current_character;
		StringTuple next = peek();
		switch(next.y.toString()){
		case "whitespace": case "symbol": case "period": endToken(endString());break;
		case "uchar": case "lchar": advance(); string(); break;
		case "zero": case "non-zero": advance(); id();break;
		case "end": endOfInput(endString()); break;
		default: error(true); 
		}
	}
	private boolean endString() {
		if (keywords.get(current_token) == null){
			return true;
		}
		else {
			ct_type = keywords.get(current_token);
			return false;
		}
	}
	private void id() {
		ct_type = Tokens.ID;
		current_token += this.current_character;
		StringTuple next = peek();
		switch(next.y){
		case "whitespace": case "symbol": case "period": endToken(true);break;
		case "uchar": case "lchar": advance(); id(); break;
		case "zero": case "non-zero": advance(); id();break;
		case "end": endOfInput(true);break;
		default: error(true); 
		}
	}
	private void whitespace() {
		StringTuple next = peek();
		if(next.y.toString() != "end") {
			advance();
			newToken();
		}
	}
	
	private void advance() {
		current_pointer ++;
		String character = String.valueOf(input.charAt(current_pointer));
		current_character = character;
		cc_type = getType(character);
	}
	
	private StringTuple peek() {
		if(current_pointer+1 == (input.length())) {
			return new StringTuple(null,"end");
		}
		else {
			String character = String.valueOf(input.charAt(current_pointer+1));
			return(new StringTuple(character,getType(character)));
			
		}
	}
	
	private void error(Boolean next) {
		output = new OutputTuple();
		output.tokens.add(Tokens.ERROR);
		if (next == true) {
			output.tokenValues.add(peek().x);
		}
		else{
			output.tokenValues.add(current_character);
		}
	}
		
		
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Lexer l = new Lexer();
		
		
		OutputTuple s = l.lexString("fettle w(n1,n:Number):Number giz");
		if (s != null) {
			System.out.println(s.toString());	
			}
		
		//myObj.close();
		
		
	}
	

}
