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

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("eu.smartenit.unada.web.util.SizeConverter")
public class SizeConverter implements Converter {

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Float.valueOf(value) * 1000000;
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
        if (value == null) {
            return "0";
        }
        long val = (Long) value;

        double x = (double) val / 1000000;
        DecimalFormat df = new DecimalFormat("#.##");
        String dx = df.format(x);

        return String.valueOf(Double.valueOf(dx));
    }

}
