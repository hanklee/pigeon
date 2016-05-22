

Pigeon 

Java message framework like erlang pass message through the process.



API

Send_message(Pid, Message), or Pid.send_message(Message)

Pid = MessageSystem.register(Process).


Interface Process method

Handle_Message(Message) 


Result = Handle_call(Message)

Reception(mode)


Reason

从java服务端转erlang有一段日子，这段时间学习了erlang如何处理高并发. 个人认为erlang比java更适合写服务器程序(特别是新手)，主要有以下几个原因:


1.erlang可以开成千上万的进程，性能没有影响；Java如果开着一个一直运行的线程开销太大了，只能用线程池去做任务。

2.erlang进程间通信简单而强大，Java线程通信没有一个共识的机制，很多都是自己去实现而且很多实现的都不稳定。

3.写erlang你基本不会担心race condition，而java，写多线程程序每个变量都要想想会会不会导致race condition。

4.erlang热更新太方便了
  

总体来说，erlang是在语言层面来处理高并发。的确erlang出生的原因都是为着高并发而去的，所以erlang在处理高并发比java有优势。


用Java写高并发程序不难，难在于程序的正确性。由于java多线程程序一不小心比较容易出现race condition，当处理小并发时，程序是没问题的；而高并发时，就出现意外的结果，而去很多时候出现问题还带概率，所以找比较难再现的bug是个头疼的问题。
erlang基本很少要面对race condition的问题。因为erlang不允许别的进程获取修改自己的数据，每个进程自己的数据都是独立的。erlang是怎样获取和修改数据，通过进程间的通讯，我觉得java的多线程解决方案可以学习一下erlang的这种思想。所以我自己写了一个类似erlang进程间消息通信的库 pigeon。

这个库的进程严格来说不是进程，是一个消息驱动的类。当收到消息时，这个类会放到线程池里运行，处理收到的消息。基于这个框架，以后写java的高并发程序时，基本无须共享各个模块的数据，用消息来进行替换。


