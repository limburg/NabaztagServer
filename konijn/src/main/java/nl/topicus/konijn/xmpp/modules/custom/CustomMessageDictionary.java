package nl.topicus.konijn.xmpp.modules.custom;

import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMessageDictionary extends NamespaceHandlerDictionary {
	final Logger LOGGER = LoggerFactory
	.getLogger(CustomMessageDictionary.class);
	
	public CustomMessageDictionary() {
		super("violet:custom");
		register(new CustomMessageHandler());
		seal();
	}
	

    @Override
    public StanzaHandler get(Stanza stanza) {
        String namespace;
        if(stanza.getVerifier().subElementsPresentExact(1)) {
            namespace = stanza.getFirstInnerElement().getNamespaceURI();
        } else {
            namespace = stanza.getNamespaceURI();
        }
        LOGGER.info("1: " + namespace);
        
        if(namespace != null && namespace.equals("violet:custom")) {
            return super.get(stanza);
        } else {
            return null;
        }
    }
}
