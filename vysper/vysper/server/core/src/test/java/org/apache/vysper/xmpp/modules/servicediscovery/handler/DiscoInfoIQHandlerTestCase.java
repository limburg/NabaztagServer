/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.vysper.xmpp.modules.servicediscovery.handler;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.vysper.StanzaAssert;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.delivery.StanzaRelay;
import org.apache.vysper.xmpp.delivery.failure.DeliveryFailureStrategy;
import org.apache.vysper.xmpp.modules.servicediscovery.collection.ServiceCollector;
import org.apache.vysper.xmpp.modules.servicediscovery.collection.ServiceDiscoveryRequestListenerRegistry;
import org.apache.vysper.xmpp.modules.servicediscovery.management.Feature;
import org.apache.vysper.xmpp.modules.servicediscovery.management.Identity;
import org.apache.vysper.xmpp.modules.servicediscovery.management.InfoElement;
import org.apache.vysper.xmpp.modules.servicediscovery.management.InfoRequest;
import org.apache.vysper.xmpp.modules.servicediscovery.management.ServiceDiscoveryRequestException;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.components.ComponentStanzaProcessor;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.apache.vysper.xmpp.stanza.StanzaErrorCondition;
import org.apache.vysper.xmpp.stanza.XMPPCoreStanza;
import org.apache.vysper.xmpp.writer.StanzaWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 */
public class DiscoInfoIQHandlerTestCase extends Mockito {

    private static final Entity FROM = EntityImpl.parseUnchecked("from@vysper.org");
    private static final Entity SERVER = EntityImpl.parseUnchecked("vysper.org");
    private static final Entity COMPONENT = EntityImpl.parseUnchecked("comp.vysper.org");
    private static final Entity USER = EntityImpl.parseUnchecked("user@vysper.org");
    private static final Entity USER_WITH_RESOURCE = EntityImpl.parseUnchecked("user@vysper.org/res1");
    
    private ServerRuntimeContext serverRuntimeContext = mock(ServerRuntimeContext.class);
    private SessionContext sessionContext = mock(SessionContext.class);
    private ServiceCollector serviceCollector = mock(ServiceCollector.class);
    private StanzaRelay stanzaRelay = mock(StanzaRelay.class);
    private StanzaWriter stanzaWriter = mock(StanzaWriter.class);

    private IQStanza stanza = (IQStanza) IQStanza.getWrapper(buildStanza());
    
    private DiscoInfoIQHandler handler = new DiscoInfoIQHandler();
    
    private ComponentStanzaProcessor componentStanzaProcessor = mock(ComponentStanzaProcessor.class);
    private Feature feature = new Feature("foo");
    private Identity identity = new Identity("bar", "fez");
    private List<InfoElement> infoElements = Arrays.asList(feature, identity);
    
    private Stanza buildStanza() {
        return buildStanza("iq", NamespaceURIs.JABBER_CLIENT, "query", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO);
    }

    private Stanza buildStanza(String name, String namespaceUri) {
        return buildStanza(name, namespaceUri, "query", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO);
    }
    
    private Stanza buildStanza(String name, String namespaceUri, String innerName, String innerNamespaceUri) {
        return new StanzaBuilder(name, namespaceUri)
            .addAttribute("type", "get")
            .addAttribute("id", "1")
            .startInnerElement(innerName, innerNamespaceUri)
            .build();
    }
    
    @Test
    public void nameMustBeIq() {
        Assert.assertEquals("iq", handler.getName());
    }

    @Test
    public void verifyNullStanza() {
        Assert.assertFalse(handler.verify(null));
    }

    @Test
    public void verifyInvalidName() {
        Assert.assertFalse(handler.verify(buildStanza("dummy", NamespaceURIs.JABBER_CLIENT)));
    }

    @Test
    public void verifyInvalidNamespace() {
        Assert.assertFalse(handler.verify(buildStanza("iq", "dummy")));
    }

    @Test
    public void verifyNullNamespace() {
        Assert.assertFalse(handler.verify(buildStanza("iq", null)));
    }

    @Test
    public void verifyNullInnerNamespace() {
        Assert.assertFalse(handler.verify(buildStanza("iq", NamespaceURIs.JABBER_CLIENT, "query", null)));
    }

