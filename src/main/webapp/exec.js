Envjs({
    scriptTypes : {
        '': true, //inline and anonymous
        'text/javascript': true,
        'text/envjs': false
    }
});

function counter() {
	return document.getElementById('counter').innerHTML;
}

function checkcounter(val) {
	if (counter()!=val) throw "counter should be: "+val+" but was: "+counter();
}


window.location='http://localhost:8080/wicket/bookmarkable/com.vaynberg.tester.HomePage';

Wicket.Log = { 

		enabled: function() {
			return true;
		},
		
		info: function(msg) {
		    print("INFO: "+msg);
		},
		
		error: function(msg) {
			print("ERROR: "+msg);
		},  

		log: function(msg) {
			print("LOG: "+msg);
		}
	}

checkcounter("0");

link=document.getElementById('link');

if (link==null) { throw "link not found"; }

// dispatch mouse click event
var evt=document.createEvent("MouseEvents");
evt.initEvent("click",true,true);
link.dispatchEvent(evt);
Envjs.wait(); //not sure if this is needed

checkcounter("1");


