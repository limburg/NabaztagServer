package nl.topicus.konijn.xmpp.modules.sources;

import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

/**
 * Handles the bunny request for sources, the initial settings for the buny
 * (light, ears..)
 * 
 * @author Joost Limburg
 * 
 */
public class VioletSourcesDictionary extends NamespaceHandlerDictionary {
	public VioletSourcesDictionary() {
		super("violet:iq:sources");
		register(new VioletSourcesHandler());
		seal();
	}
}
