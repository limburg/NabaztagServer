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
package org.apache.vysper.xmpp.modules.core.session.handler;

import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

/**
 * handles session requests
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class SessionIQHandler extends DefaultIQHandler {

    @Override
    protected boolean verifyInnerElement(Stanza stanza) {
        return verifyInnerElementWorker(stanza, "session") && verifyInnerNamespace(stanza, NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SESSION);
    }

    @Override
    protected Stanza handleSet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
        return StanzaBuilder.createIQStanza(null, null, IQStanzaType.RESULT, stanza.getID()).addAttribute("from",
                sessionContext.getServerJID().getFullQualifiedName()).build();
    }
}