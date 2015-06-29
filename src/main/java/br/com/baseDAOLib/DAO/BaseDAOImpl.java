package br.com.baseDAOLib.DAO;


import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.baseDAOLib.DAO.exception.PredicateInvalidException;

import com.mchange.util.DuplicateElementException;

/**
 *
 * @author henrique
 */
@SuppressWarnings("unchecked")
abstract class BaseDAOImpl<T> implements BaseDAO<T>{
	
	@Inject
	private EntityManager manager;
	
	private BaseDAOReflection reflection = new BaseDAOReflection();
	
	private final Class<T> entityClass = (Class<T>) ((ParameterizedType) 
			getClass().getGenericSuperclass()).getActualTypeArguments()[0];
   
    public T save(T entity){
    	reflection.goFields(entity, entity.getClass());
    	entity = beforeSaving(entity);
		this.manager.persist(entity);
		return afterSaving(entity);
	}

	public T delete(T entity){
		entity = beforeDelete(entity);
		this.manager.remove(entity);
		return afterDelete(entity);
	}

	public T update(T entity){
		reflection.goFields(entity, entity.getClass());
		entity = beforeUpdate(entity);
		this.manager.merge(entity);
		return afterUpdate(entity);
	
	}
	
	public List<T> findEntitiesForProperties(int beginning, int end, String order, String names, Object... values) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder() ;
		CriteriaQuery<T> criteriaQuery = builder.createQuery(entityClass);
		Root<T> root = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(root) ;
		
		criteriaQuery.where(mountWhere(builder, root, buildMap(names, values)));
		
		if(order != null)
			criteriaQuery.orderBy(builder.asc(root.get(order)));
		
		TypedQuery<T> query = manager.createQuery((criteriaQuery));
		
		if(beginning > 0)
			query.setFirstResult(beginning);
		if(end > 0)
			query.setMaxResults(end);
		
		
		return query.getResultList();
	}
	
	public T findEntityForId(long id) {
		return (T) this.manager.find(entityClass, id);
	}
	
	public T findEntityForProperties(String names, Object... values) {
		List<T> list = findEntitiesForProperties(0, 2, null, names, values);
		if(list.size() > 1){
			throw new DuplicateElementException("more than one " + entityClass.getSimpleName() + " has been found.");
		}
		return list.get(0);
	}
	
	public <E> List<E> findFieldsForProperties(int beginning, int end, String order, String field, String names, Object... values) {
		List<E> x = new ArrayList<E>();
		ParameterizedType paramType;
        paramType = (ParameterizedType) x.getClass().getGenericInterfaces()[0];
        Class<E> parameterClass = (Class<E>) paramType.getActualTypeArguments()[0].getClass();
        
        
        CriteriaBuilder builder = manager.getCriteriaBuilder() ;
		CriteriaQuery<E> criteriaQuery = builder.createQuery(parameterClass);
		Root<T> root = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(root.<E>get(field));
		
		criteriaQuery.where(mountWhere(builder, root, buildMap(names, values)));
		
		if(order != null)
			criteriaQuery.orderBy(builder.asc(root.get(order)));
		
		TypedQuery<E> query = manager.createQuery((criteriaQuery));
		
		if(beginning > 0)
			query.setFirstResult(beginning);
		if(end > 0)
			query.setMaxResults(end);
		
		
		return query.getResultList();
	}
	
	public<E> E findFieldForProperties(String field, String names, Object... values) {
		List<E> list = this.<E>findFieldsForProperties(0, 2, null,field , names, values);
		if(list.size() > 1){
			throw new DuplicateElementException("more than one " + field + " has been found.");
		}
		return list.get(0);
	}
	
	
	protected T beforeSaving(T entity){ return entity;}
	protected T afterSaving(T entity){ return entity;}
	
	protected T beforeDelete(T entity){ return entity;}
	protected T afterDelete(T entity){ return entity;}
	
	protected T beforeUpdate(T entity){ return entity;}
	protected T afterUpdate(T entity){ return entity;}

	protected EntityManager getEntityManager(){
		return manager;
	}
	
	private List<Parameter> buildMap(String names, Object... values){
		List<Parameter> parameters = new ArrayList<Parameter>();
		Parameter parameter = null;
		
		String[] properties = names.split(Pattern.quote(","));
		
		if(properties.length != values.length){
			
		}
		Predicates predicate;
		int i = 0;
		for(String property : properties){
			property = property.trim();
			parameter = new Parameter();
			predicate = this.getPredicate(property);
			parameter.setProperty( property.replace(predicate.getValue(), "").trim());
			parameter.setPredicates(predicate);
			parameter.setValue(values[i]);
			parameters.add(parameter);
			i++;
		}
		return parameters;
	}
	
	private Predicates getPredicate(String property){
		for(Predicates predicate : Predicates.values()){
			if(property.contains(predicate.getValue())){
				return predicate;
			}
		}
		return Predicates.equal;
	}
	
	private Predicate[] mountWhere(CriteriaBuilder builder, Root<T> root, List<Parameter> maps){
		Predicate[] predicates = new Predicate[maps.size()];
		
		for(int i = 0; i < maps.size(); i++){
			predicates[i] = this.createPredicate(builder, root, maps.get(i));
		}
		
		return predicates;
	}
	
	@SuppressWarnings("rawtypes")
	private Predicate createPredicate(CriteriaBuilder builder, Root<T> root, Parameter parameter){
		Predicate predicate = null;
		switch (parameter.getPredicates()) {
			case notEqual:
				predicate = builder.notEqual(root.get(parameter.getProperty()), parameter.getValue());
				break;
			case greaterThan:
				validateComparable(parameter);
				predicate = builder.greaterThan(root.<Comparable>get(parameter.getProperty()), 
						(Comparable) parameter.getValue());
				break;
			case greaterThanOrEqualTo:
				validateComparable(parameter);
				predicate = builder.greaterThanOrEqualTo(root.<Comparable>get(parameter.getProperty()), 
						(Comparable) parameter.getValue());
				break;
			case lessThan:
				validateComparable(parameter);
				predicate = builder.lessThan(root.<Comparable>get(parameter.getProperty()), 
						(Comparable) parameter.getValue());
				break;
			case lessThanOrEqualTo:
				validateComparable(parameter);
				predicate = builder.lessThan(root.<Comparable>get(parameter.getProperty()), 
						(Comparable) parameter.getValue());
				break;
			case like:
				validateString(parameter);
				predicate = builder.like(root.<String>get(parameter.getProperty()), "%" + (
						(String) parameter.getValue()) + "%");
				break;
		default:
			predicate = builder.equal(root.get(parameter.getProperty()), parameter.getValue());
			break;
		}
		
		return predicate;
	}
	
	public void validateComparable(Parameter parameter){
		if(!(parameter.getValue() instanceof Comparable)) {
			throw new PredicateInvalidException("the attribute "+ parameter.getProperty() 
					+" must implement java.lang.Comparable to use " + parameter.getPredicates().getValue() + ".");
		}
	}
	
	public void validateString(Parameter parameter){
		if(!(parameter.getValue() instanceof String)) {
			throw new PredicateInvalidException("the attribute "+ 
					parameter.getProperty() +" must implement comparable to use LIKE.");
		}
	}
			
}

