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

import eu.smartenit.sbox.db.dto.DC2DCCommunication;

/**
 * The BindDC2DCCommunication annotation. It maps the parameters of a sql statement 
 * in AbstractDC2DCCommunicationDAO into DC2DCCommunication parameters.
 *
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
@BindingAnnotation(BindDC2DCCommunication.DC2DCCommunicationBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindDC2DCCommunication {

	public static class DC2DCCommunicationBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindDC2DCCommunication, DC2DCCommunication>() {
				public void bind(SQLStatement q, BindDC2DCCommunication bind, DC2DCCommunication arg) {
					q.bind(bind.value() + ".communicationNumber", arg.getId().getCommunicationNumber());
					q.bind(bind.value() + ".communicationSymbol", arg.getId().getCommunicationSymbol());
					q.bind(bind.value() + ".localCloudDCName", arg.getId().getLocalCloudDCName());
					q.bind(bind.value() + ".localAsNumber", arg.getId().getLocalAsNumber());
					q.bind(bind.value() + ".remoteCloudDCName", arg.getId().getRemoteCloudDCName());
					q.bind(bind.value() + ".remoteAsNumber", arg.getId().getRemoteAsNumber());
					q.bind(bind.value() + ".remoteAsNumber", arg.getId().getRemoteAsNumber());
					q.bind(bind.value() + ".trafficDirection", arg.getTrafficDirection());
				}
			};
		}
	}

	String value();
}
