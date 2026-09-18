package JobProcessing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A unit of work submitted by a {@link Producer}. Carries an optional payload
 * and a future that is completed with the consumer's output once processed.
 */
public final class Job {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final long id;
    private final JobType type;
    private final Object payload;
    private final CompletableFuture<String> result = new CompletableFuture<>();

    public Job(JobType type, Object payload) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.type = type;
        this.payload = payload;
    }

    public long getId() {
        return id;
    }

    public JobType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

    public CompletableFuture<String> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Job#" + id + "[" + type + "]";
    }
}
