package thread;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPool {


    private class PoolThread extends Thread {
        {
            setDaemon(true);
            start();
        }
        Runnable r;

        @Override
        public void run() {
            OUTER: while (!dead.get()) {
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && !dead.get()) {
                        try {
                            taskQueue.wait();
                        } catch (final InterruptedException e) {
                            if (dead.get())
                                break OUTER;
                            Thread.currentThread().interrupt();
                        }
                    }
                    r = taskQueue.remove();
                }
                try {
                    r.run();
                } catch (final Throwable t) {
                    t.printStackTrace();
                    //System.out.println("Task failed");
                }
                finally {

                }
                synchronized (inProcess) {
                    inProcess.decrementAndGet();
                    inProcess.notifyAll();
                }
            }
        }
    }

    public static int defaultMaxTasks(final int threads) {
        return threads * 100;
    }

    public static int defaultNumberOfThreads() {
        return Runtime.getRuntime().availableProcessors() + 1;
    }

    private final Deque<Runnable> taskQueue = new ArrayDeque<>();
    private final int limit;
    public final boolean fifo;
    private final AtomicBoolean dead = new AtomicBoolean(false);
    private final AtomicBoolean flushing = new AtomicBoolean(false);

    private final Deque<PoolThread> pool;

    private final AtomicInteger inProcess = new AtomicInteger(0);
    public final boolean limitless;

    public ThreadPool() {
        this(defaultNumberOfThreads());
    }


    public ThreadPool(final int threads) {
        this(threads, defaultMaxTasks(threads), true, false);
    }


    public ThreadPool(final int threads, final int maxTasks) {
        this(threads, maxTasks, true, false);
    }

    ThreadPool(final int threads, final int maxTasks, final boolean fifo, final boolean limitless) {
        if (threads < 1)
            throw new ThreadPoolException("thread count must be positive");
        if (maxTasks < threads)
            throw new ThreadPoolException("maxTasks must be greater than or equal to thread count");
        pool = new ArrayDeque<>(threads);
        limit = maxTasks;
        this.fifo = fifo;
        this.limitless = limitless;
        for (int i = 0; i < threads; i++) {
            pool.add(new PoolThread());
        }
    }


    public void die() {
        dead.set(true);
        for (final PoolThread pt : pool)
            pt.interrupt();
    }

    public void flush() {
        flushing.set(true);
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
        finish();
        if (dead.get())
            return;
        synchronized (flushing) {
            flushing.set(false);
            flushing.notifyAll();
        }
    }

    public void finish() {
        synchronized (inProcess) {
            while (inProcess.get() > 0) {
                try {
                    // wakes up once a second to facilitate debugging
                    inProcess.wait(1000);
                } catch (final InterruptedException e) {
                    if (dead.get())
                        break;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public int getPriority() {
        if (dead.get())
            throw new ThreadPoolException("pool is dead!");
        return pool.getFirst().getPriority();
    }


    public void run(final Runnable r) {
        run(r, fifo);
    }


    public void run(final Runnable r, final boolean fifo) {
        if (dead.get())
            throw new ThreadPoolException("pool is dead!");
        synchronized (flushing) {
            while (flushing.get()) {
                try {
                    flushing.wait();
                } catch (final InterruptedException e) {
                    if (dead.get())
                        return;
                    Thread.currentThread().interrupt();
                }
            }
        }
        synchronized (inProcess) {
            if (!limitless) {
                while (inProcess.get() == limit) {
                    try {
                        inProcess.wait();
                    } catch (final InterruptedException e) {
                        if (dead.get())
                            return;
                        Thread.currentThread().interrupt();
                    }
                }
            }
            inProcess.incrementAndGet();
            synchronized (taskQueue) {
                if (fifo)
                    taskQueue.addLast(r);
                else
                    taskQueue.addFirst(r);
                //System.out.println("called");
                //taskQueue.notifyAll();
            }
            inProcess.notifyAll();
        }
    }


    public void setPriority(final int priority) {
        if (dead.get())
            throw new ThreadPoolException("pool is dead!");
        for (final Thread t : pool)
            t.setPriority(priority);
    }

    static class ThreadPoolException extends RuntimeException {
        public ThreadPoolException(String s) {
            super(s);
        }

        private static final long serialVersionUID = 1L;
    }
}

