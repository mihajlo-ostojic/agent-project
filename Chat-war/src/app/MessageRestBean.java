package app;



import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ejb.EJB;


import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import java.util.List;

@Stateless
@Path("/messages")
public class MessageRestBean implements MessageRest{

	@EJB
	private MessageManagerRemote messageManager;
	
	@Override
	public void sendMessage(ACLMessage message) {
		messageManager.post(message);
	}

	@Override
	public List<String> getPerformatives() {
//		return messageManager.getPerformatives();
		return null;
	}
	
}
