package JobProcessing;

import java.util.Optional;

/** Builds a job and submits it to the coordinator, getting the result back directly. */
public class Producer {
    private final Coordinator coordinator;

    public Producer(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    /** Empty when no consumer is registered for the given job type. */
    public Optional<String> produce(JobType type, Object payload) {
        return coordinator.submit(new Job(type, payload));
    }
}
