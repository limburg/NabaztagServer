package nl.topicus.konijn.violet.factories;

import java.io.ByteArrayOutputStream;
import java.util.List;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.violet.Message;
import nl.topicus.konijn.violet.SOURCE;
import nl.topicus.konijn.xmpp.MyXMPPServer;
import nl.topicus.konijn.xmpp.util.BunniePresenceCache;

import org.apache.commons.codec.binary.Base64;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.delivery.failure.DeliveryException;
import org.apache.vysper.xmpp.delivery.failure.IgnoreFailureStrategy;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.apache.wicket.Application;

public class MessagePacketFactory extends AbstractVioletFactory {
	public static boolean sendMessage(String connectedUid,
			List<Message> messages) {
		MyXMPPServer server = ((WicketApplication) Application.get())
				.getXmppFactory().getServer();
		BunniePresenceCache cache = (BunniePresenceCache) server
				.getServerRuntimeContext().getPresenceCache();

		Entity nab = cache.getNode(connectedUid);

		Entity serverEnt = server.getServerRuntimeContext().getServerEnitity();

		if (nab == null) {
			LOGGER.warn("Sending message to unconnected nabaztag "
					+ connectedUid + " failed");
			return false;
		} else {
			LOGGER.info("Sending message to: " + nab.getFullQualifiedName());
		}

		byte[] strMessage = Base64.encodeBase64(MessagePacketFactory
				.composeSimpleMessage(messages));

		StanzaBuilder stanzaBuilder = StanzaBuilder
				.createMessageStanza(serverEnt, nab, null, null)
				.addAttribute("id", "message-12626205")
				.startInnerElement("packet", "violet:packet")
				.addText(new String(strMessage))
				.addAttribute("to", nab.getFullQualifiedName())
				.addAttribute("format", "1.0").addAttribute("ttl", "604800")
				.endInnerElement();

		try {
			server.getServerRuntimeContext()
					.getStanzaRelay()
					.relay(nab, stanzaBuilder.build(),
							new IgnoreFailureStrategy());
		} catch (DeliveryException e) {
			LOGGER.warn("Could not deliver message packet to " + connectedUid);
			return false;
		}

		return true;
	}

	public static byte[] composeSimpleMessage(List<Message> messages) {

		String dataArray = "";
		ByteArrayOutputStream packArray = new ByteArrayOutputStream();

		for (Message msg : messages) {
			if (msg.getValue() != null)
				dataArray += msg.getSource() + " " + msg.getValue() + "\n";
			else
				dataArray += msg.getSource() + "\n";
		}

		byte[] encrypted = crypt8(dataArray, 0x47, 47);

		packArray.write(SOURCE.PACKET_START.getId());
		packArray.write(SOURCE.MESSAGE.getId());
		writeIntTo3Bytes(packArray, 1 + encrypted.length);
		packArray.write(0x0);
		packArray.write(encrypted, 0, encrypted.length);
		packArray.write(SOURCE.EOF.getId());

		return packArray.toByteArray();
	}

}
