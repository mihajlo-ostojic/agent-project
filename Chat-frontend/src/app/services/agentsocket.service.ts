import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { catchError, tap, switchAll, share } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgentsocketService {
  private WS_ENDPOINT : string = 'ws://localhost:8080/Chat-war/wsagent/';

  private socket$!: WebSocketSubject<any>;
  private messagesSubject$ = new Subject<any>();
  public messages$ = this.messagesSubject$.pipe(switchAll(), share(), catchError(e => { throw e }));

  constructor() { }

  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = this.getNewWebSocket();
      const messages = this.socket$.pipe(
        tap({
          error: error => console.log(error),
        }), catchError(_ => EMPTY));
      this.messagesSubject$.next(messages);
      this.socket$.subscribe({
        complete: () => { localStorage.removeItem('agentSessionId') }
      })
    }
  }

  private getNewWebSocket() {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (var i = 0; i < 10; i++){
       text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    // var text = "chat";
    localStorage.setItem("agentSessionId", text);
    return webSocket({url: this.WS_ENDPOINT+localStorage.getItem("agentSessionId"), deserializer: msg => msg.data});
  }

  sendMessage(msg: any) {
    this.socket$.next(msg);
  }

  close() {
    this.socket$.complete();
    localStorage.removeItem('agentSessionId');
  }
}
