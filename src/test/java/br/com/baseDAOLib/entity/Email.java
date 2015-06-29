package br.com.baseDAOLib.entity;

import javax.persistence.Embeddable;

import br.com.baseDAOLib.DAO.annotation.Consist;

@Embeddable
public class Email {

	private String email;
	@Consist
	public void con(){
		System.out.println("Embeddable");
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
