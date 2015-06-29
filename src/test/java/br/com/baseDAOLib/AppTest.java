package br.com.baseDAOLib;

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
    	usuario.setLogin("trst");
    	usuario.setNivel("1");
    	usuario.setSenha("123");
    	Email email = new Email();
    	email.setEmail("fsdfs@dds.com.br");
    	usuario.setEmail(email);
    	usuario = usuarioDAO.save(usuario);

    	System.out.println(usuarioDAO.findEntitiesForProperties(0, 0,"login" , "login>=, email=", "trst", email).size());
    	
    	System.out.println(usuarioDAO.findEntityForProperties("email", email).getLogin());
    }
}
