package br.com.baseDAOLib.DAO;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

class JpaUtil {
	
	private EntityManagerFactory factory =  Persistence.createEntityManagerFactory("projeto");
	
	
	@Produces
	public EntityManager getEntityManager(){
		EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		return manager;
	}
	
	public void finaliza(@Disposes EntityManager manager) {
		manager.getTransaction().commit();
		
		if(manager.isOpen())
			manager.close();
	}
	
}
