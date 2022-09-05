package nodemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;


import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;



import models.Host;


@Singleton
@LocalBean
public class NodeManagerBean implements NodeManagerRemote{

	private Host currentNode;// node of machine
	private Map<String, Host> nodes; //all nodes
	
	
	
	public static String nodeport = "%s:8080";
	
	
	public NodeManagerBean() {
		
		currentNode = createThisNode();
		nodes = new HashMap<>();
	}

	@Override
	public void addNode(Host node) {
		if (node.getAlias().equals(currentNode.getAlias())) return;
		System.out.println(String.format("Adding node: %s", node.getAlias()));
		nodes.put(node.getAlias(), node);
	}

	@Override
	public void removeNode(String nodeAlias) {
		
		nodes.remove(nodeAlias);
	}

	@Override
	public Host getCurrentNode() {
		return currentNode;
	}

	@Override
	public Host getNode(String nodeAlias) {
		return nodes.get(nodeAlias);
	}

	@Override
	public List<String> getAllAliases() {
		return new ArrayList<String>(nodes.keySet());
	}

	@Override
	public List<Host> getAllNodes() {
		return new ArrayList<Host>(nodes.values());
	}
	
	
	
	
	//init node methods
	

	public Host createThisNode() {
		String master = getMasterAlias();
		String alias = String.format(nodeport,  getAlias());
		String address = getAddress();
		return new Host(master, alias,address);
	}
	
	
	private String getAddress() {		
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			return (String) server.getAttribute(http, "boundAddress");			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getAlias() {		
		return System.getProperty("jboss.node.name");
	}
		
	private String getMasterAlias() {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream fileInput  = loader.getResourceAsStream("nodemanager/connection.properties");
			
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			
			String mip = connectionProperties.getProperty("master");
			
			if (mip.isEmpty()) {
				return null;
			}
			
			return String.format(nodeport, mip); 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	

}
