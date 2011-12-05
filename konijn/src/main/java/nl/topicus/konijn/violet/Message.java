package nl.topicus.konijn.violet;

public class Message {
	private String source;
	
	private String value;

	public Message()
	{
		
	}
	public Message(String source, String value) {
		super();
		this.setSource(source);
		this.setValue(value);
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}

}
