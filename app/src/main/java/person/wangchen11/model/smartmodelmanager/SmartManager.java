package person.wangchen11.model.smartmodelmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import person.wangchen11.model.smartmodel.CanNotGetFieldException;
import person.wangchen11.model.smartmodel.ClassNotSmartTableException;
import person.wangchen11.model.smartmodel.FieldTypeCaseException;
import person.wangchen11.model.smartmodel.NoSmartFieldException;
import person.wangchen11.model.smartmodel.SmartFieldInfo;
import person.wangchen11.model.smartmodel.SmartTableInfo;

public class SmartManager {
	private static Gson mGson = new Gson();
	private SQLiteDatabase mDatabase = null;
	private Map<Class<?>,SmartTableInfo> mTableInfoMap = new HashMap<Class<?>, SmartTableInfo>();

	public SmartManager(SQLiteDatabase database) {
		mDatabase = database;
	}

	public void destory(){
		if(mDatabase !=null){
			mDatabase.close();
		}
	}

	public static Gson getGson() {
		return mGson;
	}

	public synchronized SmartTableInfo loadTable(Class<?> clazz) throws Exception {
		if(clazz==null) {
			return null;
		}
		SmartTableInfo smartTableInfo = mTableInfoMap.get(clazz);
		if(smartTableInfo!=null) {
			return smartTableInfo;
		}
		smartTableInfo = new SmartTableInfo(clazz);
		if(!smartTableInfo.isValid()) {
			throw new ClassNotSmartTableException(clazz);
		}

		updateTableInfo(smartTableInfo);
		mTableInfoMap.put(clazz, smartTableInfo);
		return smartTableInfo;
	}

	public SQLiteDatabase getDatabase(){
		return mDatabase;
	}

	private void updateTableInfo(SmartTableInfo smartTableInfo) {
		smartTableInfo.updateTableInfo(mDatabase);
	}

	public void drop(Class<?> smartTableClass) throws Exception  {
		SmartTableInfo smartTableInfo = loadTable(smartTableClass);
		smartTableInfo.drop(getDatabase());
		mTableInfoMap.remove(smartTableClass);
	}

	public boolean isExist(Class<?> smartTableClass,String whereSelection,String[] whereParams) throws Exception  {
		return queryObject(smartTableClass,whereSelection,whereParams,null)!=null;
	}

	public<T> T queryObject(Class<T> smartTableClass,String whereSelection,String[] whereParams,String[] fields) throws Exception {
		return queryObject(smartTableClass,whereSelection,whereParams,null,fields);
	}

	public<T> T queryObject(Class<T> smartTableClass,String whereSelection,String[] whereParams,String orderBy,String[] fields) throws Exception {
		List<T> objects = queryObjects(smartTableClass,whereSelection,whereParams,orderBy,fields,0,1);
		return objects.size()<=0?null:objects.get(0);
	}

	public<T> List<T> queryObjects(Class<T> smartTableClass) throws Exception {
		return queryObjects(smartTableClass,null,null,null,null,0,-1);
	}

	public<T> List<T> queryObjects(Class<T> smartTableClass,String whereSelection,String[] whereParams,String orderBy,String[] fields,int offset,int count) throws Exception {
		SmartTableInfo smartTableInfo = loadTable(smartTableClass);
		if(smartTableInfo==null) {
			throw new ClassNotSmartTableException(smartTableClass);
		}

		String limit = null;
		if(offset>=0&&count>0){
			limit = offset+","+count;
		}
		Cursor cursor = getDatabase().query(smartTableInfo.getSmartTableName(),fields,whereSelection,whereParams,null,null,orderBy,limit);
		List<T> results = new ArrayList<>();
		if(cursor==null){
			return results;
		}
		while( cursor.moveToNext() ){
			results.add(resultSet2Object(cursor, smartTableClass, smartTableInfo, fields));
		}
		cursor.close();
		return results;
	}

	public boolean update(Object object,String whereSelection,String[] whereParams,String[] fields) throws Exception  {
		Class<?> smartTableClass = object.getClass();
		SmartTableInfo smartTableInfo = loadTable(smartTableClass);
		if(smartTableInfo==null) {
			throw new ClassNotSmartTableException(smartTableClass);
		}
		if(fields==null||fields.length==0) {
			fields = smartTableInfo.getFieldNames();
		}
		ContentValues contentValues = new ContentValues();
		prepareValues(object,smartTableInfo,fields,false,contentValues);
		return 0 < getDatabase().update(smartTableInfo.getSmartTableName(),contentValues,whereSelection,whereParams);
	}

