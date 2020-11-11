package person.wangchen11.model.smartmodel;

import java.lang.reflect.Field;

import person.wangchen11.model.smartmodel.annotation.SmartField;
import person.wangchen11.model.smartmodelmanager.TextUtil;

public class SmartFieldInfo {
	private Field mField;
	private String mFieldName;
	private SmartField mSmartField;
	public SmartFieldInfo(Field field) {
		mField = field;
		mSmartField = mField.getAnnotation(SmartField.class);
		mFieldName = getSmartFieldNameReal();
	} 

	public boolean isValid() {
		return mFieldName!=null;
	}
	
	public Field getField() {
		return mField;
	}
	
	public String getSmartFieldName() {
		return mFieldName;
	}
	
	private String getSmartFieldNameReal() {
		SmartField smartField = mSmartField;
		if(smartField==null) {
			return null;
		}
		StringBuilder nameBuilder = new StringBuilder();
		for(char ch:mField.getName().toCharArray()) {
			if(Character.isUpperCase(ch)) {
				nameBuilder.append('_');
				nameBuilder.append(Character.toLowerCase(ch));
			} else {
				nameBuilder.append(ch);
			}
		}
		return nameBuilder.toString();
	}
	
	public SmartField getSmartField() {
		return mSmartField;
	}
	
	public String genCreateFieldString() {
		StringBuilder stringBuilder = new StringBuilder();
		SmartField smartField = getSmartField();
		String type = getFieldSqlType(smartField);
		stringBuilder.append(getSmartFieldName());
		if(smartField.autoIncrement()){
			type = "INTEGER";
		}
		stringBuilder.append(" "+type);
		if(smartField.notNull()) {
			stringBuilder.append(" NOT NULL");
		}
		if(smartField.primaryKey()) {
			stringBuilder.append(" PRIMARY KEY");
		}
		if(smartField.autoIncrement()) {
			//stringBuilder.append(" AUTO_INCREMENT");
			stringBuilder.append(" AUTOINCREMENT");
		}
		if(!TextUtil.isEmpty(smartField.defaultValue())) {
			stringBuilder.append(" DEFAULT '");
			stringBuilder.append(smartField.defaultValue());
			stringBuilder.append("'");
		}
		return stringBuilder.toString();
	}

	private static String getFieldSqlType(SmartField smartField) {
		String type = "varchar(255)";

		if( !TextUtil.isEmpty(smartField.type()) ) {
			type = smartField.type();
		}
		
		return type;
	}
}
