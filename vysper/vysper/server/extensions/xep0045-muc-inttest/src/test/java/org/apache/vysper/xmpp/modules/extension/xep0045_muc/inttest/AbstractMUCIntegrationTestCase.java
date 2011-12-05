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
package org.apache.vysper.xmpp.modules.extension.xep0045_muc.inttest;

import org.apache.vysper.xmpp.modules.extension.xep0045_muc.MUCModule;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Conference;
import org.apache.vysper.xmpp.server.XMPPServer;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 */
public class AbstractMUCIntegrationTestCase extends AbstractIntegrationTestCase {

    protected static final String NICK1 = "Nick";

    protected static final String NICK2 = "Nick2";

    protected static final String MUC_SUBDOMAIN = "chat";

    protected static final String ROOM_JID = "room@chat.vysper.org";

    protected Conference conference = new Conference("test conference");

    protected XMPPConnection client2;

    protected MultiUserChat chat;

    protected MultiUserChat chat2;

    @Override
    protected void addModules(XMPPServer server) {
        server.addModule(new MUCModule(MUC_SUBDOMAIN, conference));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        client2 = connectClient(port, TEST_USERNAME2, TEST_PASSWORD2);

        chat = new MultiUserChat(client, ROOM_JID);
        chat2 = new MultiUserChat(client2, ROOM_JID);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            client2.disconnect();
        } catch (Exception ignored) {
            ;
        }
        
        super.tearDown();
    }
    
    
}
