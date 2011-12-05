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
package org.apache.vysper.xmpp.server.s2s;
import java.io.File;

import org.apache.vysper.mina.C2SEndpoint;
import org.apache.vysper.mina.S2SEndpoint;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authentication.AccountManagement;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.XMPPServer;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;


public class Server2Server {

    public static void main(String[] args) throws Exception {
        Entity localServer = EntityImpl.parseUnchecked(args[0]);
        Entity localUser = EntityImpl.parseUnchecked(args[1]);
        Entity remoteServer = EntityImpl.parseUnchecked(args[2]);
        Entity remoteUser = EntityImpl.parseUnchecked(args[3]);
        String remotePassword = args[4];

        String keystorePath;
        String keystorePassword;
        if(args.length > 5) {
            keystorePath = args[5];
            keystorePassword = args[6];
        } else {
            keystorePath = "src/main/config/bogus_mina_tls.cert";
            keystorePassword = "boguspw";            
        }
        
        XMPPServer server = new XMPPServer(localServer.getDomain());

        StorageProviderRegistry providerRegistry = new MemoryStorageProviderRegistry();
        final AccountManagement accountManagement = (AccountManagement) providerRegistry
        .retrieve(AccountManagement.class);

        if (!accountManagement.verifyAccountExists(localUser)) {
            accountManagement.addUser(localUser, "password1");
        }

        // S2S endpoint
        server.addEndpoint(new S2SEndpoint());
        
        // C2S endpoint
        server.addEndpoint(new C2SEndpoint());
        
        server.setStorageProviderRegistry(providerRegistry);
        server.setTLSCertificateInfo(new File(keystorePath), keystorePassword);
        
        server.start();
        
        // enable server connection to use ping
        //server.addModule(new XmppPingModule());

        ServerRuntimeContext serverRuntimeContext = server.getServerRuntimeContext();
        
        Thread.sleep(2000);

        XMPPServerConnectorRegistry registry = serverRuntimeContext.getServerConnectorRegistry();
        
        XMPPServerConnector connector = registry.connect(remoteServer);
        
        Stanza stanza = new StanzaBuilder("message", NamespaceURIs.JABBER_SERVER)
            .addAttribute("from", localUser.getFullQualifiedName())
            .addAttribute("to", remoteUser.getFullQualifiedName())
            .startInnerElement("body", NamespaceURIs.JABBER_SERVER)
            .addText("Hello world")
            .endInnerElement()
            .build();
            
        connector.write(stanza);
        
        //sendMessagesUsingClients(localUser, remoteServer, remoteUser, remotePassword, keystorePath, keystorePassword);
        
        Thread.sleep(50000);
        
        server.stop();
    }

    private static void sendMessagesUsingClients(Entity localUser, Entity remoteServer, Entity remoteUser,
            String remotePassword, String keystorePath, String keystorePassword) throws XMPPException,
            InterruptedException {
        ConnectionConfiguration localConnectionConfiguration = new ConnectionConfiguration("localhost", 5222);
        localConnectionConfiguration.setKeystorePath(keystorePath);
        localConnectionConfiguration.setTruststorePath(keystorePath);
        localConnectionConfiguration.setTruststorePassword(keystorePassword);
        XMPPConnection localClient = new XMPPConnection(localConnectionConfiguration);

        localClient.connect();
        localClient.login(localUser.getNode(), "password1");
        localClient.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                System.out.println("# " + packet);
            }
        }, new PacketFilter() {
            public boolean accept(Packet arg0) {
                return true;
            }
        });
        
        
        ConnectionConfiguration remoteConnectionConfiguration = new ConnectionConfiguration(remoteServer.getFullQualifiedName(), 5222);
        remoteConnectionConfiguration.setKeystorePath(keystorePath);
        remoteConnectionConfiguration.setTruststorePath(keystorePath);
        remoteConnectionConfiguration.setTruststorePassword(keystorePassword);
        XMPPConnection remoteClient = new XMPPConnection(remoteConnectionConfiguration);

        remoteClient.connect();
        remoteClient.login(remoteUser.getNode(), remotePassword);
        
        Thread.sleep(3000);
        
        Message msg = new Message(remoteUser.getFullQualifiedName());
//        Message msg = new Message(localUser.getFullQualifiedName());
        msg.setBody("Hello world");
        
        localClient.sendPacket(msg);
//        remoteClient.sendPacket(msg);
        
        
        Thread.sleep(8000);
        remoteClient.disconnect();
        localClient.disconnect();
    }
    
}
