package JobProcessing;

/** Builds a job and submits it to the coordinator, getting the result back directly. */
public class Producer {
    private final Coordinator coordinator;

    public Producer(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public String produce(JobType type, Object payload) {
        return coordinator.submit(new Job(type, payload));
    }
}
