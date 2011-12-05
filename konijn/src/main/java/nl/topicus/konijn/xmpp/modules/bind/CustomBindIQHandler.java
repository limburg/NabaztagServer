package nl.topicus.konijn.xmpp.modules.bind;

import nl.topicus.konijn.xmpp.util.CustomResourceRegistry;

import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

/**
 * handles bind requests
 * 
 * @author Joost Limburg
 */
public class CustomBindIQHandler extends DefaultIQHandler {

	@Override
	protected boolean verifyInnerElement(Stanza stanza) {
		return verifyInnerNamespace(stanza,
				NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_BIND)
				&& (verifyInnerElementWorker(stanza, "bind") || verifyInnerElementWorker(
						stanza, "unbind"));
	}

	@Override
	protected Stanza handleSet(IQStanza stanza,
			ServerRuntimeContext serverRuntimeContext,
			SessionContext sessionContext) {

		String resourceId = null;

		boolean binding = true;

		for (XMLElement xe : stanza.getInnerElements()) {
			if (xe.getName().equals("bind")) {
				binding = true;
				for (XMLElement xb : xe.getInnerElements()) {
					if (xb.getName().equals("resource")) {
						resourceId = xb.getInnerText().getText();
					}
				}
			} else if (xe.getName().equals("unbind")) {
				binding = false;
				for (XMLElement xb : xe.getInnerElements()) {
					if (xb.getName().equals("resource")) {
						resourceId = xb.getInnerText().getText();
					}
				}
			}
		}

		if (binding) {
			((CustomResourceRegistry) serverRuntimeContext
					.getResourceRegistry()).bindSession(resourceId,
					sessionContext);
		} else {
			serverRuntimeContext.getPresenceCache().remove(stanza.getFrom());
			// ((CustomResourceRegistry) serverRuntimeContext
			// .getResourceRegistry()).unbindResource(resourceId);
		}
		Entity entity = new EntityImpl(sessionContext.getInitiatingEntity(),
				resourceId);

		StanzaBuilder stanzaBuilder = StanzaBuilder
				.createIQStanza(null, null, IQStanzaType.RESULT, stanza.getID())
				.startInnerElement(binding ? "bind" : "unbind",
						NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_BIND)
				.startInnerElement("jid",
						NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_BIND)
				.addText(entity.getFullQualifiedName()).endInnerElement()
				.endInnerElement();

		return stanzaBuilder.build();
	}
}
