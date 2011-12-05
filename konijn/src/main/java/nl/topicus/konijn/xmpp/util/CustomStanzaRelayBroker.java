package nl.topicus.konijn.xmpp.util;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityUtils;
import org.apache.vysper.xmpp.delivery.StanzaRelay;
import org.apache.vysper.xmpp.delivery.failure.DeliveryException;
import org.apache.vysper.xmpp.delivery.failure.DeliveryFailureStrategy;
import org.apache.vysper.xmpp.protocol.ProtocolException;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom stanza relay
 * 
 * @author Joost Limburg
 */
public class CustomStanzaRelayBroker implements StanzaRelay {

	final Logger LOGGER = LoggerFactory
			.getLogger(CustomStanzaRelayBroker.class);

	protected StanzaRelay internalRelay;

	protected StanzaRelay externalRelay;

	protected ServerRuntimeContext serverRuntimeContext;

	/**
	 * a StanzaRelay receiving stanzas which are targeted to server-local JIDs
	 */
	public void setInternalRelay(StanzaRelay internalRelay) {
		this.internalRelay = internalRelay;
	}

	/**
	 * a StanzaRelay receiving stanzas which are targeted to JIDs which are
	 * external to this server
	 */
	public void setExternalRelay(StanzaRelay externalRelay) {
		this.externalRelay = externalRelay;
	}

	public void setServerRuntimeContext(
			ServerRuntimeContext serverRuntimeContext) {
		this.serverRuntimeContext = serverRuntimeContext;
	}

	public void relay(Entity receiver, Stanza stanza,
			DeliveryFailureStrategy deliveryFailureStrategy)
			throws DeliveryException {

		// boolean toServerTLD = receiver == null
		// || (!receiver.isNodeSet() && EntityUtils.isAddressingServer(
		// receiver, serverRuntimeContext.getServerEnitity()));

		boolean toServerTLD = false;

		String domain = receiver.getDomain();
		if (domain.equalsIgnoreCase("xmpp.platform.violet.net")
				|| domain.equalsIgnoreCase("xmpp.objects.violet.net")
				|| domain.equalsIgnoreCase("xmpp.applet.violet.net")
				|| receiver.getNode().equals("net.violet.platform")) {
			toServerTLD = true;
		}

		boolean toComponent = receiver != null
				&& EntityUtils.isAddressingServerComponent(receiver,
						serverRuntimeContext.getServerEnitity());
		if (toServerTLD) {
			
			
			System.out.println("Got: " + stanza.getNamespaceURI() + " "+ stanza.getName());
			Stanza innerStanza = new Stanza("violet:custom", stanza.getName(),
					stanza.getNamespacePrefix(), stanza.getAttributes(),
					stanza.getInnerFragments(), stanza.getDeclaredNamespaces());
			StanzaHandler handler = serverRuntimeContext.getHandler(innerStanza);
			SessionContext context = serverRuntimeContext.getResourceRegistry().getSessionContext(stanza.getFrom().getResource());
			
			try {
				System.out.println("Got handler: " + handler.getName());
				handler.execute(innerStanza, serverRuntimeContext, false, context, null);
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			
			return;
		}

		boolean relayToExternal = serverRuntimeContext.getServerFeatures()
				.isRelayingToFederationServers();

		if (EntityUtils.isAddressingServer(receiver,
				serverRuntimeContext.getServerEnitity())
				|| toComponent) {
			internalRelay.relay(receiver, stanza, deliveryFailureStrategy);
		} else {
			if (!relayToExternal)
				throw new IllegalStateException(
						"this server is not relaying to external currently");
			externalRelay.relay(receiver, stanza, deliveryFailureStrategy);
		}
	}

	@Override
	public boolean isRelaying() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
