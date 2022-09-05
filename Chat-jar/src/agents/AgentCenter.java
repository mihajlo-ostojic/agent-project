package agents;

public class AgentCenter {

	private String alias;
	private String adress;
	
	
	public AgentCenter() {
		super();
	}
	public AgentCenter(String alias, String adress) {
		super();
		this.alias = alias;
		this.adress = adress;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	
	
}
