import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Parallel Development Pattern: TaskPool
 * A TaskPool is composed by the Thread Master, which runs on the main function, and is responsible for the program execution.
 * It also contains Worker Threads which basically take care of the rest of the program execution.
 * <p>
 * The tasks are stored initially on an arraylist.
 * Then a task is assigned to each Worker Thread.
 * */
public class TaskPool {
    private final List<Runnable> taskList;
    private final Lock lock;

    /**
     * The array of worker threads in the task pool.
     */
    protected final WorkerThread[] workers;

    /**
     * Constructor for the TaskPool Pattern.
     * @param poolSize Constructs the number of Thread Workers to be generated in the pool.
     */
    public TaskPool(int poolSize) {
        this.taskList = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.workers = new WorkerThread[poolSize];


        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    /**
     * @param task Executes the Runnable task
     * */
    public void submitTask(Runnable task) {
        lock.lock();
        try {
            taskList.add(task);
            //
        } finally {
            lock.unlock();
        }
    }

    /**
     * Class WorkerThread which extends Thread
     * The run() function ensures that all the Worker Threads are running until all tasks are done
     * Once they finish all the remaining tasks, then the Worker Threads end by the shutdown() function.
     * */
    protected class WorkerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task = null;
                lock.lock();
                try {
                    if (!taskList.isEmpty()) {
                        task = taskList.remove(0);
                    }
                } finally {
                    lock.unlock();
                }

                if (task != null) {
                    task.run();
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * Interrupts all the Threads from the TaskPool
     * */
    public void shutdown() {
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }
}
