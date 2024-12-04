# Java project as part of ENSEA curriculum       
   
We implemented all the features from the layer 7 (the possibility to each user to configure a name with #nickname, the possibility to send a message to a specific user with @username, to obtain statistics on you own activity with #stat, or to ban aan other user with #ban username).    
Each user have to define a name with _#nickname_, then can send messages to all users, or send message to a specific user with @username.


### How to use it    

choose a name (or use the default name that is your ip)

    #nickname Werrataz

message sent to all users connected to the server

    Hello all, how are you ?

message sent to Martin. Martin can answer to Werrataz by enter "@Werrataz the results last week was very promising !"

    @Martin how about the project ?

permit to obtain statistics about the messages you sent

    #stat 

permit to block all messages from Martin

    #ban Martin


### How to install    

To install, you just have to clone, then launch the TCPMultiServer then the TCPClient. You can use the IP 127.0.0.1 if you test both client and server in the same machine.    
