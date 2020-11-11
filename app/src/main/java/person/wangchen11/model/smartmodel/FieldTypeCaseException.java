package person.wangchen11.model.smartmodel;

import java.sql.SQLException;

public class FieldTypeCaseException extends SQLException {
	private static final long serialVersionUID = 1L;
	public FieldTypeCaseException() {
	}

	public FieldTypeCaseException(String field) {
		super(""+field);
	}

	public FieldTypeCaseException(Class<?> clazz,String field) {
		super(""+clazz+":"+field);
	}

}
