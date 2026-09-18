package JobProcessing;

import java.util.ArrayList;
import java.util.List;

/**
 * The single coordinator. Holds zero to many registered consumers and routes
 * each submitted job to the first consumer that handles its type.
 */
public class Coordinator {
    private final List<Consumer> consumers = new ArrayList<>();

    public void register(Consumer consumer) {
        consumers.add(consumer);
    }

    public void unregister(Consumer consumer) {
        consumers.remove(consumer);
    }

    public String submit(Job job) {
        for (Consumer consumer : consumers) {
            if (consumer.getJobType() == job.getType()) {
                return consumer.process(job);
            }
        }
        throw new IllegalStateException("No consumer registered for job type " + job.getType());
    }
}
