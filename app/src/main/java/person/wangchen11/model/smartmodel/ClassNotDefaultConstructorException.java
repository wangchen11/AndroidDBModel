package person.wangchen11.model.smartmodel;

import java.sql.SQLException;

public class ClassNotDefaultConstructorException extends SQLException {
	private static final long serialVersionUID = 1L;
	public ClassNotDefaultConstructorException() {
	}
	
	public ClassNotDefaultConstructorException(Class<?> clazz) {
		super(""+clazz);
	}

}
