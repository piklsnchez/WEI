'use strict';
class Wei {
    //dao
    //menu
    //article
    //calendar
    constructor(){
        this.dao      = new Dao();
        this.menu     = new Menu();
        this.article  = null;
        this.calendar = null;
        
        this.dao.getUsers((json) => {
            this.menu.users.list = json.users;
            this.drawUserMenu();
        });
        this.dao.getSessions((json) => {
            this.menu.sessions.list = json.sessions;
            this.drawSessionMenu();
        });
        this.dao.getSessionNotes((json) => {
            this.article = new Article(json.sessions);
            this.drawArticle();
        });
        this.dao.getDates((json) => {
            this.calendar = new Calendar(json.planningDates);
            this.drawCalendar();
        });
    }
    
    static main(){
        let wei = new Wei();
    }
    
    drawUserMenu(){
        let oldUserElement = document.querySelector("#menu .items .users");
        let userElement    = oldUserElement.cloneNode(false);
        oldUserElement.parentNode.replaceChild(userElement, oldUserElement);
        this.menu.users.list.forEach(u => {
            let li = document.createElement("li");
            li.className += "expandable";
            li.setAttribute("tabindex", "-1");
            li.appendChild(document.createTextNode(u.firstName + " " + u.lastName));
            userElement.appendChild(li);
            let ul = document.createElement("ul");
            //description
            let descLi = document.createElement("li");
            descLi.appendChild(document.createTextNode(u.description));
            ul.appendChild(descLi);
            //location
            let locLi = document.createElement("li");
            locLi.appendChild(document.createTextNode(u.location));
            ul.appendChild(locLi);
            //phone
            let phoneLi = document.createElement("li");
            phoneLi.appendChild(document.createTextNode(u.phone));
            ul.appendChild(phoneLi);
            //email
            let emailLi = document.createElement("li");
            emailLi.appendChild(document.createTextNode(u.email));
            ul.appendChild(emailLi);
            li.appendChild(ul);
        });
    }
    
    drawSessionMenu(){
        let oldSessionElement = document.querySelector("#menu .items .sessions");
        let sessionElement    = oldSessionElement.cloneNode(false);
        oldSessionElement.parentNode.replaceChild(sessionElement, oldSessionElement);
        this.menu.sessions.list.forEach(s => {
            let li = document.createElement("li");
            li.className += "expandable";
            li.setAttribute("tabindex", "-1");
            li.appendChild(document.createTextNode(s.name + ": " + s.title));
            if(s.mcs){
                let ul = document.createElement("ul");
                s.mcs.forEach(mc => {
                    let li = document.createElement("li");
                    li.appendChild(document.createTextNode(mc.firstName + " " + mc.lastName));
                    ul.appendChild(li);
                });
                li.appendChild(ul);
            }
            sessionElement.appendChild(li);
        });
    }
    
    drawArticle(){
        let oldArticleElement = document.querySelector("#article");
        let articleElement    = oldArticleElement.cloneNode(false);
        oldArticleElement.parentNode.replaceChild(articleElement, oldArticleElement);
        this.article.list.forEach(a => {
            let p = document.createElement("p");
            let titleElement = document.createElement("h3");
            titleElement.setAttribute("class", "title");
            titleElement.appendChild(document.createTextNode(a.title));
            p.appendChild(titleElement);
            articleElement.appendChild(p);
            a.notes.forEach(n => {
                let noteElement = document.createElement("span");
                noteElement.setAttribute("contenteditable", "true");
                noteElement.setAttribute("data-id", n.id);
                noteElement.addEventListener("contextmenu", e => {
                    e.preventDefault();
                    let save = window.confirm("Save section?");
                    if(save){
                        this.updateNote(e);
                    }
                });
                noteElement.appendChild(document.createTextNode(n.note));
                p.appendChild(noteElement);
            });
        });
    }
    
    updateNote(e){
        let noteElement = e.target;
        let id = noteElement.getAttribute("data-id");
        let content = noteElement.innerHTML;
        /*console.log(id);
        console.log(content);*/
        this.dao.updateNote(id, content);
    }
    
    drawCalendar(){
        let oldDateElement = document.querySelector("#calendar .planningDates");
        let dateElement    = oldDateElement.cloneNode(false);
        oldDateElement.parentNode.replaceChild(dateElement, oldDateElement);
        this.calendar.list.forEach(d => {
            let li = document.createElement("li");
            let spanElement = document.createElement("span");
            spanElement.setAttribute("class", "date");
            spanElement.appendChild(document.createTextNode(d.date + ": "));
            li.appendChild(spanElement);
            li.appendChild(document.createElement("br"));
            li.appendChild(document.createTextNode(d.title));
            dateElement.appendChild(li);
        });
    }
}

class Menu {
    //users
    //sessions
    constructor(){
        this.users    = {"title":"MEMBERS","list":[]}
        this.sessions = {"title":"TOPICS", "list":[]}
    }
}

class Article {
    //title
    //list
    constructor(list){
        this.title = "";
        this.list  = list;
    }
}

class Calendar{
    //title
    //list
    constructor(list){
        this.title = "CALENDAR";
        this.list  = list;
    }
}

class Dao{
    constructor(){
    }
    
    proccesResult(e){
        console.log(e.responseText);
    }
    
    getUsers(func){
        let ajax = new XMLHttpRequest();
        ajax.onload = e => func(JSON.parse(e.target.responseText));
        ajax.open("GET", "/wei/users/info");
        ajax.send(null);
    }
    
    getSessions(func){
        let ajax = new XMLHttpRequest();
        ajax.onload = e => func(JSON.parse(e.target.responseText));
        ajax.open("GET", "/wei/sessions/facilitators");
        ajax.send(null);
    }
    
    getSessionNotes(func){
        let ajax = new XMLHttpRequest();
        ajax.onload = e => func(JSON.parse(e.target.responseText));
        ajax.open("GET", "/wei/sessions/notes");
        ajax.send(null);
    }
    
    updateNote(id, note){
        let ajax = new XMLHttpRequest();
        ajax.open("POST", `/wei/sessions/note/${id}`);
        ajax.send(note);
    }
    
    getDates(func){
        let ajax = new XMLHttpRequest();
        ajax.onload = e => func(JSON.parse(e.target.responseText));
        ajax.open("GET", "/wei/planning_dates");
        ajax.send(null);
    }
}

Wei.main();