package nl.topicus.konijn.xmpp.authentication;

import nl.topicus.konijn.xmpp.util.WicketInjector;

import org.apache.commons.codec.binary.Base64;
import org.apache.vysper.xmpp.authentication.SASLMechanism;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;
import org.apache.vysper.xmpp.uuid.JVMBuiltinUUIDGenerator;
import org.apache.vysper.xmpp.uuid.UUIDGenerator;

/**
 * handles SASL DigestMD5 mechanism.
 * 
 * @author Joost Limburg
 */
public class DigestMD5 implements SASLMechanism {

	// private static final AuthenticationResponses AUTHENTICATION_RESPONSES =
	// new AuthenticationResponses();
	private UUIDGenerator uuidGenerator = new JVMBuiltinUUIDGenerator(); //

	private static char Q = '"';

	public String getName() {
		return "DIGEST-MD5";
	}

	public Stanza started(SessionContext sessionContext,
			SessionStateHolder sessionStateHolder, Stanza authStanza) {
		WicketInjector.inject(sessionContext);

		String nonce = uuidGenerator.create();
		String challengeReply = "nonce=" + Q + nonce + Q + ",qop=" + Q + "auth"
				+ Q + ",charset=utf-8,algorithm=md5-sess";

		sessionContext.putAttribute("nonce", nonce);

		challengeReply = new String(Base64.encodeBase64(challengeReply
				.getBytes()));

		Stanza stan = new StanzaBuilder("challenge",
				NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL).addText(
				challengeReply).build();

		return stan;

	}
}
