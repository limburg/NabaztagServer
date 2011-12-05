package nl.topicus.konijn.config.modules;

import org.guiceyfruit.jndi.JndiBind;

import com.google.inject.Inject;
import com.google.inject.name.Named;

@JndiBind("setting")
public class Setting {
	private String host;
	
	@Inject 
	public Setting(@Named("setting.host") String host)
	{
		this.host = host;
	}

	public String getHost() {
		return host;
	}
}
