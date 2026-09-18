package JobProcessing;

/** A unit of work: a type plus whatever input that type's consumer needs. */
public final class Job {
    private final JobType type;
    private final Object payload;

    public Job(JobType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public JobType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
