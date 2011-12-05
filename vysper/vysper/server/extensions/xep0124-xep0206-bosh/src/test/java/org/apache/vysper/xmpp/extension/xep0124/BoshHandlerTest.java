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
package org.apache.vysper.xmpp.extension.xep0124;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authentication.SASLMechanism;
import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationModule;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaProcessor;
import org.apache.vysper.xmpp.server.ServerFeatures;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class BoshHandlerTest {

    private IMocksControl mocksControl;

    private ServerRuntimeContext serverRuntimeContext;

    private BoshHandler boshHandler;

    @Before
    public void setUp() throws Exception {
        mocksControl = createControl();
        serverRuntimeContext = mocksControl.createMock(ServerRuntimeContext.class);
        boshHandler = new BoshHandler();
        boshHandler.setServerRuntimeContext(serverRuntimeContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testProcess() throws IOException, SAXException {
        // test session creation
        HttpServletRequest httpServletRequest = mocksControl.createMock(HttpServletRequest.class);
        expect(serverRuntimeContext.getNextSessionId()).andReturn("200");
        expect(serverRuntimeContext.getServerEnitity()).andReturn(new EntityImpl(null, "vysper.org", null));
        expect(serverRuntimeContext.getDefaultXMLLang()).andReturn("en");
        Continuation continuation = mocksControl.createMock(Continuation.class);
        expect(httpServletRequest.getAttribute(Continuation.ATTRIBUTE)).andReturn(continuation);
        expectLastCall().atLeastOnce();
        continuation.setTimeout(anyLong());
        Capture<BoshRequest> br = new Capture<BoshRequest>();
        continuation.setAttribute(eq("request"), EasyMock.<BoshRequest> capture(br));
        continuation.addContinuationListener(EasyMock.<ContinuationListener> anyObject());
        continuation.suspend();

        ServerFeatures serverFeatures = mocksControl.createMock(ServerFeatures.class);
        expect(serverRuntimeContext.getServerFeatures()).andReturn(serverFeatures);
        expect(serverFeatures.getAuthenticationMethods()).andReturn(Collections.<SASLMechanism> emptyList());
        expect(serverRuntimeContext.getModule(InBandRegistrationModule.class)).andReturn(null);

        Capture<BoshResponse> captured = new Capture<BoshResponse>();
        continuation.setAttribute(eq("response"), EasyMock.<BoshResponse> capture(captured));
        continuation.resume();
        mocksControl.replay();

        Stanza boshRequest = createSessionRequest();
        boshHandler.process(httpServletRequest, boshRequest);
        mocksControl.verify();
        
        assertEquals(httpServletRequest, br.getValue().getHttpServletRequest());
        assertEquals(boshRequest, br.getValue().getBody());

        Stanza response = new XMLUtil(new String(captured.getValue().getContent())).parse();
        assertNotNull(response);
        assertEquals("body", response.getName());
        assertEquals(NamespaceURIs.XEP0124_BOSH, response.getNamespaceURI());
        assertEquals("200", response.getAttributeValue("sid"));
        assertEquals("vysper.org", response.getAttributeValue("from"));
        assertEquals("60", response.getAttributeValue("wait"));
        assertEquals("1", response.getAttributeValue("hold"));
        assertEquals("1.9", response.getAttributeValue("ver"));
        assertEquals(1, response.getInnerElements().size());
        XMLElement streamFeatures = response.getInnerElements().get(0);
        assertEquals("features", streamFeatures.getName());
        assertEquals(NamespaceURIs.HTTP_ETHERX_JABBER_ORG_STREAMS, streamFeatures.getNamespaceURI());
        assertEquals(1, streamFeatures.getInnerElements().size());
        XMLElement saslMechanisms = streamFeatures.getInnerElements().get(0);
        assertEquals("mechanisms", saslMechanisms.getName());
        assertEquals(NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL, saslMechanisms.getNamespaceURI());

        // test session retrieval, retrieves the session above identified by sid=200
        mocksControl.reset();
        expect(httpServletRequest.getAttribute(Continuation.ATTRIBUTE)).andReturn(continuation);
        expectLastCall().atLeastOnce();
        continuation.setTimeout(anyLong());
        continuation.suspend();
        continuation.setAttribute(eq("request"), EasyMock.<BoshRequest> capture(br));
        continuation.addContinuationListener(EasyMock.<ContinuationListener> anyObject());
        StanzaProcessor stanzaProcessor = mocksControl.createMock(StanzaProcessor.class);
        expect(serverRuntimeContext.getStanzaProcessor()).andReturn(stanzaProcessor);
        Capture<Stanza> stanzaCaptured = new Capture<Stanza>();
        stanzaProcessor.processStanza(eq(serverRuntimeContext), EasyMock.<SessionContext> anyObject(),
                EasyMock.<Stanza> capture(stanzaCaptured), EasyMock.<SessionStateHolder> anyObject());
        mocksControl.replay();
        boshRequest = createSaslRequest();
        boshHandler.process(httpServletRequest, boshRequest);
        mocksControl.verify();
        
        assertEquals(httpServletRequest, br.getValue().getHttpServletRequest());
        assertEquals(boshRequest, br.getValue().getBody());
        
        Stanza stanza = stanzaCaptured.getValue();
        assertNotNull(stanza);
        assertEquals("auth", stanza.getName());
        assertEquals(NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL, stanza.getNamespaceURI());
        assertEquals("DIGEST-MD5", stanza.getAttributeValue("mechanism"));
    }

    @Test
    public void testGetEmptyResponse() {
        Stanza response = boshHandler.getEmptyResponse();
        assertNotNull(response);
        assertEquals("body", response.getName());
        assertEquals(NamespaceURIs.XEP0124_BOSH, response.getNamespaceURI());
        assertEquals(0, response.getAttributes().size());
        assertEquals(0, response.getInnerElements().size());
    }

    @Test
    public void testWrapStanza() {
        StanzaBuilder stanzaBuilder = new StanzaBuilder("iq", NamespaceURIs.JABBER_CLIENT);
        Stanza stanza = stanzaBuilder.build();
        Stanza body = boshHandler.wrapStanza(stanza);
        assertNotNull(body);
        assertEquals("body", body.getName());
        assertEquals(NamespaceURIs.XEP0124_BOSH, body.getNamespaceURI());
        assertEquals(1, body.getInnerElements().size());
        assertEquals("iq", body.getInnerElements().get(0).getName());
        assertEquals(NamespaceURIs.JABBER_CLIENT, body.getInnerElements().get(0).getNamespaceURI());
    }

    @Test
    public void testMergeResponses() {
        Stanza response1 = createPingStanzaResponse("vysper.org", "user1@vysper.org/resource", "100");
        Stanza response2 = createPingStanzaResponse("vysper.org", "user1@vysper.org/resource", "101");
        assertEquals(response1, boshHandler.mergeResponses(response1, null));
        assertEquals(response1, boshHandler.mergeResponses(null, response1));
        Stanza merged = boshHandler.mergeResponses(response1, response2);
        assertNotNull(merged);
        assertEquals("body", merged.getName());
        assertEquals(NamespaceURIs.XEP0124_BOSH, merged.getNamespaceURI());
        assertEquals(2, merged.getInnerElements().size());
        assertEquals(response1.getInnerElements().get(0), merged.getInnerElements().get(0));
        assertEquals(response2.getInnerElements().get(0), merged.getInnerElements().get(1));
    }

    private Stanza createSessionRequest() {
        StanzaBuilder body = new StanzaBuilder("body", NamespaceURIs.XEP0124_BOSH);
        body.addAttribute("rid", "100");
        body.addAttribute("to", "vysper.org");
        body.addAttribute(NamespaceURIs.XML, "lang", "en");
        body.addAttribute("wait", "60");
        body.addAttribute("hold", "1");
        body.addAttribute("ver", "1.9");
        body.addAttribute(NamespaceURIs.URN_XMPP_XBOSH, "version", "1.0");
        return body.build();
    }

    private Stanza createSaslRequest() {
        StanzaBuilder body = new StanzaBuilder("body", NamespaceURIs.XEP0124_BOSH);
        body.addAttribute("rid", "101");
        body.addAttribute("sid", "200");
        body.startInnerElement("auth", NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL)
                .addAttribute("mechanism", "DIGEST-MD5").endInnerElement();
        return body.build();
    }

    private static Stanza createPingStanzaResponse(String from, String to, String id) {
        StanzaBuilder body = new StanzaBuilder("body", NamespaceURIs.XEP0124_BOSH);
        body.startInnerElement("iq", NamespaceURIs.JABBER_CLIENT).addAttribute("from", from)
                .addAttribute("type", "result").addAttribute("to", to).addAttribute("id", id);
        body.endInnerElement();
        return body.build();
    }

}