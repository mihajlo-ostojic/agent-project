package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import agentmanager.AgentManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.AgentMessage;
import models.Book;
import ws.WSAgents;

public class SearchAgent implements Agent{

	public AID aid;
	
	@EJB
	private WSAgents socketAgent;
	
	@EJB
	private CachedAgentsRemote cachedAgents; 
	
	
	@EJB 
	private AgentManagerRemote agentManager;
	@Override
	public String init() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(String agentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(AID aid) {
		this.aid = aid;
		
	}

	@Override
	public void handleMessage(AgentMessage message) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public void handleMessage(ACLMessage message) {


		String receiver = (String) message.userArgs.get("reciver");
		String socket = (String) message.userArgs.get("socket");
		
		String option = (String) message.userArgs.get("command");
		String store = (String) message.userArgs.get("store");
		System.out.println("Collector dobio komandu: " + option);
		System.out.println("Collector dobio komanda poslata sa soketa: " + socket);
		System.out.println("Collector pretrazuje u storeu: " + store);
		
		String author = (String) message.userArgs.get("author");
		
		String title = (String) message.userArgs.get("title");
		
		
		
		switch(option)
		{
			case "SEARCH_STORE_1":
				
				List<Book> foundBooks = new ArrayList<Book>();
				boolean isautor = false;
				boolean istitle = false;
				for(Book b:cachedAgents.getStore1())
				{
					
					if(author==null || author.isEmpty())
					{
						isautor = true;
					}else
					{
						if(b.getAuthor().contains(author))
						{
							isautor = true;
						}else
						{
							isautor = false;
						}
					}
					
					if(title==null || title.isEmpty())
					{
						istitle = true;
					}else
					{
						if(b.getName().contains(title))
						{
							istitle = true;
						}else
						{
							istitle = false;
						}
					}
					
					if(isautor && istitle)
					{
						foundBooks.add(b);
					}
				}
				String response = "books:";
				for(Book b:foundBooks)
				{
					response += b.getName() + "," + b.getAuthor() + "," + b.getPrice() +","+ b.getHref() + "|";
				}
				socketAgent.onMessage(socket, response);
				break;
			case "SEARCH_STORE_2":
				
				foundBooks = new ArrayList<Book>();
				isautor = false;
				istitle = false;
				for(Book b:cachedAgents.getStore2())
				{
					
					if(author==null || author.isEmpty())
					{
						isautor = true;
					}else
					{
						if(b.getAuthor().contains(author))
						{
							isautor = true;
						}else
						{
							isautor = false;
						}
					}
					
					if(title==null || title.isEmpty())
					{
						istitle = true;
					}else
					{
						if(b.getName().contains(title))
						{
							istitle = true;
						}else
						{
							istitle = false;
						}
					}
					
					if(isautor && istitle)
					{
						foundBooks.add(b);
					}
				}
				response = "books:";
				for(Book b:foundBooks)
				{
					response += b.getName() + "," + b.getAuthor() + "," + b.getPrice() +","+ b.getHref() + "|";
				}
				socketAgent.onMessage(socket, response);
				break;
			case "SEARCH_STORE_3":
				//
				foundBooks = new ArrayList<Book>();
				isautor = false;
				istitle = false;
				for(Book b:cachedAgents.getStore3())
				{
					
					if(author==null || author.isEmpty())
					{
						isautor = true;
					}else
					{
						if(b.getAuthor().contains(author))
						{
							isautor = true;
						}else
						{
							isautor = false;
						}
					}
					
					if(title==null || title.isEmpty())
					{
						istitle = true;
					}else
					{
						if(b.getName().contains(title))
						{
							istitle = true;
						}else
						{
							istitle = false;
						}
					}
					
					if(isautor && istitle)
					{
						foundBooks.add(b);
					}
				}
				response = "books:";
				for(Book b:foundBooks)
				{
					response += b.getName() + "," + b.getAuthor() + "," + b.getPrice() +","+ b.getHref() + "|";
				}
				socketAgent.onMessage(socket, response);
				break;
			default:
				
				break;
		}
		
	}

	@Override
	public String getAgentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return aid.getName();
	}

}
