/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 00:42
 */
package com.lixianling.pigeon.concurrent;

import java.util.BitSet;
import java.util.LinkedList;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: UniqueQueue.java 67 2016-04-03 18:18:36Z hank $
 */
public class UniqueQueue<T extends Link> extends LinkedList<T> {

    private BitSet sets;

    public UniqueQueue() {
        sets = new BitSet();
    }

    public BitSet sets() {
        return this.sets;
    }


    @Override
    public boolean add(T link) {
        boolean myset = sets.get(link.index());
        if (!myset) {
            sets.set(link.index());
            return super.add(link);
        } else
            return false;
    }

    @Override
    public T poll() {
        T tmp = super.poll();
        if (tmp != null) {
            sets.clear(tmp.index());
        }
        return tmp;
    }

}
