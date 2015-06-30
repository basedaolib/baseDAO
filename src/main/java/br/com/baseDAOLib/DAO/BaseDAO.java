package br.com.baseDAOLib.DAO;

import java.util.List;


public abstract interface BaseDAO<T> {

	public T insert(T entity);

	public T delete(T entity);

	public T update(T entity);
	
	public T disassociate(T entity);

	public T findEntityForId(long id);
	
	public List<T> findEntitiesForProperties(int beginning, int end, String order, String names, Object... values);
	
	public T findEntityForProperties(String names, Object... values);
	
	public <E> List<E> findFieldsForProperties(int beginning, int end, String order, String field, String names, Object... values);
	
	public<E> E findFieldForProperties(String field, String names, Object... values);
}
