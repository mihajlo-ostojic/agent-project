import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UserserService } from '../services/userser.service';
import { WebsocketService } from '../services/websocket.service';
@Component({
  selector: 'app-userlist',
  templateUrl: './userlist.component.html',
  styleUrls: ['./userlist.component.css']
})
export class UserlistComponent implements OnInit {

  liveData$ = this.wsService.messages$;

  private loggedIn : string[] = [];
  private registered : string[] = [];
  public displayed : any[] = [];//object

  @Output() messageEvent = new EventEmitter<string>();
  public selected : string = "";

  constructor(private wsService : WebsocketService, private userService : UserserService) { 
    this.liveData$.subscribe({
      next : msg => this.handleMessage(msg as string)
    });
    this.wsService.connect();
    setTimeout(() => {this.userService.getLoggedIn(); this.userService.getRegistered();}, 500)
  }

  ngOnInit(): void {
    
  }

  handleMessage(message : string) {
    console.log("stigla poruka: "+message);
    if(message.match('.+:.*')) {
      var type = message.split(':')[0];
      var content = message.split(':')[1];
      if(type == 'loggedInList') {
        this.handleLoggedInList(content);
      }
      else if(type == 'registeredList') {
        this.handleRegisteredList(content);
      }
    }
  }

  handleLoggedInList(message : string) {
    console.log("logovani korisnici:"+ message);
    this.loggedIn = message.split('|');
    this.setUpDisplayedUsers();
  }

  handleRegisteredList(message : string) {
    console.log("registrovani korisnici:"+ message);
    this.registered = message.split('|');
    this.setUpDisplayedUsers();
  }

  setUpDisplayedUsers() {
    this.displayed = [];
    for(var user of this.registered) {
      if(user!=this.userService.getUsername()){
      if(this.isINLOGED(user)){
        this.displayed.push({'user':user, 'active':true})
        // console.log("dodat u listu logovanih " + user)
      }else {
        this.displayed.push({'user':user, 'active':false})
        // console.log("dodat u listu nelogovanih " + user)
      }
    }
    }
    this.displayed.push({'user':"ALL", 'active':true})
    if(this.selected == null && this.displayed.length > 0) {
      this.select(this.displayed[0]['user']);
    }
  }

  isINLOGED(usename:any): boolean{
    // console.log("proverava za "+ usename)
    for(var u in this.loggedIn)
    {
      if(this.loggedIn[u]===usename){
        // console.log("jeste logovan "+ usename)
        return true;
      }
    }
    return false;
  }

  onChange(val:any) {
    console.log(JSON.parse(val));
}

  sendMessage() {
    console.log("dogadjaj obavestavanja selekcije ")
    console.log(JSON.stringify(this.selected))
    this.messageEvent.emit(this.selected)
  }

  select(user : string) {
    console.log("selektovan korisnik:" + user)
    this.selected = user;
    this.sendMessage();
  }

}
