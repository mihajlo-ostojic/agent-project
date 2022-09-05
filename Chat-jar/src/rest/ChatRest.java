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
public interface ChatRest {
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(User user);
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public void login(User user);
	
//	@GET
//	@Path("/loggedIn/{id}")
//	public void getloggedInUsers(@PathParam("id") String id);
	
//	@GET
//	@Path("/registered/{id}")
//	public void getRegisteredUsers(@PathParam("id") String id);
//	
	@POST
	@Path("/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getloggedInUsers(String id);
	
	@POST
	@Path("/registered")
	@Consumes(MediaType.APPLICATION_JSON)
 	public void getRegisteredUsers(String id);
	
	
	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendToAll(Message msg);
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendToUser(Message msg);
	
//	@GET
//	@Path("/{user}/{id}")
//	public void getUserMessages(@PathParam("user")String user,@PathParam("id")String id);
	@POST
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUserMessages(Message msg);
	
	@DELETE
	@Path("/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logOut(User user);

}
