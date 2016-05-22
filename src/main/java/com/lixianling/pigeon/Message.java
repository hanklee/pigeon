/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 00:54
 */
package com.lixianling.pigeon;

/**
 * @author Xianling Li(hanklee)
 *         $Id: Message.java 67 2016-04-03 18:18:36Z hank $
 */
public class Message {

    private final Pid from;
    private final int signal;
    private final Object content;

    public Message(Pid from, Object content) {
        this(from, -1, content);
    }

    public Message(Pid from, int signal, Object content) {
        this.from = from;
        this.signal = signal;
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public int getSignal() {
        return signal;
    }

    public Pid getFrom() {
        return from;
    }
}
