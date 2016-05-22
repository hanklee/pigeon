/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 01:27
 */
package com.lixianling.pigeon;

import com.lixianling.pigeon.concurrent.ParallelSystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xianling Li(hanklee)
 *         $Id: ProcessSystem.java 67 2016-04-03 18:18:36Z hank $
 */
public final class ProcessSystem {

    private final Map<Pid, PigeonProcess> pidProcessMap;
    private static ProcessSystem INSTANCE = null;


    public static void init(int threadWorkers) {
        if (INSTANCE == null) {
            INSTANCE = new ProcessSystem(threadWorkers);
        }
    }

    public static ProcessSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProcessSystem(ParallelSystem.DEFAULT_WORKERS);
        }
        return INSTANCE;
    }


    private ProcessSystem(int threadWorkers) {
        ParallelSystem.getInstance(threadWorkers); // inital the parallel system
        pidProcessMap = new ConcurrentHashMap<Pid, PigeonProcess>();
    }

    public Pid register(PigeonProcess process) {
        pidProcessMap.put(process.getPid(), process);
        return process.getPid();
    }


    public void unregister(Pid pid) {
        pidProcessMap.remove(pid);
    }

    public void sendMessage(final Pid pid, final Message message) {
        final PigeonProcess process = pidProcessMap.get(pid);
        process.sendMessage(message);
    }

    public void shutdown(){
        try {
            ParallelSystem.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
