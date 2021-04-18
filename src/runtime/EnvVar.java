package runtime;

import enums.Tokens;

public class EnvVar {
	public Object value;
	final Tokens type;
	public EnvVar(Object value, Tokens type) {
		this.value = value;
		this.type = type;
	}
}
