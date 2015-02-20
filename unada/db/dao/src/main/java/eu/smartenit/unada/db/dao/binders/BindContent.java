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

import eu.smartenit.unada.db.dto.Content;

/**
 * The BindContent annotation. It maps the parameters of a sql statement 
 * in AbstractContentDAO into Content parameters.
 *
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
@BindingAnnotation(BindContent.ContentBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindContent {

	public static class ContentBinderFactory implements BinderFactory {

		public Binder<BindContent, Content> build(Annotation annotation) {
			return new Binder<BindContent, Content>() {
				public void bind(SQLStatement<?> q, BindContent bind, Content arg) {
					q.bind(bind.value() + ".contentid", arg.getContentID());
					q.bind(bind.value() + ".size", arg.getSize());
					q.bind(bind.value() + ".url", arg.getUrl());
					q.bind(bind.value() + ".path", arg.getPath());
					q.bind(bind.value() + ".quality", arg.getQuality());
					q.bind(bind.value() + ".cachetype", arg.getCacheType());
					q.bind(bind.value() + ".cachedate", arg.getCacheDate().getTime());
					q.bind(bind.value() + ".cachescore", arg.getCacheScore());
					q.bind(bind.value() + ".category", arg.getCategory().toString());
					q.bind(bind.value() + ".downloaded", arg.isDownloaded());
					q.bind(bind.value() + ".prefetched", arg.isPrefetched());
                    q.bind(bind.value() + ".vimeoprefetched", arg.isPrefetchedVimeo());
				}
			};
		}
	}

	String value();
}
