package rest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.AgentType;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.Performative;
import models.Host;
import ws.WSAgents;

@Stateless
@Path("/agent")
public class AgentsRestBean implements AgentsRest {


	@EJB 
	private AgentManagerRemote agentManager;
	
	@EJB 
	private MessageManagerRemote messageManager;
	@EJB
	private WSAgents socket;
	
	@Override
	public void getAllAgentsTypes(Dto dto) {
		// TODO Auto-generated method stub
		
		List<AgentType> types = agentManager.getTypes();
		String response = "agentTypes:";
		for(AgentType type: types)
		{
			response += type.getName();
			if(type.isStatefull()) response +=",true|";
			else response +=",false|";
		}
		
		socket.onMessage(dto.id, response);
		
	}

	@Override
	public void getAllAgents(Dto dto) {
		// TODO Auto-generated method stub
		
		socket.onMessage(dto.id, agentManager.getRunningAgentsString());
	}

	@Override
	public void startAgent(Dto dto) {
		agentManager.startAgent(new AgentType(false,dto.agentType), dto.agentName);
		
	}

	@Override
	public void stopAgent(Dto dto) {
		agentManager.stopAgent(new AID(dto.agentName,new Host("",dto.hostAlias,""),new AgentType(false,dto.agentType)));
		
	}

	@Override
	public void getPerformatives(Dto dto) {
		String response = "performatives:";
		
		List<String> enumNames = Stream.of(Performative.values())
                .map(Enum::name)
                .collect(Collectors.toList());
		for(String s: enumNames)
		{
			response += s+"|";
		}
		
		socket.onMessage(dto.id, response);
	}

	@Override
	public void sendMessage(Dto dto) {
		ACLMessage message = new ACLMessage();
		message.isNotForChat = true;
		message.userArgs.put("reciver", dto.agentName);
		message.userArgs.put("socket", dto.id);
		message.userArgs.put("command", dto.command);
		message.userArgs.put("performative", dto.performative);
		message.userArgs.put("store", dto.store);
		messageManager.post(message);
		
	}

}
