/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 01:50
 */
package com.lixianling.pigeon;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: TestMessage.java 67 2016-04-03 18:18:36Z hank $
 */
public class TestMessage {

    public static void main(String[] args) {

        PigeonProcess process = new PigeonProcess() {
            @Override
            public void receive(Message message) {
                System.out.println(Thread.currentThread()+",process 1:"+message.getContent());
//                ProcessSystem.getInstance().sendMessage(message.getFrom(),
//                        new Message(this.getPid(),0,"send from process1"));

            }


            @Override
            public void error(Exception e) {
                // nothing to do
            }

            @Override
            public void error(Exception e, Message message) {

            }
        };

        PigeonProcess process1 = new PigeonProcess() {
            @Override
            public void receive(Message message) {
                System.out.println(Thread.currentThread()+",process 2:"+ message.getContent());
                ProcessSystem.getInstance().sendMessage(message.getFrom(),
                        new Message(this.getPid(),0,"send from process2"));


            }

            @Override
            public void error(Exception e) {
                 // nothing to do
            }

            @Override
            public void error(Exception e, Message message) {

            }
        };

        ProcessSystem system = ProcessSystem.getInstance();
        Pid pid1 = system.register(process);
        Pid pid2 = system.register(process1);

        system.sendMessage(pid1,new Message(pid2,0,"hello"));
        system.sendMessage(pid2,new Message(pid1,0,"hello world"));

        system.shutdown();

    }
}
