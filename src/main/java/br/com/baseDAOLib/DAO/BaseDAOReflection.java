package br.com.baseDAOLib.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import br.com.baseDAOLib.DAO.annotation.Consist;
import br.com.baseDAOLib.DAO.exception.NotedInvokeMethodException;

class BaseDAOReflection {
	
	public void goFields(Object entity, Class<?> clazz){
		this.goFields(entity, clazz, null);
	}
	
	private void goFields(Object entity, Class<?> clazz, Method[] ignore){
		try {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				field.setAccessible(true);
				Object value = field.get(entity);
				if(value != null){
					if(value instanceof Collection<?>){
						this.goFieldsCollection((Collection<?>) value, value.getClass());
					}else
						if(contemAnotation(field.getType())){
							this.goFields(value, value.getClass());
						}
				}
			}
			this.executeAnnotatedMethods(entity, clazz, ignore);
			
			if(!Object.class.equals(clazz.getSuperclass()))
				this.goFields(entity, clazz.getSuperclass(), clazz.getDeclaredMethods());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void goFieldsCollection(Collection<?> collection, Class<?> clazz){
		for(Object item : collection){
			this.goFields(item, item.getClass());
		}
	}
	
	private void executeAnnotatedMethods(Object object, Class<?> clazz, Method[] ignore){
		try {
			
			for(Method method : clazz.getDeclaredMethods()){
				method.setAccessible(true);
				if(method.isAnnotationPresent(Consist.class) && notIgnored(method, ignore)){
					if(method.getParameters().length > 0){
						throw new NotedInvokeMethodException("the method annotated with annotation @Consist can not contain parameters");
					}
					method.invoke(object);
				}
			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private boolean notIgnored(Method method, Method[] ignore){
		if(ignore != null){
			for(Method item : ignore){
				if(methodEquals(item, method))
					return false;
			}
		}
		return true;
	}
	
	private boolean contemAnotation(Class<?> field){
		return field.isAnnotationPresent(Entity.class) || field.isAnnotationPresent(Embeddable.class);
	}
	
	private boolean methodEquals(Method method, Method other){
		return method.getName().equals(other.getName()) && 
				method.getParameters().length == other.getParameters().length;
	}
	
	

}
