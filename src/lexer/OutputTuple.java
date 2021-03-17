package lexer;

import java.util.ArrayList;

import enums.Tokens;

public class OutputTuple {
	public ArrayList<Tokens> tokens = new ArrayList<Tokens>();
	public ArrayList<Object> tokenValues = new ArrayList<Object>();
	public String toString() {
		String output = "Tokens: ";
		for (int i = 0; i < tokens.size(); i++) {
			 if (i == 0 ) {
				 output+=tokens.get(i);
			 }
			 else {
			 	 output+=", " + tokens.get(i);
			 }
	       }
		 output+="\nValues: ";
		 for (int i = 0; i < tokenValues.size(); i++) {
			 if (i == 0 ) {
				 output+=tokenValues.get(i);
			 }
			 else {
			 	 output+=", " + tokenValues.get(i);
			 }
	        }
		 return output;			 
	}
}
