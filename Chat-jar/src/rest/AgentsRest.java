package rest;

import javax.ejb.Remote;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import models.Message;
import models.User;

@Remote
public interface AgentsRest {
	@POST
	@Path("/agents/classes")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAllAgentsTypes(Dto dto);
	
	@POST
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAllAgents(Dto dto);
	
	@POST
	@Path("/agents/start")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAgent(Dto dto);
	
	@POST
	@Path("/agents/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(Dto dto);
	
	@POST
	@Path("/messages/performatives")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPerformatives(Dto dto);
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(Dto dto);

}
