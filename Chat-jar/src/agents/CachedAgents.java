package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import models.Book;

/**
 * Session Bean implementation class CachedAgents
 */
@Singleton
@LocalBean
@Remote(CachedAgentsRemote.class)
public class CachedAgents implements CachedAgentsRemote{

	HashMap<String, Agent> runningAgents;
	ArrayList<Book> bookStore1;
	ArrayList<Book> bookStore2;
	ArrayList<Book> bookStore3;
	HashMap<AID, Agent> rAgents;
//	public Map<AID, Agent> runingAgents = new HashMap<AID, Agent>();
	/**
	 * Default constructor.
	 */
	public CachedAgents() {

		runningAgents = new HashMap<>();
		bookStore1 = new ArrayList<Book>(); 
		bookStore2 = new ArrayList<Book>(); 
		bookStore3 = new ArrayList<Book>();
		rAgents =  new HashMap<>();
	}

	@Override
	public HashMap<String, Agent> getRunningAgents() {
		return runningAgents;
	}

	@Override
	public void addRunningAgent(String key, Agent agent) {
		System.out.println("Added agent to cached agents");
		runningAgents.put(key, agent);
	}

	@Override
	public void stopAgent(String agentId) {
		runningAgents.remove(agentId);
		System.out.println("Removed agent:" + agentId);
	}
	
	@Override
	public ArrayList<Book> getStore1() {
		return bookStore1;
	}

	@Override
	public ArrayList<Book> getStore2() {
		// TODO Auto-generated method stub
		return bookStore2;
	}

	@Override
	public ArrayList<Book> getStore3() {
		// TODO Auto-generated method stub
		return bookStore3;
	}

	@Override
	public void setStore1(List<Book> books) {
		bookStore1 = (ArrayList<Book>) books;
		
	}

	@Override
	public void setStore2(List<Book> books) {
		bookStore2 = (ArrayList<Book>) books;
		
		
	}

	@Override
	public void setStore3(List<Book> books) {
		bookStore3 = (ArrayList<Book>) books;
		
	}

	@Override
	public HashMap<AID, Agent> getRunnAgents() {
		return rAgents;
	}

}
