package JobProcessing;

/**
 * Consumer for {@link JobType#SAVE} jobs: takes in the job's payload and
 * outputs a confirmation string describing what was saved.
 */
public class SaveConsumer implements Consumer {
    @Override
    public JobType getJobType() {
        return JobType.SAVE;
    }

    @Override
    public String process(Job job) {
        return "Saved: " + job.getPayload();
    }
}
