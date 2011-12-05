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
package org.apache.vysper.xmpp.modules.extension.xep0045_muc.handler;

import static org.apache.vysper.xmpp.stanza.IQStanzaType.SET;

import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Affiliation;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Occupant;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Role;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.model.Room;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.stanzas.IqAdminItem;
import org.apache.vysper.xmpp.modules.extension.xep0045_muc.stanzas.MucUserItem;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.StanzaErrorCondition;

/**
 */
public class GrantModeratorTestCase extends AbstractGrantRevokeTestCase {

    public void testGrantVoice() throws Exception {
        Room room = conference.findOrCreateRoom(ROOM2_JID, "Room 2");
        Occupant occ1 = room.addOccupant(OCCUPANT1_JID, "nick");
        occ1.setRole(Role.Moderator);
        room.getAffiliations().add(OCCUPANT1_JID, Affiliation.Admin);
        
        Occupant occ2 = room.addOccupant(OCCUPANT2_JID, "Nick 2");
        occ2.setRole(Role.Participant);

        // send message to room
        IQStanza result = (IQStanza) IQStanza.getWrapper(sendIq(OCCUPANT1_JID, ROOM2_JID, SET, "id1",
                NamespaceURIs.XEP0045_MUC_ADMIN, new IqAdminItem("Nick 2", Role.Moderator)));
        
        assertIqResultStanza(ROOM2_JID, OCCUPANT1_JID, "id1", result);

        assertEquals(Role.Moderator, room.findOccupantByNick("Nick 2").getRole());

        // verify that remaining users got presence
        assertPresenceStanza(new EntityImpl(ROOM2_JID, "Nick 2"), OCCUPANT2_JID, null,
                new MucUserItem(null, null, Affiliation.None, Role.Moderator), null, occupant2Queue.getNext());
        assertPresenceStanza(new EntityImpl(ROOM2_JID, "Nick 2"), OCCUPANT1_JID, null,
                new MucUserItem(null, null, Affiliation.None, Role.Moderator), null, occupant1Queue.getNext());
    }
    
    public void testMemberAttemptGrantModerator() throws Exception {
        Room room = conference.findOrCreateRoom(ROOM2_JID, "Room 2");
        Occupant occupant1 = room.addOccupant(OCCUPANT1_JID, "nick");
        room.getAffiliations().add(OCCUPANT1_JID, Affiliation.Member);
        occupant1.setRole(Role.Moderator);

        room.addOccupant(OCCUPANT2_JID, "Nick 2");

        assertChangeNotAllowed("Nick 2", StanzaErrorCondition.NOT_ALLOWED, null, Role.Moderator);
    }
}
