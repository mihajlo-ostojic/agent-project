package agents;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;

import messagemanager.ACLMessage;
import messagemanager.AgentMessage;
import ws.WSAgents;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.text.NumberFormat;

import com.jaunt.Element;

import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;

import agentmanager.AgentManagerRemote;
import models.Book;

public class CollectorAgent implements Agent{

	public AID aid;
	
	public String delfiPath = "https://www.delfi.rs/knjige/zanr/88_klasicna_knjizevnost_delfi_knjizare.html";
	public String vulkanPath = "https://www.knjizare-vulkan.rs/opsta-knjizevnost/";
	public String lagunaPath = "https://www.laguna.rs/z15_zanr_domaci_autori_laguna.html";
	
	@EJB
	private WSAgents socketAgent;
	
	@EJB 
	private AgentManagerRemote agentManager;
	
	@EJB
	private CachedAgentsRemote cachedAgents; 
	
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
	public void handleMessage(ACLMessage message) {


		String receiver = (String) message.userArgs.get("reciver");
		String socket = (String) message.userArgs.get("socket");
		
		String option = (String) message.userArgs.get("command");
		String store = (String) message.userArgs.get("store");
		System.out.println("Collector dobio komandu: " + option);
		System.out.println("Collector dobio komanda poslata sa soketa: " + socket);
		System.out.println("Collector skladisti u storeu: " + store);
		
		
		switch(option)
		{
			case "SCRAPE_DELFI":
				
				List<Book>books = collectFromUrl(delfiPath, CollectorAgent::convertDelfiHtmlToBook, "<div class=\"listing-item white-bg item-category-1\">");
				
				if(store.equals("1"))
				{
					List<Book>allbooks = cachedAgents.getStore1();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore1(allbooks);
				}
				if(store.equals("2"))
				{
					List<Book>allbooks = cachedAgents.getStore2();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore2(allbooks);
				}
				if(store.equals("3"))
				{
					List<Book>allbooks = cachedAgents.getStore3();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore3(allbooks);
				}
				socketAgent.onMessage(socket, "collector:New books collected");
				break;
			case "SCRAPE_VULKAN":
				
				books = collectFromUrl(vulkanPath, CollectorAgent::convertVulkanHtmlToBook, "<div class=\"item-data col-xs-12 col-sm-12\">");
				
				if(store.equals("1"))
				{
					List<Book>allbooks = cachedAgents.getStore1();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore1(allbooks);
				}
				if(store.equals("2"))
				{
					List<Book>allbooks = cachedAgents.getStore2();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore2(allbooks);
				}
				if(store.equals("3"))
				{
					List<Book>allbooks = cachedAgents.getStore3();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore3(allbooks);
				}
				socketAgent.onMessage(socket, "collector:New books collected");
				
				break;
			case "SCRAPE_LAGUNA":
				
				books = collectFromUrl(lagunaPath, CollectorAgent::convertLagunaHtmlToBook, "<div class=\"knjiga col-lg-3 col-md-3 col-sm-4 col-xs-8\">");
				
				if(store.equals("1"))
				{
					List<Book>allbooks = cachedAgents.getStore1();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore1(allbooks);
				}
				if(store.equals("2"))
				{
					List<Book>allbooks = cachedAgents.getStore2();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore2(allbooks);
				}
				if(store.equals("3"))
				{
					List<Book>allbooks = cachedAgents.getStore3();
					for(Book book: books)
					{
						allbooks.add(book);
					}
					cachedAgents.setStore3(allbooks);
				}
				socketAgent.onMessage(socket, "collector:New books collected");
				
				break;
			default:
				
				break;
		}
		
	}

	
	
	
	
