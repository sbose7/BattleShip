package JobProcessing;

import java.util.concurrent.CompletableFuture;

/**
 * Submits jobs of a given type and payload to the coordinator. Any number of
 * producers may share one coordinator.
 */
public class Producer {
    private final String name;
    private final Coordinator coordinator;

    public Producer(String name, Coordinator coordinator) {
        this.name = name;
        this.coordinator = coordinator;
    }

    public CompletableFuture<String> produce(JobType type, Object payload) {
        Job job = new Job(type, payload);
        System.out.println(name + " submitted " + job);
        coordinator.submit(job);
        return job.getResult();
    }
}
