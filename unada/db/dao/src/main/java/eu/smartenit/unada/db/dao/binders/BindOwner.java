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
package eu.smartenit.unada.db.dao.binders;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import eu.smartenit.unada.db.dto.Owner;

/**
 * The BindOwner annotation. It maps the parameters of a sql
 * statement in AbstractOwnerDAO into Owner
 * parameters.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
@BindingAnnotation(BindOwner.OwnerBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindOwner {

	public static class OwnerBinderFactory implements
			BinderFactory {

		public Binder<BindOwner, Owner> build(
				Annotation annotation) {
			return new Binder<BindOwner, Owner>() {
				public void bind(SQLStatement<?> q,
						BindOwner bind, Owner arg) {
					q.bind(bind.value() + ".facebookid", arg.getFacebookID());
					q.bind(bind.value() + ".oauthtoken", arg.getOauthToken());
				}
			};
		}
	}

	String value();
}
