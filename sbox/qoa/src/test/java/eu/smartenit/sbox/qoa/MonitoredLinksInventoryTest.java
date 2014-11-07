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
package eu.smartenit.sbox.qoa;

import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;

/**
 * Includes test methods for population and data retrieval methods of
 * {@link MonitoredLinksInventory} class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class MonitoredLinksInventoryTest {
	
	private AS as1;
	private AS as2;
	
	private BGRouter bgRouter11;
	private BGRouter bgRouter12;
	private BGRouter bgRouter21;
	
	private Link link111;
	private Link link112;
	private Link link121;
	private Link link122;
	private Link link211;
	
	@Before
	public void setup() {
		bgRouter11 = new BGRouter();
		bgRouter12 = new BGRouter();
		bgRouter21 = new BGRouter();
		
		link111 = new Link(); link111.setBgRouter(bgRouter11);
		link112 = new Link(); link112.setBgRouter(bgRouter11);
		link121 = new Link(); link121.setBgRouter(bgRouter12);
		link122 = new Link(); link122.setBgRouter(bgRouter12);
		link211 = new Link(); link211.setBgRouter(bgRouter21);
		
		bgRouter11.setInterDomainLinks(Arrays.asList(link111, link112));
		bgRouter12.setInterDomainLinks(Arrays.asList(link121, link122));
		bgRouter21.setInterDomainLinks(Arrays.asList(link211));
		
		as1 = new AS(); as1.setAsNumber(1); as1.setBgRouters(Arrays.asList(bgRouter11, bgRouter12));
		as2 = new AS(); as2.setAsNumber(2); as2.setBgRouters(Arrays.asList(bgRouter21));
	}
	
	@Test
	public void shouldPopulateInventory() {
		MonitoredLinksInventory inventory = new MonitoredLinksInventory();
		inventory.populate(Arrays.asList(as1, as2));
		
		assertEquals(2, inventory.getAllAsNumbers().size());
		assertEquals(1, inventory.getAsNumber(bgRouter12));
		assertEquals(4, inventory.getLinks(1).size());
		assertEquals(1, inventory.getLinks(2).size());
		assertEquals(2, inventory.getLinks(bgRouter11).size());
		assertEquals(3, inventory.getBGRouters().size());
	}

}
