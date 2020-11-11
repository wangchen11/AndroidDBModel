package person.wangchen11.model.smartmodel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface SmartField {
	public String name() default "";
	public boolean autoIncrement() default false;
	public boolean notNull() default false;
	public boolean primaryKey() default false;
	public String type() default "";//like "varchar(100)"
	public String defaultValue() default "";
}
