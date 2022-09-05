package app;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agents.AID;
import agents.AgentType;

public interface AgentRest {
	
	@GET
	@Path("/classes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAllTypes();
	
	@POST
	@Path("/classes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateTypes(List<AgentType> types);
	
	@GET
	@Path("/running")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getAllAgents();
	
	@POST
	@Path("/running")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateAgents(List<AID> agents);
	
	@PUT
	@Path("/running/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAgent(AgentType type, @PathParam("name") String name);
	
	@DELETE
	@Path("/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(AID aid);

}
