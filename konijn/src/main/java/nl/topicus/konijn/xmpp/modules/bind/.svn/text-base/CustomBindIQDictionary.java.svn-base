package nl.topicus.konijn.xmpp.modules.bind;

import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;

/**
 * Custom Binding, allows the user to define what namespace to bind to. Not
 * possible in original implementation.
 * 
 * @author Joost Limburg
 * 
 */
public class CustomBindIQDictionary extends NamespaceHandlerDictionary {
	public CustomBindIQDictionary() {
		super(NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_BIND);
		register(new CustomBindIQHandler());
		seal();
	}
}
