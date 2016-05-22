/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 00:47
 */
package com.lixianling.pigeon;

import com.lixianling.pigeon.concurrent.ParallelSystem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Xianling Li(hanklee)
 *         $Id: PigeonProcess.java 67 2016-04-03 18:18:36Z hank $
 */
public abstract class PigeonProcess extends ParallelSystem.Order {

    private int idx;
    protected final Queue<Message> data;
    private Pid pid;
    private final ParallelSystem parallelSystem;
    private final ProcessSystem processSystem;

    public PigeonProcess() {
        idx = ParallelSystem.getInstance().getIdx();
        this.data = new ConcurrentLinkedQueue<Message>();
        processSystem = ProcessSystem.getInstance();
        parallelSystem = ParallelSystem.getInstance();
    }

    public void sendMessage(Message message) {
        this.data.add(message);
        parallelSystem.order(this);
    }

    protected void sendPidMessage(Pid pid,Message message) {
        processSystem.sendMessage(pid,message);
    }

    @Override
    public int index() {
        return idx;
    }

    protected final Pid getPid() {
        if (pid == null) {
            pid = new Pid(idx);
        }
        return pid;
    }


    @Override
    public final void execute() {
        for (Message tmp = this.data.poll(); tmp != null; tmp = this.data.poll()) {
            try {
                receive(tmp);
            } catch (Exception e) {
                error(e, tmp);
            }
        }
    }

    /**
     *
     * this exception is throw in parallel system.
     *
     * @param e Exception
     */
    @Override
    public void error(Exception e) {
        e.printStackTrace();
    }

    public abstract void error(Exception e, Message message);

    public abstract void receive(Message message);

}
