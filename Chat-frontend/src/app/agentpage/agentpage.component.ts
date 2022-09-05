import { Component, OnInit, EventEmitter,Output } from '@angular/core';
import { UserserService } from '../services/userser.service';
import { AgentsocketService } from '../services/agentsocket.service';
import { ToastrService } from 'ngx-toastr';
import { MessagesService } from '../services/messages.service';
@Component({
  selector: 'app-agentpage',
  templateUrl: './agentpage.component.html',
  styleUrls: ['./agentpage.component.css']
})
export class AgentpageComponent implements OnInit {

  liveData$ = this.wsService.messages$;

    public message : string = "";
    public performative : string = "";
    public agentName : string = "";
    public agentType : string = "";
  public store: string = "";
  public authore: string = "";
  public title: string = "";

  private types : string[] = [];
  private agents : string[] = [];
  private books : string[] = [];
  private performatives : string[] = [];
  public displayed : any[] = [];//object

  public displayedTypes : any[] = [];//object
  public displayedAgents : any[] = [];//object
  public displayedBooks : any[] = [];//object
  public displayedPerformatives : any[] = [];//object


  @Output() messageEvent = new EventEmitter<string>();
  public selected : string = "";

  constructor(private wsService : AgentsocketService, private userService : UserserService, private toasterService: ToastrService, private mservice :MessagesService) { 
    this.liveData$.subscribe({
      next : msg => this.handleMessage(msg as string)
    });
    this.wsService.connect();
    setTimeout(() => {this.mservice.getAgentTypes(); this.mservice.getRunningAgents();this.mservice.getPerformatives()}, 500)
  }


  handleMessage(message : string) {
    console.log("stigla poruka: "+message);
    if(message.match('.+:.*')) {
      var type = message.split(':')[0];
      var content = message.split(':')[1];
      if(type == 'agentTypes') {
        this.handleAgentTypes(content);
      }
      else if(type == 'agentAIDs') {
        this.handleAIDS(content);
      }else if(type == 'collector') {
        this.handleCollector(content);
      }else if(type == 'books') {
        this.handleBooks(content);
      }else if(type == 'performatives') {
        this.handlePerformative(content);
      }
    }
  }

  handleAgentTypes(message : string) {
    console.log("tipovi agenata:"+ message);
    this.types = message.split('|');
    this.setUpDisplayedTypes();
  }

  setUpDisplayedTypes() {
    this.displayedTypes = [];


    for(var type of this.types)
    {
      var nameAndState = type.split(',');

      this.displayedTypes.push({'type':nameAndState[0], 'statefull':nameAndState[1]})
    }

  }


  handleAIDS(message : string) {
    console.log("svi agenti:"+ message);
    this.agents = message.split('|');
    console.log("agenti za ispis: "+this.agents );
    this.setUpDisplayedAgents();
  }

  setUpDisplayedAgents() {
    this.displayedAgents = [];


    for(var agent of this.agents)
    {
      var nameTypeHost = agent.split(',');

      this.displayedAgents.push({'name':nameTypeHost[0], 'type':nameTypeHost[1],'host': nameTypeHost[2] } )
    }

  }

  handleCollector(message : string) {
    console.log("od kollektora:"+ message);
    this.toasterService.show("Stigla je nova poruka od kolektora:"+message);
  }

  setUpDisplayedBooks() {
    this.displayedBooks= [];


    for(var book of this.books)
    {
      var nameAuthorPriceHref = book.split(',');

      this.displayedBooks.push({'name':nameAuthorPriceHref[0], 'author':nameAuthorPriceHref[1],'price': nameAuthorPriceHref[2],'href': nameAuthorPriceHref[3] } )
    }

  }

  handleBooks(message : string) {
    console.log("svi nadjene knjige:"+ message);
    this.books = message.split('|');
    this.setUpDisplayedBooks();
  }

  setUpDisplayedPerformatives() {
    this.displayedPerformatives= [];


    for(var p of this.performatives)
    {
      this.displayedPerformatives.push({'name':p } )
    }

  }

  handlePerformative(message : string) {
    console.log("svi agenti:"+ message);
    this.performatives = message.split('|');
    this.setUpDisplayedPerformatives();
  }

  send() {

    this.mservice.sendMessage(this.agentName, this.agentType, this.message, this.performative, "",this.store,this.authore,this.title)
    // if(this.selected==="ALL")
    // {
    //   alert("To send messages to all, click the button all");
    //   return;
    // }
    // console.log("selektovano: "+this.selected)
    // this.mservice.send(this.userService.getUsername(),this.selected, this.content,this.subject)

  }

  startAgent()
  {
    this.mservice.startAgent(this.agentName,this.agentType);

  }

  stopAgent()
  {
    this.mservice.stopAgent(this.agentName,this.agentType);
  }

  getPerformatives()
  {
    this.mservice.getPerformatives();



    
  }



  // setUpDisplayedUsers() {
  //   this.displayed = [];
  //   for(var user of this.registered) {
  //     if(user!=this.userService.getUsername()){
  //     if(this.isINLOGED(user)){
  //       this.displayed.push({'user':user, 'active':true})
  //       // console.log("dodat u listu logovanih " + user)
  //     }else {
  //       this.displayed.push({'user':user, 'active':false})
  //       // console.log("dodat u listu nelogovanih " + user)
  //     }
  //   }
  //   }
  //   this.displayed.push({'user':"ALL", 'active':true})
  //   if(this.selected == null && this.displayed.length > 0) {
  //     this.select(this.displayed[0]['user']);
  //   }
  // }


  ngOnInit(): void {
  }

}
class AgentDto {
  constructor(
      public command : string,
      public performative : boolean,
      public agentName : string,
      public agentType : string,
      public id : string,
      public hostAlias : string
  ) {}
}
