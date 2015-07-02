package br.com.baseDAOLib.DAO;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

class JpaUtil {
	
	private EntityManagerFactory factory =  Persistence.createEntityManagerFactory("projeto");
	
	
	@Produces
	public EntityManager getEntityManager(){
		EntityManager manager = factory.createEntityManager();
		return manager;
	}
}
