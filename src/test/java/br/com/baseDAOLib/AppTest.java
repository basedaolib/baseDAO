package br.com.baseDAOLib;

import java.util.Date;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.baseDAOLib.DAO.UsuarioDAO;
import br.com.baseDAOLib.entity.Email;
import br.com.baseDAOLib.entity.Usuario;

/**
 * Unit test for simple App.
 */
@RunWith(WeldJUnit4Runner.class)
public class AppTest 
    extends TestCase
{
	
	@Inject
	private UsuarioDAO usuarioDAO; 
	
	@Test
    public void testInicio(){
    	Usuario usuario = new Usuario();
    	usuario.setLogin("henrique");
    	usuario.setNivel("1");
    	usuario.setSenha("123");
    	Email email = new Email();
    	email.setEmail("fsdfs@dds.com.br");
    	usuario.setEmail(email);
    	usuario = usuarioDAO.insert(usuario);
    	
    	
    }
}
