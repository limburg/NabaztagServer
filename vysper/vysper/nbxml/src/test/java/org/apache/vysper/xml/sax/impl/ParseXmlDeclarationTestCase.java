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
package org.apache.vysper.xml.sax.impl;

import java.util.Iterator;

import org.apache.vysper.xml.sax.impl.TestHandler.TestEvent;

/**
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class ParseXmlDeclarationTestCase extends AbstractAsyncXMLReaderTestCase {

    public void testEmptyElement() throws Exception {
        Iterator<TestEvent> events = parse("<?xml version=\"1.0\"?>\n <root />").iterator();

        assertStartDocument(events.next());
        // no event for the declaration
        assertStartElement("", "root", "root", events.next());
        assertEndElement("", "root", "root", events.next());
        assertEndDocument(events.next());

        assertNoMoreevents(events);
    }

}