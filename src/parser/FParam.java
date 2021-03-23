package parser;

import java.util.List;

import enums.Tokens;


public class FParam {
	public Tokens type;
	public List<String> params;
	public FParam(List<String> params, Tokens type) {
		this.type = type;
		this.params = params;
	}
	public Integer size() {
		return params.size();
	}
}
