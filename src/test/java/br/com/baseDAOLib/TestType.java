package br.com.baseDAOLib;

import static org.junit.Assert.*;

import org.junit.Test;

import br.com.baseDAOLib.DAO.UsuarioDAO;
import br.com.baseDAOLib.DAO.UsuarioDAOImpl;
import br.com.baseDAOLib.entity.Usuario;

public class TestType {

	@Test
	public void test() {
		UsuarioDAO a = new UsuarioDAOImpl();
		a.findFieldForProperties(0, 0, null, null, null);
	}

}
