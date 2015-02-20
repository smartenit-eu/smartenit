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

import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The BindUNaDaConfiguration annotation. It maps the parameters of a sql
 * statement in AbstractUNaDaConfigurationDAO into UNaDaConfiguration
 * parameters.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
@BindingAnnotation(BindUNaDaConfiguration.UNaDaConfigurationBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindUNaDaConfiguration {

	public static class UNaDaConfigurationBinderFactory implements
			BinderFactory {

		public Binder<BindUNaDaConfiguration, UNaDaConfiguration> build(
				Annotation annotation) {
			return new Binder<BindUNaDaConfiguration, UNaDaConfiguration>() {
				public void bind(SQLStatement<?> q,
						BindUNaDaConfiguration bind, UNaDaConfiguration arg) {
					q.bind(bind.value() + ".ipaddress", arg.getIpAddress());
					q.bind(bind.value() + ".macaddress", arg.getMacAddress());
					q.bind(bind.value() + ".port", arg.getPort());
					q.bind(bind.value() + ".latitude", arg.getLocation()
							.getLatitude());
					q.bind(bind.value() + ".longtitude", arg.getLocation()
							.getLongitude());
					q.bind(bind.value() + ".openssid", arg.getOpenWifi()
							.getSSID());
					q.bind(bind.value() + ".privatessid", arg.getPrivateWifi()
							.getSSID());
					q.bind(bind.value() + ".privatessidpassword", arg
							.getPrivateWifi().getPassword());
					q.bind(bind.value() + ".socialinterval",
							arg.getSocialInterval());
					q.bind(bind.value() + ".overlayinterval",
							arg.getOverlayInterval());
					q.bind(bind.value() + ".predictioninterval",
							arg.getPredictionInterval());
					q.bind(bind.value() + ".bootstrap",
							arg.isBootstrapNode());
                    q.bind(bind.value() + ".social",
                            arg.isSocialPredictionEnabled());
                    q.bind(bind.value() + ".overlay",
                            arg.isOverlayPredictionEnabled());
                    q.bind(bind.value() + ".chunksize",
                            arg.getChunkSize());
				}
			};
		}
	}

	String value();
}
