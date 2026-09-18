package JobProcessing;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The single coordinator in the system. Producers submit jobs to it; it
 * dispatches each job, by job type, to one of the registered consumers.
 * Zero to many consumers may be registered or unregistered at any time -
 * a job whose type has no registered consumer fails its result future.
 */
public class Coordinator implements Runnable {
    private final BlockingQueue<Job> incoming = new LinkedBlockingQueue<>();
    private final Map<JobType, List<ConsumerWorker>> consumersByType = new ConcurrentHashMap<>();
    private final Map<JobType, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();
    private final Thread thread = new Thread(this, "Coordinator");
    private volatile boolean running = true;

    public void start() {
        thread.start();
    }

    public void submit(Job job) {
        incoming.offer(job);
    }

    public void registerConsumer(ConsumerWorker worker) {
        consumersByType
                .computeIfAbsent(worker.getJobType(), type -> new CopyOnWriteArrayList<>())
                .add(worker);
        roundRobinCounters.computeIfAbsent(worker.getJobType(), type -> new AtomicInteger());
        worker.start();
    }

    public void unregisterConsumer(ConsumerWorker worker) {
        List<ConsumerWorker> workers = consumersByType.get(worker.getJobType());
        if (workers != null) {
            workers.remove(worker);
        }
        worker.shutdown();
    }

    public void shutdown() {
        running = false;
        thread.interrupt();
        consumersByType.values().forEach(workers -> workers.forEach(ConsumerWorker::shutdown));
    }

    @Override
    public void run() {
        while (running) {
            Job job;
            try {
                job = incoming.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            dispatch(job);
        }
    }

    private void dispatch(Job job) {
        List<ConsumerWorker> workers = consumersByType.get(job.getType());
        if (workers == null || workers.isEmpty()) {
            job.getResult().completeExceptionally(
                    new IllegalStateException("No consumer registered for job type " + job.getType()));
            return;
        }
        AtomicInteger counter = roundRobinCounters.get(job.getType());
        int index = Math.floorMod(counter.getAndIncrement(), workers.size());
        workers.get(index).submit(job);
    }
}
