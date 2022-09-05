package messagemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Default;

import agents.AID;
import messagemanager.ACLMessage;


public class ACLMessage implements Serializable {

	
	private static final long serialVersionUID = 9089809346282605606L;

	public Performative performative;

	public AID sender;
	
	public List<AID> receivers;
	
	public AID replyTo;

	
	public String content;
	public Serializable contentObj;
	
	public Map<String, String> userArgs;
	
	public String language;
	
	public String encoding;
	
	public String ontology;

	public String protocol;
	
	public String conversationId;
	
	public String replyWith;
	
	public String inReplyTo;
	
	public long replyBy;
	
	public boolean isNotForChat = false;
	
	public ACLMessage() {
		userArgs = new HashMap<>();
		receivers = new ArrayList<>();
	}

	public ACLMessage(Performative performative) {
		this.performative = performative;
		receivers = new ArrayList<>();
		userArgs = new HashMap<>();
	}
	
	public ACLMessage(Performative performative, AID sender, List<AID> recivers, Serializable content) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.receivers = recivers;
		this.contentObj = content;
		userArgs = new HashMap<>();
	}
	
	public ACLMessage(Performative performative, AID sender, List<AID> recivers) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.receivers = recivers;
		userArgs = new HashMap<>();
	}

	
	public Map<String, String> getUserArgs()
	{
		return userArgs;
	}
	public Serializable getUserArg(String arg)
	{
		return userArgs.get(arg);
	}

	public boolean canReplyTo() {
		return sender != null || replyTo != null;
	}

	public ACLMessage makeReply(Performative performative) {
		if (!canReplyTo())
			throw new IllegalArgumentException("There's no-one to receive the reply.");
		ACLMessage reply = new ACLMessage(performative);
		// receiver
		reply.receivers.add(replyTo != null ? replyTo : sender);
		// description of content
		reply.language = language;
		reply.ontology = ontology;
		reply.encoding = encoding;
		// control of conversation
		reply.protocol = protocol;
		reply.conversationId = conversationId;
		reply.inReplyTo = replyWith;
		return reply;
	}

}