    @Test
    public void verifyInvalidInnerNamespace() {
        Assert.assertFalse(handler.verify(buildStanza("iq", NamespaceURIs.JABBER_CLIENT, "query", "dummy")));
    }
    
    @Test
    public void verifyInvalidInnerName() {
        Assert.assertFalse(handler.verify(buildStanza("iq", NamespaceURIs.JABBER_CLIENT, "dummy", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)));
    }

    @Test
    public void verifyMissingInnerElement() {
        Stanza stanza = new StanzaBuilder("iq", NamespaceURIs.JABBER_CLIENT).build();
        Assert.assertFalse(handler.verify(stanza));
    }
    
    @Test
    public void verifyValidStanza() {
        Assert.assertTrue(handler.verify(stanza));
    }

    @Test
    public void sessionIsRequired() {
        Assert.assertTrue(handler.isSessionRequired());
    }
    
    @Before
    public void before() {
        when(serverRuntimeContext.getServerRuntimeContextService(ServiceDiscoveryRequestListenerRegistry.SERVICE_DISCOVERY_REQUEST_LISTENER_REGISTRY))
            .thenReturn(serviceCollector);
        
        when(serverRuntimeContext.getServerEnitity()).thenReturn(SERVER);
        when(serverRuntimeContext.getStanzaRelay()).thenReturn(stanzaRelay);
        when(sessionContext.getResponseWriter()).thenReturn(stanzaWriter);
    }

    @Test
    public void handleGetToServer() throws Exception {
        IQStanza stanza = createRequest(SERVER);
        
        when(serviceCollector.processServerInfoRequest(any(InfoRequest.class))).thenReturn(infoElements);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        StanzaAssert.assertEquals(createExpectedResponse(SERVER), response);
    }

    @Test
    public void handleGetToExistingComponent() throws Exception {
        IQStanza stanza = createRequest(COMPONENT);
        
        when(serverRuntimeContext.getComponentStanzaProcessor(COMPONENT)).thenReturn(componentStanzaProcessor);
        
        when(serviceCollector.processComponentInfoRequest(any(InfoRequest.class))).thenReturn(infoElements);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        StanzaAssert.assertEquals(createExpectedResponse(COMPONENT), response);
    }

    @Test
    public void handleGetToUser() throws Exception {
        IQStanza stanza = createRequest(USER);
        
        when(serviceCollector.processInfoRequest(any(InfoRequest.class))).thenReturn(infoElements);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        StanzaAssert.assertEquals(createExpectedResponse(USER), response);
    }

    @Test
    public void handleGetToUserWithResource() throws Exception {
        when(sessionContext.getInitiatingEntity()).thenReturn(FROM);
        
        IQStanza stanza = createRequest(USER_WITH_RESOURCE);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Assert.assertNull(response);
        
        verify(stanzaRelay).relay(eq(USER_WITH_RESOURCE), eq(stanza), any(DeliveryFailureStrategy.class));
    }
    
    @Test
    public void handleGetToUserWithResourceInbound() throws Exception {
        when(sessionContext.getInitiatingEntity()).thenReturn(USER);
        
        IQStanza stanza = createRequest(USER_WITH_RESOURCE);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Assert.assertNull(response);
        
        verify(stanzaWriter).write(stanza);
    }
    
    @Test
    public void handleGetToNonExistingComponent() throws Exception {
        IQStanza stanza = createRequest(COMPONENT);
        
        when(serverRuntimeContext.getComponentStanzaProcessor(COMPONENT)).thenReturn(null);
        when(serviceCollector.processComponentInfoRequest(any(InfoRequest.class))).thenReturn(infoElements);
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Stanza expected = createErrorResponse(COMPONENT, "item-not-found");

        StanzaAssert.assertEquals(expected, response);
    }

    @Test
    public void handleGetNoServiceCollector() throws Exception {
        IQStanza stanza = createRequest(SERVER);
        
        when(serverRuntimeContext.getServerRuntimeContextService(ServiceDiscoveryRequestListenerRegistry.SERVICE_DISCOVERY_REQUEST_LISTENER_REGISTRY))
            .thenReturn(null);
            
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Stanza expected = createErrorResponse(SERVER, "internal-server-error");
        
        StanzaAssert.assertEquals(expected, response);
    }
    
