The Idea
---------
This project tries to replace WicketTester with a better approach. The biggest problem 
with WicketTester is that most of the code tries to literally hack the request processing 
to emulate browser functions such as clicking a link or submitting a form.

By using htmlunit we can completely elimiate all these hacks and simplify WicketTester 
by delegating all the client-side emulation hacks to hmltunit where the actual javascript 
code would be executed. WicketTester would then become a simple bridge between the user's code 
and the htmlunit runtime.

As a bonus, we can also test Wicket's javascript, something that was difficult in the past.

What there is now
-----------------
HtmlUnitTest which demonstrates that ajax links work

Todo
----
- Test the extent of HtmlUnit's compatibility by testing more advanced Ajax usecases
- See if its possible to improve performance
  - first test takes 1.7 seconds
    - com.gargoylesoftware.htmlunit.html.HTMLParser.parse takes 1.3 of those seconds
Done
----
- Remove the network - have a basic WicketTester like thing that can respond to URLs so all transfers are done in-memory to speed things up
  - Need to see how pluggable that side of html unit is, replacing their WebClient with our DirectLinkClient or something like that
  
