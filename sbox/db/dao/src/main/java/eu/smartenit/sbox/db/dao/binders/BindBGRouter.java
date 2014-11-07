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
package eu.smartenit.sbox.db.dao.binders;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import eu.smartenit.sbox.db.dto.BGRouter;

/**
 * The BindBGRouter annotation. It maps the parameters of a sql statement in AbstractBGRouterDAO 
 * into BGRouter parameters.
 *
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
@BindingAnnotation(BindBGRouter.BGRouterBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindBGRouter {

	public static class BGRouterBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindBGRouter, BGRouter>() {
				public void bind(SQLStatement q, BindBGRouter bind, BGRouter arg) {
					q.bind(bind.value() + ".address", arg.getManagementAddress().getPrefix());
					q.bind(bind.value() + ".snmpCommunity", arg.getSnmpCommunity());
				}
			};
		}
	}

	String value();
}
