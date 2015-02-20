package eu.smartenit.enduser.app.facebookAccount;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.smartenit.enduser.app.facebookAccount.RemoteLoginReply;
import android.test.AndroidTestCase;

public class RemoteLoginReplyTest extends AndroidTestCase {

	private RemoteLoginReply remoteLoginReply;

	@BeforeClass
	public void setUp() throws Exception {
		String json = "{\"type\": \"remoteLoginReply\",\"outcome\": 0,\"wifiConfiguration\": {\"ssid\": \"RB-HORST-Private\",\"password\": \"smartenit123\"}}";

		InputStream is = new ByteArrayInputStream(json.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		remoteLoginReply = new RemoteLoginReply(br);
	}

	@Test
	public void testRemoteLoginReplyBufferedReader() {
		assertFalse(remoteLoginReply.isOutcome());
	}

	@Test
	public void testIsOutcome() {
		assertEquals("Wrong return status", false, remoteLoginReply.isOutcome());
	}

	@Test
	public void testGetPrivateSSID() {
		assertEquals(remoteLoginReply.getPrivateSSID(), "RB-HORST-Private");
	}

	@Test
	public void testGetPassword() {
		assertEquals(remoteLoginReply.getPassword(), "smartenit123");
	}

}
