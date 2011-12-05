package nl.topicus.konijn.xmpp.modules.sources;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.violet.factories.AmbientPacketFactory;
import nl.topicus.konijn.xmpp.util.WicketInjector;

import org.apache.log4j.Logger;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.apache.wicket.util.crypt.Base64;

/**
 * Handler for Sources query.
 * 
 * @author Joost Limburg
 * 
 */
public class VioletSourcesHandler extends DefaultIQHandler {
	private static final Logger LOGGER = Logger
			.getLogger(VioletSourcesHandler.class);

	@Override
	protected Stanza handleGet(IQStanza stanza,
			ServerRuntimeContext serverRuntimeContext,
			SessionContext sessionContext) {
		StanzaBuilder stanzaBuilder = null;
		try {
			WicketApplication wi = WicketInjector.inject(sessionContext);
			LOGGER.info("Sending initial sources");

			Nabaztag nab = wi.getDelegatorInstance().vysperGetNabaztag(stanza.getFrom().getNode());

			String retValue = Base64.encodeBase64String(AmbientPacketFactory
					.composeAmbientPacket(nab.getLeftEar(), nab.getRightEar(),
							nab.getBlink(), false, true));

			stanzaBuilder = StanzaBuilder
					.createIQStanza(stanza.getTo(), stanza.getFrom(),
							IQStanzaType.RESULT, stanza.getID())
					.startInnerElement("query", "violet:iq:sources")
					.startInnerElement("packet", "violet:packet")
					.addText(retValue).addAttribute("format", "1.0")
					.addAttribute("ttl", "604800").endInnerElement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return stanzaBuilder.build();
	}
}
