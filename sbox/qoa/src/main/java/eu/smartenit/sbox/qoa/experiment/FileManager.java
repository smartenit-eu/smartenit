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
package eu.smartenit.sbox.qoa.experiment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File manager class created to enable writing traffic details to file.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public class FileManager {
	
	/**
	 * Method used to update file with new record.
	 * 
	 * @param path
	 * 		path to file
	 * @param fileName
	 * 		file name
	 * @param newRecord
	 * 		new record
	 */
	public void updateFile(String path, String fileName, String newRecord) {
		File file = new File(path + fileName);
		try {
			if(!file.exists()) {
				File newFile = new File(path, fileName);
				newFile.createNewFile();
				file = newFile;
			}
			if(file.exists()) {
				FileOutputStream output = new FileOutputStream(file, true);
				output.write(newRecord.getBytes());
				output.close();
			} else {
				throw new IllegalStateException("Cannot create " + file.getAbsolutePath() + " file");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create " + file.getAbsolutePath() + " file");
		}
	}
}
