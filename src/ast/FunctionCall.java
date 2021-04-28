package ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.Tokens;
import parser.FParam;

public class FunctionCall {

	public final List<FParam> parameters;
	public final Tokens returnType;
	public final Map<String, Object> scope;
	
	public FunctionCall(List<FParam> p, Tokens rtype ) {
		this.parameters=p;
		this.returnType=rtype;
		this.scope = new HashMap<>();
	}
	public void updateMap(Map<String, Object> scope) {
		
	}

}
