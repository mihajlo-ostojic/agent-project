package app;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.AgentType;

@Stateless
@Path("/agents")
@Remote(AgentRest.class)
public class AgentRestBean implements AgentRest{

	
	@EJB
	private AgentManagerRemote agentManager;
	
	
	@Override
	public List<AgentType> getAllTypes() {
		return agentManager.getTypes();

	}

	@Override
	public void updateTypes(List<AgentType> types) {
		agentManager.updateAgentTypes(types);
		
	}

	@Override
	public List<AID> getAllAgents() {
		return agentManager.getRunningAgentsAids();
	}

	@Override
	public void updateAgents(List<AID> agents) {
		agentManager.updateRunningAgents(agents);
		
	}

	@Override
	public void startAgent(AgentType type, String name) {
		agentManager.startAgent(type, name); 
		
	}

	@Override
	public void stopAgent(AID aid) {
		agentManager.stopAgent(aid);
		
	}

}