	public List<Book> collectFromUrl(String url, Function<Element, Book> function, String elementToSearchFor) {
		System.out.println("Collector agent is searching url:");
		System.out.println(url);
		UserAgent agent = new UserAgent();
		try {
			agent.visit(url);
			Elements bookE = agent.doc.findEvery(elementToSearchFor);
			return bookE.toList().stream().map(function).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	static public Book convertDelfiHtmlToBook(Element bookElem) {
		try {
			Element linkAndItemElement = bookElem.findFirst("<a href>");
			String href = linkAndItemElement.getAt("href");
			System.out.println("Collector found link: " + href);
			
			
			
			Element bookBody = bookElem.findFirst("<div class=\"body\">");
			
			Element bookInfo = bookElem.findFirst("<div class=\"carousel-book-info \">");
			
			Element bookTitleH3 = bookInfo.findFirst("<h3>");
			Element bookTitle = bookTitleH3 .findFirst("<a>");
			String title = bookTitle.getTextContent();
			
			String author = "";
			List<Element> nameAndAuthor = bookInfo.getChildElements();
			if(nameAndAuthor.size()==2)
			{
				Element selected = nameAndAuthor.get(1);
				author = selected.getTextContent();
			}
			
			
			Element bookPrice = bookBody.findFirst("<del>");
			String price = bookPrice.getTextContent();
			System.out.println("Collector found name: " + title);
			System.out.println("Collector found author: " + author);
			System.out.println("Collector found price: " + price);
			Book b = new Book();
			b.setAuthor(author);
			b.setSprice(price);
			b.setHref(href);
			b.setName(title);
			
			return b;
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		return null;
	}
	static public Book convertVulkanHtmlToBook(Element bookElem) {
		try {

			
			
			Element imgWrapper = bookElem.findFirst("<div class=\"img-wrapper\">");
			Element link = imgWrapper.findFirst("<a href>");
			String href = link.getAt("href");
			System.out.println("Collector found link: " + href);
			
			
			Element textWrapper = bookElem.findFirst("<div class=\"text-wrapper \">");
			
			Element title = textWrapper.findFirst("<div class=\"title\">");
			Element a= title.findFirst("<a>");
			String btitle = a.getTextContent();
			
			
			Element attributeWrapper = textWrapper.findFirst("<div class=\"atributs-wrapper\">");
			Element a2= attributeWrapper.findFirst("<a>");
			String author = a2.getTextContent();
			
			Element pricesWrapper = textWrapper.findFirst("<div class=\"prices-wrapper\">");
			Element div= pricesWrapper.findFirst("<div>");
			String price = div.getTextContent();
			
			
			
			System.out.println("Collector found name: " + btitle);
			System.out.println("Collector found author: " + author);
			System.out.println("Collector found price: " + price);
			Book b = new Book();
			b.setAuthor(author);
			b.setSprice(price);
			b.setHref(href);
			b.setName(btitle);
			
			return b;
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		return null;
	}
	
	
	static public Book convertLagunaHtmlToBook(Element bookElem) {
		try {

			
			
			Element divSlike = bookElem.findFirst("<div class=\"knjiga_img\">");
			
			Element link = divSlike.findFirst("<a href>");
			String href = link.getAt("href");
			System.out.println("Collector found link: " + href);
			
			String realHref = "https://www.laguna.rs/" + href;
			
			
			Element divPodaci = bookElem.findFirst("<div class=\"podaci\">");
//			Element divNaslov = divPodaci.findFirst("<div class=\"naslov\">");
			String title = "";
			String author = "";
			List<Element> nameAndAuthor = divPodaci.getChildElements();
			if(nameAndAuthor.size()>=3)
			{
				Element selectedName = nameAndAuthor.get(0);
				title = selectedName.getTextContent();
				
				Element selectedAuthor = nameAndAuthor.get(2);
				author = selectedAuthor.getTextContent();
			}
			String price = "nema";
			
			
			
			
			
			System.out.println("Collector found name: " + title);
			System.out.println("Collector found author: " + author);
			System.out.println("Collector found price: " + price);
			Book b = new Book();
			b.setAuthor(author);
			b.setSprice(price);
			b.setHref(realHref);
			b.setName(title);
			
			return b;
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		return null;
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

	@Override
	public void handleMessage(AgentMessage message) {
		// TODO Auto-generated method stub
		
	}

}
