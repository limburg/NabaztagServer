package nl.topicus.konijn.xmpp.modules.session;

import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;

/**
 * Custom session handler
 * 
 * @author Joost Limburg
 *
 */
public class CustomSessionDictionary extends NamespaceHandlerDictionary {

	public CustomSessionDictionary() {
		super(NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SESSION);
		register(new CustomSessionIQHandler());
		seal();
	}
}
