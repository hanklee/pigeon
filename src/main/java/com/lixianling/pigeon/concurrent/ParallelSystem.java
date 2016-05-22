/**
 * Copyright 2015 The pigeon Project
 * Created Date: 2016-04-03 00:43
 */
package com.lixianling.pigeon.concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;

/**
 * @author Xianling Li(hanklee)
 *         $Id: ParallelSystem.java 68 2016-04-05 16:08:00Z hank $
 */
public class ParallelSystem {

    public static final int DEFAULT_WORKERS;

    static {
        DEFAULT_WORKERS = Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    private final Queue<Order> queue;
    private final Queue<Worker> workerLine;
    private final int workerNum;

    private int idx;
//    private volatile boolean accept;

    private static ParallelSystem INSTANCE = null;

    public static ParallelSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ParallelSystem(DEFAULT_WORKERS);
        }
        return INSTANCE;
    }

    public static ParallelSystem getInstance(int num) {
        if (INSTANCE == null) {
            INSTANCE = new ParallelSystem(num);
        }
        return INSTANCE;
    }

    /**
     * public method
     */

    private ParallelSystem(int workerNum) {
        queue = new UniqueQueue<Order>();
        workerLine = new LinkedList<Worker>();
        this.workerNum = workerNum;
        for (int i = 0; i < workerNum; i++) {
            Worker worker = new Worker(this);
            workerLine.add(worker);
        }
        idx = 0;
//        accept = true;
    }

    public void shutdown() throws Exception {
        while (queue.size() > 0 || workerLine.size() != workerNum) {
            Thread.sleep(100);
        }

        // todo may be race condition
        for (Worker worker : workerLine) {
            worker.stop();
        }
    }

    public synchronized int getIdx() {
        idx++;
        if (idx <= 0) idx = 1;
        return idx;
    }

    public void order(final Order order) {
        reception(order, null);
    }


    /**
     * get worker a job or worker release a job
     *
     * @param order
     * @param worker
     * @return
     */
    private synchronized boolean reception(Order order, Worker worker) {
        boolean wakeup = true;
        if (order != null && worker != null) {
            worker.setOrder(null);
            order.setWorker(null);

            order = next();
            if (order == null) {
                workerLine.add(worker);
                return false;
            }

            wakeup = false; // no need wake up for worker who is not snooze

        } else if (order != null) {

            if (order.getWorker() != null) {
                queue.add(order);
                return false;
            }

            worker = workerLine.poll();
            if (worker == null) {
                queue.add(order);
                return false;
            }
        } else {
            return false;
        }

        worker.setOrder(order);
        order.setWorker(worker);
        if (wakeup) {
            worker.wakeup();
        }
        return true;
    }


    private Order next() {
        int queueSize = queue.size();
        if (queueSize > 0) {
            Order order = queue.poll();
            int rIndex = 1;

            while (order.getWorker() != null && rIndex < queueSize) {
                queue.add(order); // not remove
                order = queue.poll();
                rIndex++;
            }

            if (order.getWorker() != null) {
                queue.add(order); // not remove
                return null;
            }
            return order;
        }
        return null;
    }


    /**
     * Worker class
     */

    private static class Worker implements Runnable {
        private final Thread thread;
        private volatile boolean alive;
        private volatile boolean death;
        private volatile long touch;
        private volatile Order order;

        private final ParallelSystem factory;

        private Worker(ParallelSystem factory) {
            alive = true;
            this.factory = factory;
            thread = Executors.defaultThreadFactory().newThread(this); //new Thread(this);
            thread.start();
        }

        protected Order getOrder() {
            return order;
        }

        protected void setOrder(Order order) {
            this.order = order;
        }

        @Override
        public void run() {
            touch = System.currentTimeMillis();
            while (alive) {
                try {
                    touch = System.currentTimeMillis();
                    if (order != null)
                        order.execute();
                } catch (Exception e) {
                    order.error(e);
                } finally {
                    if (!factory.reception(order, this)) {
                        snooze();
                    }
                }
            }
        }

        private void snooze() {
//        if (!alive) return;
            synchronized (thread) {
                try {
                    while (order == null && alive) {
                        thread.wait();
                        // this run() method will be run again when execute fast
                    }
//                if (order != null) {
//                    thread.notify();
//                }
                } catch (InterruptedException e) {
                    if (order != null) {
                        order.error(e);
                    }
                }
            }

        }

        private void wakeup() {
            synchronized (thread) {
                thread.notify();
            }
        }

        private void stop() {
            alive = false;
            synchronized (thread) {
                thread.notify();
            }

        }
    }

    /**
     * @author Xianling Li(hanklee)
     *         $Id: ParallelSystem.java 68 2016-04-05 16:08:00Z hank $
     */
    public abstract static class Order implements Link {
        private Worker worker;

        private Worker getWorker() {
            return worker;
        }

        private void setWorker(Worker worker) {
            this.worker = worker;
        }

        public abstract void execute();

        public abstract void error(Exception e);
    }
}
