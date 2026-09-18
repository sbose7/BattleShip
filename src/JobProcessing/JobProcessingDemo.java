package JobProcessing;

import java.util.Optional;

/** Demonstrates producer -> coordinator -> consumer, with 0 to n consumers per type. */
public final class JobProcessingDemo {
    private JobProcessingDemo() {
    }

    public static void main(String[] args) {
        Coordinator coordinator = new Coordinator();
        Producer producer = new Producer(coordinator);

        // No consumers registered yet: an empty result, no exception thrown.
        print("hello (no consumers yet)", producer.produce(JobType.HELLO, null));

        coordinator.register(new HelloConsumer());
        coordinator.register(new SaveConsumer());
        coordinator.register(new SaveConsumer()); // second SAVE consumer: load now spreads across both

        print("hello", producer.produce(JobType.HELLO, null));
        print("save 1", producer.produce(JobType.SAVE, "battleship-state-1"));
        print("save 2", producer.produce(JobType.SAVE, "battleship-state-2"));
    }

    private static void print(String label, Optional<String> result) {
        System.out.println(label + " -> " + result.orElse("no consumer available"));
    }
}
