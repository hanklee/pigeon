/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 23:43
 */
package com.lixianling.pigeon;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * I think this class is not reliable for the wait signal never come.
 *
 * @author Xianling Li(hanklee)
 *         $Id: UnsafePigeonProcess.java 67 2016-04-03 18:18:36Z hank $
 */
public abstract class UnsafePigeonProcess extends PigeonProcess {

    private volatile int waitSignal;
    protected final Queue<Message> backupData;

    public UnsafePigeonProcess() {
        super();
        this.backupData = new ConcurrentLinkedQueue<Message>();
        waitSignal = -1;
    }

    protected void setWaitForSignal(int signal) {
        this.waitSignal = signal;
    }

    public void sendMessage(Message message) {
        if (waitSignal == -1 || message.getSignal() == waitSignal) {
            waitSignal = -1;
            super.sendMessage(message);
            for (Message tmp = this.backupData.poll(); tmp != null; tmp = this.backupData.poll()) {
                super.sendMessage(tmp);
            }
        } else {
//            System.out.println("backup...");
            this.backupData.add(message);
        }
    }

    public void error(Exception e, Message message) {
        if (message.getSignal() != -1) {
            sendPidMessage(message.getFrom(), new Message(this.getPid(),
                    message.getSignal(), e.getMessage()));
        }
    }

}
