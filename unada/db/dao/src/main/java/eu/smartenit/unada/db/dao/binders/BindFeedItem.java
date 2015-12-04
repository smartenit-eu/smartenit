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

import eu.smartenit.unada.db.dto.FeedItem;

/**
 * The BindFeedItem annotation. It maps the parameters of a sql
 * statement in AbstractFeedItemDAO into FeedItem
 * parameters.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
@BindingAnnotation(BindFeedItem.FeedItemBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindFeedItem {

	public static class FeedItemBinderFactory implements
			BinderFactory {

		public Binder<BindFeedItem, FeedItem> build(
				Annotation annotation) {
			return new Binder<BindFeedItem, FeedItem>() {
				public void bind(SQLStatement<?> q,
						BindFeedItem bind, FeedItem arg) {
					q.bind(bind.value() + ".feeditemid", arg.getFeedItemID());
					q.bind(bind.value() + ".contentid", arg.getContentID());
					q.bind(bind.value() + ".userid", arg.getUserID());
					q.bind(bind.value() + ".type", arg.getType());
					q.bind(bind.value() + ".time", arg.getTime()!=null ? arg.getTime().getTime() : System.currentTimeMillis());
					q.bind(bind.value() + ".feedtype", arg.getFeedType().toString());
				}
			};
		}
	}

	String value();
}
