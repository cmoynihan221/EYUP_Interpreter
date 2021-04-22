package ast;

import java.util.List;

import enums.Tokens;
import parser.FParam;

public class FunctionCall {

	public final List<FParam> parameters;
	public final Tokens returnType;
	
	public FunctionCall(List<FParam> p, Tokens rtype) {
		this.parameters=p;
		this.returnType=rtype;
	}

}
