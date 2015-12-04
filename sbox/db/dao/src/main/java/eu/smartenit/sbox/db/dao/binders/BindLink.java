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

import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * The BindLink annotation. It maps the parameters of a sql statement in AbstractLinkDAO 
 * into Link parameters.
 *
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
@BindingAnnotation(BindLink.LinkBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindLink {

	public static class LinkBinderFactory implements BinderFactory {

		public Binder build(Annotation annotation) {
			return new Binder<BindLink, Link>() {
				public void bind(SQLStatement q, BindLink bind, Link arg) {
					q.bind("localLinkID", ((SimpleLinkID) arg.getLinkID()).getLocalLinkID());
					q.bind("localIspName", ((SimpleLinkID) arg.getLinkID()).getLocalIspName());
					q.bind("physicalInterfaceName",
							arg.getPhysicalInterfaceName());
					q.bind("addressPrefix", arg.getAddress().getPrefix());
					q.bind("addressPrefixLength", arg.getAddress()
							.getPrefixLength());
					q.bind("vlan", arg.getVlan());
					q.bind("inboundInterfaceCounterOID", arg.getInboundInterfaceCounterOID());
					q.bind("outboundInterfaceCounterOID", arg.getOutboundInterfaceCounterOID());	
					q.bind("bgRouterAddress", arg.getBgRouter().getManagementAddress().getPrefix());
					q.bind("tunnelEndPrefix", arg.getTunnelEndPrefix().getPrefix());
					q.bind("tunnelEndPrefixLength", arg.getTunnelEndPrefix().getPrefixLength());
					q.bind("policerBandwidthLimitFactor", arg.getPolicerBandwidthLimitFactor());
					q.bind("filterInterfaceName", arg.getFilterInterfaceName());
					q.bind("aggregateLeakageFactor", arg.getAggregateLeakageFactor());
				}
			};
		}
	}
}
