package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import models.Message;
import models.User;

@Remote
public interface ChatManagerRemote {

	public boolean login(String username, String password, String id);

	public boolean register(User user);

	public List<User> loggedInUsers();
	
	public List<User> regeisteredUsers();
	
	public boolean logout(String username, String password);
	
	public String getSessionId(String username);
	
	public void logOutUsersFromNode(String alias);
	
	public void registerFromNode(User user);
	
	public void loginFromNode(User user);
	
	public void logoutFromNode(String username);
	
	public void getLogUsersFromMaster(String info,String nodeAlias, String nodeAddress);
	public void getRegUsersFromMaster(String info,String nodeAlias, String nodeAddress);
	
	
}
