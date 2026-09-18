package JobProcessing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Demonstrates the job processing system: one coordinator, one producer, and
 * a consumer pool that grows from zero to several workers at runtime.
 */
public final class JobProcessingDemo {
    private JobProcessingDemo() {
    }

    public static void main(String[] args) throws InterruptedException {
        Coordinator coordinator = new Coordinator();
        coordinator.start();

        Producer producer = new Producer("Producer-1", coordinator);

        // No consumers registered yet: this job's result future fails.
        CompletableFuture<String> noConsumerYet = producer.produce(JobType.HELLO, null);
        printResult("hello (no consumers yet)", noConsumerYet);

        // Scale the consumer pool up from zero to three workers.
        coordinator.registerConsumer(new ConsumerWorker("HelloConsumer-1", new HelloConsumer()));
        coordinator.registerConsumer(new ConsumerWorker("SaveConsumer-1", new SaveConsumer()));
        coordinator.registerConsumer(new ConsumerWorker("SaveConsumer-2", new SaveConsumer()));

        CompletableFuture<String> hello = producer.produce(JobType.HELLO, null);
        CompletableFuture<String> save1 = producer.produce(JobType.SAVE, "battleship-state-1");
        CompletableFuture<String> save2 = producer.produce(JobType.SAVE, "battleship-state-2");

        printResult("hello", hello);
        printResult("save1", save1);
        printResult("save2", save2);

        coordinator.shutdown();
    }

    private static void printResult(String label, CompletableFuture<String> future) {
        try {
            System.out.println(label + " -> " + future.get(2, TimeUnit.SECONDS));
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            System.out.println(label + " -> failed: " + e.getCause());
        }
    }
}
