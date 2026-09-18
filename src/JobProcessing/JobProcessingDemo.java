package JobProcessing;

/** Demonstrates producer -> coordinator -> consumer, with 0 to n consumers registered. */
public final class JobProcessingDemo {
    private JobProcessingDemo() {
    }

    public static void main(String[] args) {
        Coordinator coordinator = new Coordinator();
        Producer producer = new Producer(coordinator);

        try {
            producer.produce(JobType.HELLO, null);
        } catch (IllegalStateException e) {
            System.out.println("hello (no consumers yet) -> failed: " + e.getMessage());
        }

        coordinator.register(new HelloConsumer());
        coordinator.register(new SaveConsumer());

        System.out.println("hello -> " + producer.produce(JobType.HELLO, null));
        System.out.println("save -> " + producer.produce(JobType.SAVE, "battleship-state-1"));
    }
}
