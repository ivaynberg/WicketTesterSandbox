The Idea
---------
This project tries to replace WicketTester with a better approach. The biggest problem with WicketTester is that most of the code tries to literally hack the request to emulate browser functions such as clicking a link or submitting a form. Even worse, there are a lot of hacks to emulate the same functionality when executed via AJAX.
By using htmlunit we can completely elimiate all these hacks and simplify WicketTester by delegating all the client-side emulation hacks to hmltunit where the actual javascript code would be executed. WicketTester would then become a simple bridge between the user's code and the htmlunit runtime.

What there is now
-----------------
HtmlUnitTest which demonstrates that ajax links work

Todo
----
- Test the extent of HtmlUnit's compatibility by testing more advanced Ajax usecases
- Remove the network - have a basic WicketTester like thing that can respond to URLs so all transfers are done in-memory to speed things up
  - Need to see how pluggable that side of html unit is, replacing their WebClient with our DirectLinkClient or something like that
  
Old
---
-By using env-js we can completely elimiate all these hacks and simplify WicketTester by delegating all the client-side emulation hacks to env-js where the actual code would be executed. WicketTester would then become a simple bridge between the user's code and the env-js runtime.-
-The current problem is that env-js is incomplete in some places and buggy in others. It does not yet properly support XHR which is a blocker for this project.-
-If you wish to play with this you have to use my env-js clone and run RhinoTest unit test.-
