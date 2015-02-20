package eu.smartenit.enduser.app.facebookAccount;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.smartenit.enduser.app.facebookAccount.LoginManager;
import eu.smartenit.enduser.app.facebookAccount.RemoteLoginReply;
import android.test.AndroidTestCase;

public class LoginManagerTest extends AndroidTestCase {
	private LoginManager loginManager;

	@BeforeClass
	public void setUp() throws Exception {
		loginManager = new LoginManager(
				111,
				"123",
				"http://mona.ps.e-technik.tu-darmstadt.de/da-sense/hamidelmi/Test.php",
				true);
	}

/*	@Test
	public void testLogin() {
		RemoteLoginReply result = null;// loginManager.login(123);

		assertNotNull(result);
		assertFalse(result.isOutcome());
		assertEquals(result.getPassword(), "smartenit123");
		assertEquals(result.getPrivateSSID(), "RB-HORST-Private");
	}
*/
	
	@Test
	public void testGetFacebookID() {
		assertEquals(loginManager.getFacebookID(), 111);
	}

	@Test
	public void testGetAuthToken() {
		assertEquals(loginManager.getAuthToken(), "123");
	}

}
