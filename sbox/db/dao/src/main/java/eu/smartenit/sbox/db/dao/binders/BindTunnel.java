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

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;

/**
 * The BindTunnel annotation. It maps the parameters of a sql statement in AbstractTunnelDAO 
 * into Tunnel parameters.
 *
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
@BindingAnnotation(BindTunnel.TunnelBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindTunnel {

	public static class TunnelBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindTunnel, Tunnel>() {
				public void bind(SQLStatement q, BindTunnel bind, Tunnel arg) {
					q.bind(bind.value() + ".tunnelName", arg.getTunnelID().getTunnelName());
                    q.bind(bind.value() + ".localTunnelEndAddress", arg.getTunnelID().getLocalTunnelEndAddress()
                            .getPrefix());
                    q.bind(bind.value() + ".remoteTunnelEndAddress", arg.getTunnelID().getRemoteTunnelEndAddress()
                            .getPrefix());
					q.bind(bind.value() + ".physicalLocalInterfaceName", arg.getPhysicalLocalInterfaceName());
					q.bind(bind.value() + ".inboundInterfaceCounterOID", arg.getInboundInterfaceCounterOID());
					q.bind(bind.value() + ".outboundInterfaceCounterOID", arg.getOutboundInterfaceCounterOID());
                    q.bind(bind.value() + ".ofSwitchPortNumber", arg.getOfSwitchPortNumber());
					q.bind(bind.value() + ".localLinkID", ((SimpleLinkID)arg.getLink().getLinkID()).getLocalLinkID());
					q.bind(bind.value() + ".localIspName", ((SimpleLinkID)arg.getLink().getLinkID()).getLocalIspName());
					q.bind(bind.value() + ".localRouterAddress", arg.getLocalRouterAddress().getPrefix());
				}
			};
		}
	}

	String value();
}
