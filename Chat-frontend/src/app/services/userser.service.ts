import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserserService {

  public username : string;
  public password : string;
  constructor(private http : HttpClient) { 
    this.username = "";
    this.password = "";
  }

  getUsername()
  {
    return this.username;
  }

  login(username:any, password:any) {
    this.username = username;
    this.password = password;
    var data:any = new Object();
    data['username'] = username;
    data['password'] = password;
    data['id'] = localStorage.getItem("sessionId");
    console.log("Sessioin id za login je :"+localStorage.getItem("sessionId"));
    this.http.post('http://localhost:8080/Chat-war/api/chat/login', data).subscribe((result) => {
      console.log("Rezultat logina je :"+result);
      // let data2 = JSON.parse(result);
      // console.log(" Data je : "+data2);
    });
  }

  logout() {
    var data:any = new Object();
    data['username'] = this.username;
    data['password'] = this.password;
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      body: {
        username: this.username,
        password: this.password,
      },
    };
    this.http.delete('http://localhost:8080/Chat-war/api/chat/loggedIn', options).subscribe((result) => {
      console.log("Rezultat je :"+result);
    });
  }

  signup(username:any, password:any) {
    var data:any = new Object();
    data['username'] = username;
    data['password'] = password;
    data['id'] = localStorage.getItem("sessionId");
    console.log("Sessioin id je :"+localStorage.getItem("sessionId"));
    this.http.post('http://localhost:8080/Chat-war/api/chat/register', data, {headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
  }

  getRegistered() {
    console.log("Trazim registrovane Sessioin id je :"+localStorage.getItem("sessionId"));
    var data:String = localStorage.getItem("sessionId")||"";
    
    this.http.post('http://localhost:8080/Chat-war/api/chat/registered',data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
    // this.http.get('http://localhost:8080/Chat-war/api/chat/registered/'+ localStorage.getItem("sessionId")).subscribe();
  }

  getLoggedIn() {
    
    console.log("Trazim logovane Sessioin id je :"+localStorage.getItem("sessionId"));
    var data:String = localStorage.getItem("sessionId")||"";
    this.http.post('http://localhost:8080/Chat-war/api/chat/loggedIn',data,{headers : new HttpHeaders({ 'Content-Type': 'application/json' })}).subscribe();
    // this.http.get('http://localhost:8080/Chat-war/api/chat/loggedIn/'+ localStorage.getItem("sessionId")).subscribe();
  }

  isLoggedIn() {
    return localStorage.getItem('identifier') != null;
  }

}
