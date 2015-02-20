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
package eu.smartenit.unada.ctm.cache.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The FileUtils class. It includes IO support methods.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class FileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * The method that returns the root directory for given file.
	 * 
	 * @param file The given file.
	 * 
	 * @return The root directory.
	 */
	public static String getRootDir(String file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
		else if (file.contains("/")) {
            return file.split("/")[0];
        }
        else {
            return file;
        }
	}
	
	
	/**
	 * The method that deletes a file recursively.
	 * 
	 * @param file The file to be deleted.
	 * 
	 * @return The outcome of the deletion.
	 */
	public static boolean deleteFile(String file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
		Path dir = Paths.get(file);
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {

					logger.debug("Deleting file: " + file);
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {

					logger.debug("Deleting dir: " + dir);
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						throw exc;
					}
				}

			});
		} catch (IOException e) {
			logger.error("Exception while deleting file " + file 
					+ ": " + e.getMessage());
			return false;
		}
		return true;
	}

}
