package JobProcessing;

/**
 * Handles jobs of one specific {@link JobType} and produces a string result.
 */
public interface Consumer {
    JobType getJobType();

    String process(Job job);
}
