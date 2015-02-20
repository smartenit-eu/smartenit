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
package eu.smartenit.unada.web.util;

import java.io.Serializable;

/**
 * The DemoEvent class.
 *
 * @author George Petropoulos
 * @version 2.1
 *
 */
public class DemoEvent implements Serializable{

    public DemoEvent() {

    }

    private String title;

    private String text;

    private String alertclass;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlertclass() {
        return alertclass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DemoEvent demoEvent = (DemoEvent) o;

        if (alertclass != null ? !alertclass.equals(demoEvent.alertclass) : demoEvent.alertclass != null) return false;
        if (text != null ? !text.equals(demoEvent.text) : demoEvent.text != null) return false;
        if (title != null ? !title.equals(demoEvent.title) : demoEvent.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (alertclass != null ? alertclass.hashCode() : 0);
        return result;
    }

    public void setAlertclass(String alertclass) {
        this.alertclass = alertclass;
    }



    @Override
    public String toString() {
        return "DemoEvent{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", alertclass='" + alertclass + '\'' +
                '}';
    }
}
