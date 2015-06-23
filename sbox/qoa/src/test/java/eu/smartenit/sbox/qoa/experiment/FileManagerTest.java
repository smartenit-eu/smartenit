/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
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
package eu.smartenit.sbox.qoa.experiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

/**
 * Unit test checking file operations.
 * 
 *  @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class FileManagerTest {
	private static final String FILE_NAME = "packets.txt";
	private static final String PATH = "." + File.separator;
	private static final String RECORD = "test\n";
	private FileManager fileManager = new FileManager();
	
	@Test
	public void shouldCreateFile() throws IOException {
		fileManager.updateFile(PATH, FILE_NAME, RECORD);
		fileManager.updateFile(PATH, FILE_NAME, RECORD);
		
		File file = new File(PATH, FILE_NAME);
		assertTrue(file.exists());
		
		final StringBuilder expected = new StringBuilder();
		expected.append(RECORD).append(RECORD);
		
		FileInputStream input = new FileInputStream(file);
		final StringBuilder content = new StringBuilder();
		int byteCode;
		while ((byteCode = input.read()) != -1) {
			content.append((char) byteCode);
		}
		
		assertEquals(expected.toString(), content.toString());
		input.close();
	}
	
	@After
	public void clean() {
		final File file = new File(PATH, FILE_NAME);
		if(file.exists()) {
			file.delete();
		}
	}
}