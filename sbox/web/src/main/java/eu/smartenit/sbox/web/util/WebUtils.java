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
package eu.smartenit.sbox.web.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * The WebUtils class. It includes a support method for all web app beans.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class WebUtils {
	
	/**
	 * The method that transforms an arraylist of objects to an array of objects
	 * 
	 * @param c The object class
	 * @param objectsList The arraylist of objects
	 * 
	 * @return The array of objects
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public static <K> K[] listToArray(Class<K> c, List<K> objectsList) {
		K[] objectsArray;
		if (objectsList == null)
			objectsArray = (K[]) Array.newInstance(c, 0);
		else {
			objectsArray = (K[]) Array.newInstance(c, objectsList.size());
			for (int i = 0; i < objectsList.size(); i++)
				objectsArray[i] = objectsList.get(i);
		}
		return objectsArray;
	}

}
