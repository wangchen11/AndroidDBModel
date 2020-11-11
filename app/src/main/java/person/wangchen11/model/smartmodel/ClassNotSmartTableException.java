package person.wangchen11.model.smartmodel;

public class ClassNotSmartTableException extends Exception {
	private static final long serialVersionUID = 1L;
	public ClassNotSmartTableException() {
	}
	
	public ClassNotSmartTableException(Class<?> clazz) {
		super(""+clazz);
	}

}
