package nl.topicus.konijn.xmpp.modules.session;

import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
/**
 * Custom Session IQ handler
 * 
 * @author Joost Limburg
 *
 */
public class CustomSessionIQHandler extends DefaultIQHandler {

	@Override
	protected boolean verifyInnerElement(Stanza stanza) {
		return verifyInnerElementWorker(stanza, "session")
				&& verifyInnerNamespace(stanza,
						NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SESSION);
	}

	@Override
	protected Stanza handleSet(IQStanza stanza,
			ServerRuntimeContext serverRuntimeContext,
			SessionContext sessionContext) {
		return StanzaBuilder
				.createIQStanza(null, null, IQStanzaType.RESULT, stanza.getID())
				.addAttribute("from",
						sessionContext.getServerJID().getFullQualifiedName())
				.build();
	}
}
