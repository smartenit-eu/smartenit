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

import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.*;

/**
 * The BindEndAddressPairTunnelID annotation. It maps the parameters of a sql statement in AbstractTunnelDAO
 * into EndAddressPairTunnelID parameters.
 *
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
@BindingAnnotation(BindEndAddressPairTunnelID.EndAddressPairTunnelIDBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindEndAddressPairTunnelID {

	public static class EndAddressPairTunnelIDBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindEndAddressPairTunnelID, EndAddressPairTunnelID>() {
				public void bind(SQLStatement q, BindEndAddressPairTunnelID bind, EndAddressPairTunnelID arg) {
					q.bind(bind.value() + ".tunnelName", arg.getTunnelName());
                    q.bind(bind.value() + ".localTunnelEndAddress", arg.getLocalTunnelEndAddress()
                            .getPrefix());
                    q.bind(bind.value() + ".remoteTunnelEndAddress", arg.getRemoteTunnelEndAddress()
                            .getPrefix());
				}
			};
		}
	}

	String value();
}
