/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.om;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

public class TestDownloadContent extends OverlayBaseTest {

	private static Logger log = LoggerFactory.getLogger(TestDownloadContent.class);
	Path original;
	Content contentOM1 = new Content();
	Content contentOM2 = new Content();
	Path copy;

	static int arraySize = 999;
	static int loopSize = 1000;
	
	long startTime;
	long stopTime;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		om1.getuNaDaInfo().setHopCount(1);
		om2.getuNaDaInfo().setHopCount(1);
		log.debug("Creating file for download test.");
		original = Files.createTempFile("OMTest", "" );
		SeekableByteChannel c = Files.newByteChannel(original, StandardOpenOption.WRITE);
		Random rnd = new Random();
		byte[] bytes = new byte[arraySize];
		for(int i = 0 ; i < loopSize; i++){
			rnd.nextBytes(bytes);
			ByteBuffer b = ByteBuffer.wrap(bytes);
			while(c.write(b)>0){}
		}
		c.close();
		log.debug("Test file created: {}", original);

		contentOM1.setPath(original.toString());
		contentOM1.setContentID(contentID1);
		contentOM1.setSize(arraySize*loopSize);

		ContentDAO contentDAO = mock(ContentDAO.class);
		when(contentDAO.findById(contentID1)).thenReturn(contentOM1);
		DAOFactory.setContentDAO(contentDAO);

		copy = Files.createTempFile("OMTest", "" );
		contentOM2.setPath(copy.toString());
		contentOM2.setContentID(contentID1);
		
	}

	@Test
	public void test() throws Exception {

		om1.advertiseContent(contentID1);
		startTime = System.currentTimeMillis();
		IFutureDownload future = om2.downloadContent(contentOM2);
		future.get();
		stopTime = System.currentTimeMillis();

		assertTrue(future.isDone());
		assertTrue(future.isSuccess());

		MessageDigest md = MessageDigest.getInstance("MD5");
		MessageDigest mdCopy = MessageDigest.getInstance("MD5");
		
		try (   InputStream is = Files.newInputStream(original);
				InputStream is2 = Files.newInputStream(copy);) {

			DigestInputStream disOrig = new DigestInputStream(is, md);
			byte[] buffer = new byte[1024];
			int read = buffer.length;
			while(read >= buffer.length){
				read = disOrig.read(buffer);
				log.debug("Read last buffer from orig = {}", read);
			}
			
			int readcopy = buffer.length;
			DigestInputStream disCopy = new DigestInputStream(is2, mdCopy);
			while(readcopy >= buffer.length){
				readcopy = disCopy.read(buffer);
				log.debug("Read last buffer from copy = {}", readcopy);
			}
			
			assertArrayEquals(md.digest(), mdCopy.digest());
		}


	}

	@After
	public void cleanUp(){
		log.info("Time elapsed: {}ms", stopTime - startTime);
		
		try {
			Files.delete(original);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Files.delete(copy);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
