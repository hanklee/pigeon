/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 00:58
 */
package com.lixianling.pigeon;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: Pid.java 67 2016-04-03 18:18:36Z hank $
 */
public class Pid {

    private final int idx;

    protected Pid(int idx){
        this.idx = idx;
    }


    @Override
    public int hashCode() {
        return idx;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pid) {
            return this.idx == ((Pid)obj).idx;
        }
        return false;
    }
}
