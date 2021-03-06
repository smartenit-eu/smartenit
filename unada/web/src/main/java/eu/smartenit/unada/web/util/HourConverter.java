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
package eu.smartenit.unada.web.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("eu.smartenit.unada.web.util.HourConverter")
public class HourConverter implements Converter {

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Float.valueOf(value) * 3600 * 1000;
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
        if (value == null) {
            return "0";
        }
        long val = (Long) value;
        long hours = (long) val / 3600 / 1000;
        return String.valueOf(hours);
    }

}
