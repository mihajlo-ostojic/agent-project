import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { UserserService } from './userser.service';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor(private http : HttpClient, private userService: UserserService) { }

  send(sender : string, receiver : string, content : string,subject : string) {
    console.log("sender: "+sender);
    console.log("receiver: "+receiver);
    console.log("content: "+content);
    console.log("subject: "+subject);
    
    
    var data:any = new Object();
    data['sender'] = sender;
    data['reciver'] = receiver;
    data['content'] = content;
    data['date'] = "date"
    data['subject'] = subject;
    data['id'] = localStorage.getItem("sessionId");
    
    this.http.post("http://localhost:8080/Chat-war/api/chat/user", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  sendToAll(sender : string, content : string,subject : string) {
    console.log("salje nesto")
    console.log("sender: "+sender);
    console.log("content: "+content);
    console.log("subject: "+subject);
    var data:any = new Object();
    data['sender'] = sender;
    data['reciver'] = "all";
    data['content'] = content;
    data['date'] = "date"
    data['subject'] = subject;
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/all", data, {headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getMessages() {
    var data:any = new Object();
    data['sender'] = this.userService.getUsername();
    data['reciver'] = this.userService.getUsername();
    data['content'] = "content";
    data['date'] = "date"
    data['subject'] = "subject";
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/get" ,data ,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getMessagesForUsername(username:string) {
    var data:any = new Object();
    data['sender'] = username;
    data['reciver'] = this.userService.getUsername();
    data['content'] = "content";
    data['date'] = "date"
    data['subject'] = "subject";
    data['id'] = localStorage.getItem("sessionId");
    this.http.post("http://localhost:8080/Chat-war/api/chat/get" ,data ,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }


  getAgentTypes() {

    
    var data:any = new Object();
    data['command'] = " ";
    data['performative'] = "";
    data['agentName'] = " ";
    data['agentType'] = " ";
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = " ";
    data['store'] = " ";
    data['author'] = " ";
    data['title'] = " ";
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/agents/classes", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getRunningAgents() {
    
    
    var data:any = new Object();
    data['command'] = " ";
    data['performative'] = "";
    data['agentName'] = " ";
    data['agentType'] = " ";
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = " ";
    data['store'] = " ";
    data['author'] = " ";
    data['title'] = " ";
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/agents/running", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }


  getPerformatives() {

    
    var data:any = new Object();
    data['command'] = " ";
    data['performative'] = "";
    data['agentName'] = " ";
    data['agentType'] = " ";
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = " ";
    data['store'] = " ";
    data['author'] = " ";
    data['title'] = " ";
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/messages/performatives", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  startAgent(name: String, type: String) {
    
    
    var data:any = new Object();
    data['command'] = " ";
    data['performative'] = "";
    data['agentName'] = name;
    data['agentType'] = type;
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = " ";
    data['store'] = " ";
    data['author'] = " ";
    data['title'] = " ";
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/agents/start", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  stopAgent(name: String, type: String) {
    
    
    var data:any = new Object();
    data['command'] = " ";
    data['performative'] = "";
    data['agentName'] = name;
    data['agentType'] = type;
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = " ";
    data['store'] = " ";
    data['author'] = " ";
    data['title'] = " ";
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/agents/stop", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  sendMessage(name: String, type: String, command: String, performative:String, host:String, store:String, author:String, title:String) {
    
    
    var data:any = new Object();
    data['command'] = command;
    data['performative'] = performative;
    data['agentName'] = name;
    data['agentType'] = type;
    data['id'] = localStorage.getItem("agentSessionId");
    data['hostAlias'] = host;
    data['store'] = store;
    data['author'] = author;
    data['title'] = title;
    
    this.http.post("http://localhost:8080/Chat-war/api/agent/messages", data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }
}