	public boolean insert(Object object) throws Exception  {
		Class<?> smartTableClass = object.getClass();
		SmartTableInfo smartTableInfo = loadTable(smartTableClass);
		if(smartTableInfo==null) {
			throw new ClassNotSmartTableException(smartTableClass);
		}
		String[] fields = smartTableInfo.getFieldNames();
		ContentValues contentValues = new ContentValues();
		prepareValues(object,smartTableInfo,fields,false,contentValues);
		return 0 < getDatabase().insert(smartTableInfo.getSmartTableName(),null,contentValues);
	}

	public<T> boolean delete(Class<T> smartTableClass,String whereClause,String[] whereArgs) throws Exception {
		SmartTableInfo smartTableInfo = loadTable(smartTableClass);
		if(smartTableInfo==null) {
			throw new ClassNotSmartTableException(smartTableClass);
		}
		return 0 < getDatabase().delete(smartTableInfo.getSmartTableName(),whereClause,whereArgs);
	}

	public static String getSelectionString(SmartTableInfo smartTableInfo,String[] fields,boolean includeAutoIncrement) throws NoSmartFieldException {
		if(fields==null||fields.length==0) {
			return "*";
		}
		StringBuilder selectionBuilder = new StringBuilder();
		for(int i=0;i<fields.length;i++) {
			String fieldName = fields[i];
			SmartFieldInfo fieldInfo = smartTableInfo.getField(fieldName);
			if(fieldInfo==null) {
				throw new NoSmartFieldException(fieldName);
			}
			if(!includeAutoIncrement) {
				if(fieldInfo.getSmartField().autoIncrement()) {
					continue;
				}
			}
			selectionBuilder.append(fieldName);
			if(i<fields.length-1) {
				selectionBuilder.append(",");
			}
		}
		return selectionBuilder.toString();
	}

	public static String getSetString(SmartTableInfo smartTableInfo,String[] fields,boolean includeAutoIncrement) throws NoSmartFieldException {
		if(fields==null||fields.length==0) {
			return "*";
		}
		StringBuilder selectionBuilder = new StringBuilder();
		for(int i=0;i<fields.length;i++) {
			String fieldName = fields[i];
			SmartFieldInfo fieldInfo = smartTableInfo.getField(fieldName);
			if(fieldInfo==null) {
				throw new NoSmartFieldException(fieldName);
			}
			if(!includeAutoIncrement) {
				if(fieldInfo.getSmartField().autoIncrement()) {
					continue;
				}
			}
			selectionBuilder.append(fieldName);
			selectionBuilder.append("=?");
			if(i<fields.length-1) {
				selectionBuilder.append(",");
			}
		}
		return selectionBuilder.toString();
	}

	public static void prepareValues(Object object,SmartTableInfo smartTableInfo,String[] fields,boolean includeAutoIncrement,ContentValues contentValues) throws Exception {
		for(int i=0;i<fields.length;i++) {
			String fieldName = fields[i];
			SmartFieldInfo fieldInfo = smartTableInfo.getField(fieldName);
			if(fieldInfo==null) {
				throw new NoSmartFieldException(fieldName);
			}
			if(!includeAutoIncrement) {
				if(fieldInfo.getSmartField().autoIncrement()) {
					continue;
				}
			}
			Object fieldObject = null;
			try {
				fieldObject = fieldInfo.getField().get(object);
			} catch (Exception e) {
				throw new CanNotGetFieldException();
			}
			contentValues.put(fieldName , object2String(fieldObject));
		}
	}
	
	public static String object2String(Object object) {
		if(object==null)
			return "";
		if(object instanceof String) {
			return object.toString();
		}
		return getGson().toJson(object);
	}
	
	public static Object string2Object(String string,Class<?> type) {
		if(type.equals(String.class)) {
			return string;
		}
		return getGson().fromJson(string, type);
	}
	
	public<T> T resultSet2Object(Cursor cursor,Class<T> clazz,SmartTableInfo smartTableInfo,String[] fields) throws Exception {
		T object;
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			throw new Exception("can not instance:"+clazz);
		}
		if(fields==null) {
			fields = smartTableInfo.getFieldNames();
		}
		for(String fieldName:fields) {
			SmartFieldInfo smartFieldInfo = smartTableInfo.getField(fieldName);
			Field field = smartFieldInfo.getField();
			int columnIndex = cursor.getColumnIndex(fieldName);
			Object data = null;
			if(smartFieldInfo.getSmartField().blob()){
				data = cursor.getBlob(columnIndex);
			} else {
				String string = cursor.getString(columnIndex);
				data = string2Object(string,field.getType());
			}
			try {
				field.set(object, data);
			} catch (Exception e) {
				throw new FieldTypeCaseException(clazz, fieldName);
			}

		}
		return object;
	}
}
