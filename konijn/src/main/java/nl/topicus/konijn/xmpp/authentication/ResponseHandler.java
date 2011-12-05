package nl.topicus.konijn.xmpp.authentication;

import nl.topicus.konijn.xmpp.util.WicketInjector;

import org.apache.commons.codec.binary.Base64;
import org.apache.vysper.xmpp.addressing.EntityFormatException;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authentication.AuthenticationResponses;
import org.apache.vysper.xmpp.modules.core.sasl.handler.AbstractSASLHandler;
import org.apache.vysper.xmpp.protocol.ResponseStanzaContainer;
import org.apache.vysper.xmpp.protocol.ResponseStanzaContainerImpl;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.Stanza;

/**
 * Response Handler for MD5-Digest authentication
 * 
 * @author Joost Limburg
 */
public class ResponseHandler extends AbstractSASLHandler {
	private static final AuthenticationResponses AUTHENTICATION_RESPONSES = new AuthenticationResponses();

	public String getName() {
		return "response";
	}

	public boolean isSessionRequired() {
		return true;
	}

	@Override
	protected ResponseStanzaContainer executeWorker(Stanza stanza,
			SessionContext sessionContext, SessionStateHolder sessionStateHolder) {
		Stanza curStanza = AUTHENTICATION_RESPONSES
				.getFailureMalformedRequest();
		try {
			WicketInjector.inject(sessionContext);
			if (sessionContext.getAttribute("nonce") != null) {
				String nonce = (String) sessionContext.getAttribute("nonce");

				byte decoded[] = Base64.decodeBase64(stanza.getInnerText()
						.getText());
				String decodedString = new String(decoded);

				boolean passwordSet = false;
				boolean usernameSet = false;
				boolean nonceMatches = false;
				boolean cnonceSet = false;

				// Disect text:
				for (String key : decodedString.split(",")) {
					String disected[] = key.split("=");

					if (disected.length == 2 && disected[1].length() > 0) {
						String valued = disected[1].replace("\"", "");

						if (disected[0].equalsIgnoreCase("username")) {
							usernameSet = true;
							sessionContext.putAttribute("username", valued);
						} else if (disected[0].equalsIgnoreCase("nonce")) {
							nonceMatches = valued.equals(nonce);
						} else if (disected[0].equalsIgnoreCase("cnonce")) {
							sessionContext.putAttribute("cnonce", valued);
							cnonceSet = true;
						} else if (disected[0].equalsIgnoreCase("response")) {
							passwordSet = true;
							sessionContext.putAttribute("password", valued);
						}
					}
				}

				// Check for success:
				if (passwordSet && usernameSet && nonceMatches && cnonceSet) {
					EntityImpl initiatingEntity;

					try {
						initiatingEntity = EntityImpl
								.parse((String) sessionContext
										.getAttribute("username")
										+ "@"
										+ sessionContext.getServerJID()
												.getDomain());

						boolean authorized = sessionContext
								.getServerRuntimeContext()
								.getUserAuthentication()
								.verifyCredentials(
										initiatingEntity,
										(String) sessionContext
												.getAttribute("password"), sessionContext);

						// Verify password:
						if (authorized) {
							sessionContext
									.setInitiatingEntity(initiatingEntity);
							sessionStateHolder
									.setState(SessionState.AUTHENTICATED);
							curStanza = AUTHENTICATION_RESPONSES.getSuccess();
						} else {
							curStanza = AUTHENTICATION_RESPONSES
									.getFailureNotAuthorized();
						}
					} catch (EntityFormatException e) {

					}
				} else {
					curStanza = AUTHENTICATION_RESPONSES
							.getFailureNotAuthorized();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseStanzaContainerImpl(curStanza);
	}

}
