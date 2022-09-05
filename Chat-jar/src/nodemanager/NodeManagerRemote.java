package nodemanager;

import java.util.List;

import javax.ejb.Remote;
import models.Host;

@Remote
public interface NodeManagerRemote {
	
	public void addNode(Host node);
	public void removeNode(String nodeAlias);
	
	public Host getCurrentNode();
	public Host getNode(String nodeAlias);

	public List<String> getAllAliases();
	public List<Host> getAllNodes();
}
