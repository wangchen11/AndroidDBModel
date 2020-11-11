package person.wangchen11.model.smartmodel;

public class NoSmartFieldException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoSmartFieldException() {
	}

	public NoSmartFieldException(String field) {
		super(""+field);
	}

	public NoSmartFieldException(Class<?> clazz,String field) {
		super(""+clazz+":"+field);
	}

}
