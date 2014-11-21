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
package eu.smartenit.sbox.interfaces.intersbox.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxObject;
import eu.smartenit.sbox.ntm.dtm.sender.DTMRemoteVectorsReceiver;

public class InterSBoxServerTest 
{
    private static final Logger logger = LoggerFactory.getLogger(InterSBoxServerTest.class);

    @Test
    public void serverTestWithClient()
    {
      int port=8068;
      logger.info("BEGIN TEST INTERSBOX SERVER WITH CLIENT");
      InterSBoxServer srv = null;
      CVector cVector = null;
      RVector rVector = null;
      ArgumentCaptor<CVector> arg1 = ArgumentCaptor.forClass(CVector.class);
      ArgumentCaptor<RVector> arg2 = ArgumentCaptor.forClass(RVector.class);

      DTMRemoteVectorsReceiver receiver = null;
      try
      {
        receiver = mock(DTMRemoteVectorsReceiver.class);
        srv = new InterSBoxServer(port, receiver);
        logger.info("Server launched");
        Thread.sleep(20);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.info("Server Exception " + e.getStackTrace() );
        fail(e.toString());
      }

      try
      {
        int v1 = 21;
        int v2 = 44;

        cVector = new CVector(null, v1);
        rVector = new RVector(null, v2, null);
        logger.info("Created vectors, creates client");
        InterSBoxClient cli = new InterSBoxClient();
        logger.info("will now call Send");
        cli.send("127.0.0.1", port, cVector, rVector);
        logger.info("will now wait for server");
        srv.join(500);
        verify(receiver).receive(arg1.capture(), arg2.capture());
        assertEquals (arg1.getValue().getSourceAsNumber() , v1);
        assertEquals (arg2.getValue().getSourceAsNumber() , v2);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error("Client Exception " + e.getStackTrace() );
        fail(e.toString());
      }
    }
 
    @Test
    public void serverTestLoop()
    {
      int port=8098;
      logger.info("BEGIN TEST INTERSBOX SERVER LOOP");
      InterSBoxServer srv = null;
      CVector cVector = null;
      RVector rVector = null;
      ArgumentCaptor<CVector> arg1 = ArgumentCaptor.forClass(CVector.class);
      ArgumentCaptor<RVector> arg2 = ArgumentCaptor.forClass(RVector.class);

      DTMRemoteVectorsReceiver receiver = null;
      try
      {
        receiver = mock(DTMRemoteVectorsReceiver.class);
        srv = new InterSBoxServer(port, receiver);
        logger.info("Server launched");
        Thread.sleep(20);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.info("Server Exception " + e.getStackTrace() );
        fail(e.toString());
      }

      try
      {
        int v1 = 21;
        int v2 = 44;

        int iter=10;
        for (;iter>0; iter--)
        {
          reset(receiver);
          logger.info("Client Loop " + iter);
          v1++; v2+=3;
          cVector = new CVector(null, v1);
          rVector = new RVector(null, v2, null);
          InterSBoxClient cli = new InterSBoxClient();
          logger.info("Client created");
          cli.send("127.0.0.1", port, cVector,rVector);
          logger.info("Client send");
          Thread.sleep(100);
          verify(receiver).receive(arg1.capture(), arg2.capture());
          assertEquals (arg1.getValue().getSourceAsNumber() , v1);
          assertEquals (arg2.getValue().getSourceAsNumber() , v2);

        }
        srv.join(500);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error("Client Exception " + e.getStackTrace() );
        fail(e.toString());
      }
    }

    @Test
    public void serverTestAlone()
    {
      int port=8097;
      logger.info("BEGIN TEST INTERSBOX SERVER ALONE");
      InterSBoxServer srv = null;
      CVector cVector = null;
      RVector rVector = null;
      ArgumentCaptor<CVector> arg1 = ArgumentCaptor.forClass(CVector.class);
      ArgumentCaptor<RVector> arg2 = ArgumentCaptor.forClass(RVector.class);

      DTMRemoteVectorsReceiver receiver = null;
      try
      {
        receiver = mock(DTMRemoteVectorsReceiver.class);
        srv = new InterSBoxServer(port, receiver);
        logger.info("Server launched");
        Thread.sleep(20);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.info("Server Exception " + e.getStackTrace() );
        fail(e.toString());
      }

      try
      {
        int v1 = 78;
        int v2 = 21;

        cVector = new CVector(null, v1);
        rVector = new RVector(null, v2, null);
        InterSBoxObject obj = new InterSBoxObject();                                                                                                                             
        obj.cVector = cVector;                                                                                                                                   
        obj.rVector = rVector;                                                                                                                                   
        obj.rvPresent = true;            
        srv.received(obj);
        srv.join(500);
        verify(receiver).receive(arg1.capture(), arg2.capture());
        assertEquals (arg1.getValue().getSourceAsNumber() , v1);
        assertEquals (arg2.getValue().getSourceAsNumber() , v2);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error("Client Exception " + e.getStackTrace() );
        fail(e.toString());
      }
    }
}
