package br.com.baseDAOLib.DAO;

import java.util.List;


public abstract interface BaseDAO<T> {

	public T save(T entity);

	public T delete(T entity);

	public T update(T entity);

	public T findEntityForId(long id);
	
	public List<T> findEntitiesForProperties(int beginning, int end, String order, String names, Object... values);
	
	public T findEntityForProperties(String names, Object... values);
	
	public<E> List<E> findFieldForProperties(int beginning, int end, String order, String names, Object... values);
	
	
}
