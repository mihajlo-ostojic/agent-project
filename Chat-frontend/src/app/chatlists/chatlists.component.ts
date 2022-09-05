import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chatlists',
  templateUrl: './chatlists.component.html',
  styleUrls: ['./chatlists.component.css']
})
export class ChatlistsComponent implements OnInit {

  public selected : string = "";

  constructor() { }

  ngOnInit(): void {
  }

  receiveMessage($event:any) {
    this.selected = $event
  }
}
