package JobProcessing;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The single coordinator. Holds zero to many registered consumers per job
 * type and routes each submitted job to one of them, round-robin. Safe to
 * call register/unregister/submit concurrently from multiple threads.
 */
public class Coordinator {
    private final Map<JobType, List<Consumer>> consumersByType = new ConcurrentHashMap<>();
    private final Map<JobType, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();

    public void register(Consumer consumer) {
        consumersByType
                .computeIfAbsent(consumer.getJobType(), type -> new CopyOnWriteArrayList<>())
                .add(consumer);
        roundRobinCounters.computeIfAbsent(consumer.getJobType(), type -> new AtomicInteger());
    }

    public void unregister(Consumer consumer) {
        List<Consumer> consumers = consumersByType.get(consumer.getJobType());
        if (consumers != null) {
            consumers.remove(consumer);
        }
    }

    /** Empty when no consumer is registered for the job's type. */
    public Optional<String> submit(Job job) {
        List<Consumer> consumers = consumersByType.get(job.getType());
        if (consumers == null) {
            return Optional.empty();
        }
        // Snapshot so the size and the picked element always agree, even if
        // another thread registers/unregisters a consumer concurrently.
        Consumer[] snapshot = consumers.toArray(new Consumer[0]);
        if (snapshot.length == 0) {
            return Optional.empty();
        }
        AtomicInteger counter = roundRobinCounters.get(job.getType());
        int index = Math.floorMod(counter.getAndIncrement(), snapshot.length);
        return Optional.of(snapshot[index].process(job));
    }
}
