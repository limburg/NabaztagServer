package nl.topicus.konijn.xmpp.modules.custom;

import org.apache.vysper.xmpp.protocol.ProtocolException;
import org.apache.vysper.xmpp.protocol.ResponseStanzaContainer;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMessageHandler  implements StanzaHandler {
	final Logger logger = LoggerFactory.getLogger(CustomMessageHandler.class);


	@Override
	public boolean isSessionRequired() {
		logger.warn("session");
		return false;
	}

	@Override
	public ResponseStanzaContainer execute(Stanza stanza,
			ServerRuntimeContext serverRuntimeContext,
			boolean isOutboundStanza, SessionContext sessionContext,
			SessionStateHolder sessionStateHolder) throws ProtocolException {
		logger.warn("Received custom message");
		return null;
	}

	@Override
	public String getName() {
		logger.warn("name");
		return "violet:custom";
	}

	@Override
	public boolean verify(Stanza stanza) {
		logger.warn("verify");
		return false;
	}

}
