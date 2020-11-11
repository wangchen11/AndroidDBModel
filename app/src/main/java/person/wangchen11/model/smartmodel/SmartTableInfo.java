package person.wangchen11.model.smartmodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import person.wangchen11.model.smartmodel.annotation.SmartTable;
import person.wangchen11.model.smartmodelmanager.TextUtil;

public class SmartTableInfo {
	private Class<?> mTableModelClass;
	private List<SmartFieldInfo> mFieldes = null;
	private Map<String,SmartFieldInfo> mFieldesMap = null;
	private String[] mFieldNames = null;
	private String mSmartTableName = null;

	public SmartTableInfo(Class<?> tableModelClass) {
		mTableModelClass = tableModelClass;
		mSmartTableName = getSmartTableNameReal();
		mFieldes = getFieldesReal();
		mFieldesMap = new HashMap<String, SmartFieldInfo>();
		mFieldNames = new String[mFieldes.size()];
		for(int i=0;i<mFieldes.size();i++) {
			SmartFieldInfo fieldInfo = mFieldes.get(i);
			mFieldesMap.put(fieldInfo.getSmartFieldName(), fieldInfo);
			mFieldNames[i] = fieldInfo.getSmartFieldName();
		}
	}
	
	public boolean isValid() {
		return mSmartTableName!=null&&mFieldNames.length!=0;
	}
	
	public String getSmartTableName() {
		return mSmartTableName;
	}
	
	private String getSmartTableNameReal() {
		SmartTable smartTable = (SmartTable) mTableModelClass.getAnnotation(SmartTable.class);
		if(smartTable==null) {
			return null;
		}
		String name = smartTable.name();
		name = TextUtil.isEmpty(name) ? mTableModelClass.getSimpleName().toLowerCase():name;
		return name;
	}
	
	public List<SmartFieldInfo> getFieldes(){
		return mFieldes;
	}
	
	public SmartFieldInfo getField(String name){
		return mFieldesMap.get(name);
	}
	
	public String[] getFieldNames() {
		return mFieldNames;
	}
	
	private List<SmartFieldInfo> getFieldesReal(){
		List<SmartFieldInfo> fieldInfos = new ArrayList<SmartFieldInfo>();
		Field[] fields = mTableModelClass.getFields();
		for(Field field:fields) {
			SmartFieldInfo smartFieldInfo = new SmartFieldInfo(field);
			if(smartFieldInfo.isValid()) {
				fieldInfos.add(smartFieldInfo);
			}
		}
		return fieldInfos;
	}
	
	public void updateTableInfo(SQLiteDatabase database) {
		try {
			String sql = "select * from "+getSmartTableName()+" limit 1;";
			Map<String,Integer> metas = new HashMap<String, Integer>();
			Cursor cursor = database.query(getSmartTableName(), null, null, null, null, null, null, "1");
			if (cursor != null) {
				for(int i=0;i<cursor.getColumnCount();i++) {
					String name = cursor.getColumnName(i);
					metas.put(name, i);
					if(!mFieldesMap.containsKey(name)) {
						System.out.println("field "+name+" does not exits.");
					}
				}
				cursor.close();

				for(SmartFieldInfo fieldInfo:mFieldes) {
					if(!metas.containsKey(fieldInfo.getSmartFieldName())) {
						String addColumnSql = "alter table "+getSmartTableName()+" add column "+fieldInfo.genCreateFieldString()+";";
						System.out.println("addColumnSql");
						database.execSQL(addColumnSql);
					}
				}
			}
		} catch (SQLiteException e){
			// e.printStackTrace();
			createTable(database);
		}
	}
	
	public void createTable(SQLiteDatabase database) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE "+getSmartTableName()+"(");
		for(int i=0;i<mFieldes.size();i++) {
			SmartFieldInfo smartFieldInfo = mFieldes.get(i);
			sqlBuilder.append(smartFieldInfo.genCreateFieldString());
			if(i<mFieldes.size()-1) {
				sqlBuilder.append(',');
			}
		}
		sqlBuilder.append(");");
		String sql = sqlBuilder.toString();
		System.out.println(sql);
		database.execSQL(sql);
	}
	
	public void drop(SQLiteDatabase database)  {
		String sql = "drop table "+getSmartTableName();
		System.out.println(sql);
		database.execSQL(sql);
	}
}
