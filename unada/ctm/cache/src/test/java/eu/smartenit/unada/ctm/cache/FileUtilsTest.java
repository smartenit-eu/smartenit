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
package eu.smartenit.unada.ctm.cache;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import eu.smartenit.unada.ctm.cache.util.FileUtils;

public class FileUtilsTest {
	
	@Test
	public void testDeleteFile() throws IOException {
		String filePath = "1/2/test.txt";
		File f = new File(filePath);
		f.getParentFile().mkdirs();
		f.createNewFile();
		assertTrue(f.exists());
		
		boolean flag = FileUtils.deleteFile("1");
		assertTrue(flag);
		assertFalse(f.exists());

        flag = FileUtils.deleteFile("daad");
        assertFalse(flag);

        flag = FileUtils.deleteFile(null);
        assertFalse(flag);
	}
	
	@Test
	public void testGetRootPath() {
        assertNull(FileUtils.getRootDir(""));
        assertNull(FileUtils.getRootDir(null));
		assertEquals(FileUtils.getRootDir("1/2/3/4/"), "1");
		assertEquals(FileUtils.getRootDir("1"), "1");
		assertEquals(FileUtils.getRootDir("/1/2/3/4/"), "");
	}

}