    @Test
    public void handleGetToServerWithException() throws Exception {
        IQStanza stanza = createRequest(SERVER);
        
        when(serviceCollector.processServerInfoRequest(any(InfoRequest.class))).thenThrow(new ServiceDiscoveryRequestException(StanzaErrorCondition.INTERNAL_SERVER_ERROR));
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Stanza expected = createErrorResponse(SERVER, "internal-server-error");
        
        StanzaAssert.assertEquals(expected, response);
    }
    
    @Test
    public void handleGetToComponentWithException() throws Exception {
        IQStanza stanza = createRequest(COMPONENT);
        
        when(serverRuntimeContext.getComponentStanzaProcessor(COMPONENT)).thenReturn(componentStanzaProcessor);
        when(serviceCollector.processComponentInfoRequest(any(InfoRequest.class))).thenThrow(new ServiceDiscoveryRequestException(StanzaErrorCondition.INTERNAL_SERVER_ERROR));
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Stanza expected = createErrorResponse(COMPONENT, "internal-server-error");
        
        StanzaAssert.assertEquals(expected, response);
    }
    
    @Test
    public void handleGetToUserWithException() throws Exception {
        IQStanza stanza = createRequest(USER);
        
        when(serviceCollector.processInfoRequest(any(InfoRequest.class))).thenThrow(new ServiceDiscoveryRequestException(StanzaErrorCondition.INTERNAL_SERVER_ERROR));
        
        Stanza response = handler.handleGet(stanza, serverRuntimeContext, sessionContext);
        
        Stanza expected = createErrorResponse(SERVER, "internal-server-error");
        
        StanzaAssert.assertEquals(expected, response);
    }
    
    @Test
    public void handleResultToUser() throws Exception {
        when(sessionContext.getInitiatingEntity()).thenReturn(FROM);
        
        IQStanza stanza = createRequest(USER, IQStanzaType.RESULT);
        
        Stanza response = handler.handleResult(stanza, serverRuntimeContext, sessionContext);
        
        Assert.assertNull(response);
        
        verify(stanzaRelay).relay(eq(USER), eq(stanza), any(DeliveryFailureStrategy.class));
    }
    
    @Test
    public void handleResultToUserInbound() throws Exception {
        when(sessionContext.getInitiatingEntity()).thenReturn(USER);
        
        IQStanza stanza = createRequest(USER, IQStanzaType.RESULT);
        
        Stanza response = handler.handleResult(stanza, serverRuntimeContext, sessionContext);
        
        Assert.assertNull(response);
        
        verify(stanzaWriter).write(stanza);
    }
    
    private Stanza createErrorResponse(Entity from, String error) {
        Stanza expected = StanzaBuilder.createIQStanza(from, FROM, IQStanzaType.ERROR, "id1")
        .startInnerElement("query", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)
        .addAttribute("node", "n")
        .endInnerElement()
        .startInnerElement("error", NamespaceURIs.JABBER_CLIENT)
        .addAttribute("type", "cancel")
        .startInnerElement(error, NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_STANZAS)
        .build();
        return expected;
    }
    
    private IQStanza createRequest(Entity to) {
        return createRequest(to, IQStanzaType.GET);
    }
    
    private IQStanza createRequest(Entity to, IQStanzaType type) {
        IQStanza stanza = (IQStanza) XMPPCoreStanza.getWrapper(StanzaBuilder.createIQStanza(FROM, to, type, "id1")
                .startInnerElement("query", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)
                .addAttribute("node", "n")
                .build());
        return stanza;
    }

    private Stanza createExpectedResponse(Entity from) {
        Stanza expected = StanzaBuilder.createIQStanza(from, FROM, IQStanzaType.RESULT, "id1")
        .startInnerElement("query", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)
        .addAttribute("node", "n")
        .startInnerElement("feature", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)
        .addAttribute("var", "foo")
        .endInnerElement()
        .startInnerElement("identity", NamespaceURIs.XEP0030_SERVICE_DISCOVERY_INFO)
        .addAttribute("category", "bar")
        .addAttribute("type", "fez")
        .endInnerElement()
        .build();
        return expected;
    }
}
