package br.com.baseDAOLib.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import br.com.baseDAOLib.DAO.annotation.Consist;
import br.com.baseDAOLib.DAO.exception.NotedInvokeMethodException;

class BaseDAOReflection {
	
	public void percorrerAtributos(Object entity, Class<?> clazz){
		this.percorrerAtributos(entity, clazz, null);
	}
	
	private void percorrerAtributos(Object entity, Class<?> clazz, Method[] ignore){
		try {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				field.setAccessible(true);
				Object value = field.get(entity);
				if(value != null){
					if(contemAnotation(field.getType())){
						this.percorrerAtributos(value, value.getClass());
					}
				}
			}
			this.executeAnnotatedMethods(entity, clazz, ignore);
			
			if(!Object.class.equals(clazz.getSuperclass()))
				this.percorrerAtributos(entity, clazz.getSuperclass(), clazz.getDeclaredMethods());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
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
