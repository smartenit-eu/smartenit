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
package eu.smartenit.sbox.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * The IntegerListConverter class. It maps a list of integers to a string.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
@FacesConverter("eu.smartenit.sbox.web.util.IntegerListConverter")
public class IntegerListConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if (value == null || value.isEmpty()) {
			return new ArrayList<Integer>();
		} 
		return stringToList(value);
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		if (value == null) {
			return "[]";
		}
		return ((List<Integer>) value).toString();
	}
	
	private List<Integer> stringToList(String str) {
		List<Integer> list = new ArrayList<Integer>();
		String[] ints = str.substring(1, str.length()-1).split(",");
		for (String s : ints) {
			if (!s.isEmpty())
				list.add(Integer.valueOf(s.trim()));
		}
		return list;
	}

}
