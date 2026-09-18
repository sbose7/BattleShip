package JobProcessing;

/** Consumer for {@link JobType#HELLO} jobs: always outputs "Hello". */
public class HelloConsumer implements Consumer {
    @Override
    public JobType getJobType() {
        return JobType.HELLO;
    }

    @Override
    public String process(Job job) {
        return "Hello";
    }
}
