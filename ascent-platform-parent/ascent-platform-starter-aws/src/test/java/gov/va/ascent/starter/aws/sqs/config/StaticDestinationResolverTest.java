/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author rajuthota
 */
public class StaticDestinationResolverTest {

    /**
     * Test of resolveDestinationName method, of class StaticDestinationResolver.
     */
    @Test
    public void testResolveDestinationName() throws Exception {
        String destinationName = "queueName";
        Session session = mock(Session.class);
        Queue queue = mock(Queue.class);
        when(session.createQueue(eq(destinationName))).thenReturn(queue);

        boolean pubSubDomain = false;
        StaticDestinationResolver instance = new StaticDestinationResolver(destinationName);
        Destination result = instance.resolveDestinationName(session, destinationName, pubSubDomain);
        assertNotNull(result);
    }
    
}
