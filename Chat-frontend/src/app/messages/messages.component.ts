import { Component, Input, OnInit, SimpleChanges, ViewChild  } from '@angular/core';
import { MessagesService } from '../services/messages.service';
import { WebsocketService } from '../services/websocket.service';
import { UserserService } from '../services/userser.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {

  constructor(private messageService : MessagesService, private wsService : WebsocketService, private userService : UserserService, private toasterService: ToastrService) { }

  liveData$ = this.wsService.messages$;

  @Input() selected : string = "";

  public allMessages : dto[] = [];
  public displayedMessages : dto[] = [];

  public subject : string = "enter subject here";
  public content : string = "enter text here";

  displayedColumns: string[] = ['date', 'message'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  dataSource = new MatTableDataSource<dto>(this.displayedMessages);

  ngOnInit(): void {
    this.liveData$.subscribe({
      next : msg => this.handleMessage(msg as string)
    });
    this.wsService.connect();
    setTimeout(() => {this.messageService.getMessages()}, 500)
  }

  ngOnChanges(changes: SimpleChanges) {
    // this.allMessages = []
    this.messageService.getMessagesForUsername(this.selected)
    this.setUpDisplayedMessages();
  }

  setUpDisplayedMessages() {
    this.displayedMessages = [];
    this.filterMessagesBySelectedUser();
    console.log("Sve poruku su :");
    console.log(JSON.stringify(this.displayedMessages));
    
    this.dataSource.data = this.displayedMessages;
    console.log("Prikaz poruka na frontu:");
    console.log(JSON.stringify(this.dataSource.data));
    this.dataSource.paginator = this.paginator;
  }

  

  filterMessagesBySelectedUser() {
    console.log("Unutar filtera")
    console.log("all messages " + JSON.stringify(this.allMessages))
    for(var message of this.allMessages) {
      console.log("Poruka i je: " + JSON.stringify(message))
      console.log("Selektovan je: " + JSON.stringify(this.selected))
      if(message.otherUsername == this.selected) {
        this.displayedMessages.push(message);
        console.log("Poruka je dodata u display!")
      }
    }
  }

  handleMessage(message : string) {
    if(message.match('.+:.*')) {
      var type = message.split(':')[0];
      if(type == 'messages') {
        this.handleMessages(message.split(':')[1]);
      }
      else if(type == 'message') {
        this.handleNewMessage(message);
      }
    }
  }

  handleMessages(message : string) {
    console.log("Stigle poruke: " + message);
    var all = message.split('|');
    var split;
    let data:dto;
    console.log("sad su podeljenje: " + JSON.stringify(all));
    // sender+","+reciver+","+content+","+date+","+subject
    
    for (let msg of all)
    {
      split = msg.split(",")
      
      data = new dto(split[0],true,split[4],split[2],split[3])
      // console.log("dodaje se poruka: " + JSON.stringify(data));
      this.allMessages.push(data);
    }
    
    this.setUpDisplayedMessages();
  }

  handleNewMessage(message : string) {
    console.log("Stigla poruka: " + message);
    var all = message.split(',');
    let data:dto;
    // console.log("all is: " + JSON.stringify(all))
    let splitAgain = all[0].split(':');
    this.toasterService.show("Stigla je nova poruka od "+splitAgain[1]);
    data = new dto(splitAgain[1],true,all[4],all[2],all[3])
    // console.log(JSON.stringify(data))
    this.allMessages.push(data);
    // console.log(JSON.stringify(this.allMessages))
    this.setUpDisplayedMessages();
  }

  send() {
    if(this.selected==="ALL")
    {
      alert("To send messages to all, click the button all");
      return;
    }
    console.log("selektovano: "+this.selected)
    this.messageService.send(this.userService.getUsername(),this.selected, this.content,this.subject)
    this.subject = ""
    this.content = ""
  }

  sendToAll() {
    this.messageService.sendToAll(this.userService.getUsername(),this.subject, this.content)
    this.subject = ""
    this.content = ""
  }

}

class dto {
  constructor(
      public otherUsername : string,
      public incoming : boolean,
      public subject : string,
      public content : string,
      public dateTime : string
  ) {}
}
