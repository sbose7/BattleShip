package JobProcessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Runs a {@link Consumer} on its own thread with its own inbox queue. The
 * {@link Coordinator} dispatches jobs to a worker's inbox; the worker
 * processes them one at a time and completes each job's result future.
 */
public class ConsumerWorker implements Runnable {
    private final String name;
    private final Consumer consumer;
    private final BlockingQueue<Job> inbox = new LinkedBlockingQueue<>();
    private final Thread thread;
    private volatile boolean running = true;

    public ConsumerWorker(String name, Consumer consumer) {
        this.name = name;
        this.consumer = consumer;
        this.thread = new Thread(this, name);
    }

    public void start() {
        thread.start();
    }

    public void submit(Job job) {
        inbox.offer(job);
    }

    public JobType getJobType() {
        return consumer.getJobType();
    }

    public void shutdown() {
        running = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            Job job;
            try {
                job = inbox.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            try {
                String output = consumer.process(job);
                System.out.println(name + " processed " + job + " -> " + output);
                job.getResult().complete(output);
            } catch (Exception e) {
                job.getResult().completeExceptionally(e);
            }
        }
    }
}
