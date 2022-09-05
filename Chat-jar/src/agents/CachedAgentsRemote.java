package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Book;

public interface CachedAgentsRemote {

	public HashMap<String, Agent> getRunningAgents();
	public void addRunningAgent(String key, Agent agent);
	public void stopAgent(String agentId);
	
	public ArrayList<Book> getStore1();
	public ArrayList<Book> getStore2();
	public ArrayList<Book> getStore3();
	
	public void setStore1(List<Book> books);
	public void setStore2(List<Book> books);
	public void setStore3(List<Book> books);
	public HashMap<AID, Agent> getRunnAgents();
	
	
}
