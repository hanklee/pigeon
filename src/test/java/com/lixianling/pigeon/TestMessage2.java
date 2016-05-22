/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-04 00:02
 */
package com.lixianling.pigeon;

/**
 * @author Xianling Li(hanklee)
 *         $Id: TestMessage2.java 67 2016-04-03 18:18:36Z hank $
 */
public class TestMessage2 {

    public static void main(String[] args) {

        PigeonProcess process = new UnsafePigeonProcess() {
            @Override
            public void receive(Message message) {
                System.out.println(Thread.currentThread() + ",process 1:" + message.getContent());

                setWaitForSignal(10);
                sendPidMessage(message.getFrom(),
                        new Message(this.getPid(), "send from process1"));


            }


            @Override
            public void error(Exception e) {
                // nothing to do
            }
        };

        PigeonProcess process1 = new UnsafePigeonProcess() {

            boolean isFisrst = true;

            @Override
            public void receive(Message message) {
                System.out.println(Thread.currentThread() + ",process 2:" + message.getContent());

                if (isFisrst) {

                    sendPidMessage(message.getFrom(),
                            new Message(this.getPid(), "send from process2 singal -1 #1"));

                    sendPidMessage(message.getFrom(),
                            new Message(this.getPid(), "send from process2 singal -1 #2"));

                    sendPidMessage(message.getFrom(),
                            new Message(this.getPid(), "send from process2 singal -1 #3"));

                    sendPidMessage(message.getFrom(),
                            new Message(this.getPid(), 10, "send from process2 singal 10 #1"));

                    // send to my self
                    sendPidMessage(this.getPid(),
                            new Message(this.getPid(), 10, "send from process2 singal 10 #2"));

                    isFisrst = false;
                }
            }

            @Override
            public void error(Exception e) {
                // nothing to do
            }
        };

        ProcessSystem system = ProcessSystem.getInstance();
        Pid pid1 = system.register(process);
        Pid pid2 = system.register(process1);

        system.sendMessage(pid1, new Message(pid2, 0, "hello"));
//        system.sendMessage(pid2, new Message(pid1, 0, "hello world"));

        system.shutdown();
    }
}
