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

import eu.smartenit.sbox.db.dto.DARouter;

/**
 * The BindDARouter annotation. It maps the parameters of a sql statement in AbstractDARouterDAO 
 * into DARouter parameters.
 *
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
@BindingAnnotation(BindDARouter.DARouterBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindDARouter {

	public static class DARouterBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindDARouter, DARouter>() {
				public void bind(SQLStatement q, BindDARouter bind, DARouter arg) {
					q.bind(bind.value() + ".address", arg.getManagementAddress().getPrefix());
					q.bind(bind.value() + ".snmpCommunity", arg.getSnmpCommunity());
                    q.bind(bind.value() + ".ofSwitchDPID", arg.getOfSwitchDPID());
				}
			};
		}
	}

	String value();
}
