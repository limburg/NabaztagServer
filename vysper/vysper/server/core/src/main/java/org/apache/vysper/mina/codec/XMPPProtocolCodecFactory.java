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
package org.apache.vysper.mina.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.vysper.xml.decoder.XMPPDecoder;

/**
 * factory class for Vysper-specific encoder and decoder
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class XMPPProtocolCodecFactory implements ProtocolCodecFactory {

    public ProtocolEncoder getEncoder(IoSession s) throws Exception {
        return new StanzaWriterProtocolEncoder();
    }

    public ProtocolDecoder getDecoder(IoSession s) throws Exception {
        return new XMPPDecoder(new StanzaBuilderFactory());
    }
}
