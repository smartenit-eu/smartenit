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

import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import eu.smartenit.unada.db.dto.SocialScores;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.*;

/**
 * The BindSocialScores annotation. It maps the parameters of a sql
 * statement in AbstractSocialScoresDAO into SocialScores
 * parameters.
 * 
 * @authors George Petropoulos
 * @version 3.1
 * 
 */
@BindingAnnotation(BindSocialScores.SocialScoresBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindSocialScores {

	public static class SocialScoresBinderFactory implements
			BinderFactory {

		public Binder<BindSocialScores, SocialScores> build(
				Annotation annotation) {
			return new Binder<BindSocialScores, SocialScores>() {
				public void bind(SQLStatement<?> q,
						BindSocialScores bind, SocialScores arg) {
					q.bind(bind.value() + ".contentid", arg.getContentID());
					q.bind(bind.value() + ".alpha", arg.getAlpha());
					q.bind(bind.value() + ".delta", arg.getDelta());
					q.bind(bind.value() + ".eta", arg.getEta());
					q.bind(bind.value() + ".phi", arg.getPhi());
					q.bind(bind.value() + ".gamma", arg.getGamma());
				}
			};
		}
	}

	String value();
}
