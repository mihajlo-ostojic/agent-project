package app;


import javax.ejb.Remote;
import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import models.Host;

@Remote
public interface NodesRest {

	
	@POST
	@Path("/registernode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerNewNode(Host node);
	
	@POST
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewNode(Host node);
	
	@DELETE
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removNode(Host node);
	
	
	@POST
	@Path("/recivenodes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void reciveNodes(ArrayList<Host> nodes);
	
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean ping();
	
}
