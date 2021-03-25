package runtime;
import java.util.HashMap;
import java.util.Map;
public class EyupInstance {
	EyupBodger bodger;
	public EyupInstance(EyupBodger bodger) {
		this.bodger = bodger;
	}
	@Override
	public String toString() {
		return bodger.name+  "instance";
	}

}
