package taskcontrol.executors;


import taskcontrol.basictasks.ITask;

/**
 * Interface for task executing classes
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public interface TaskExecutor {

    /**
     * Method witch start task execution process
     */
    void startTaskExecution();

    /**
     * Method that execute task
     */
    void executeTask();

    /**
     * Method for adding task in task executing group
     * @param task task to be executed
     */
    void addTask(ITask task);

    /**
     * Method for removing task from task executing group
     * @param task task to be removed
     */
    void removeTask(ITask task);

    /**
     * Method witch is called when task execution is done
     * @param resultFlag flag that determinate outcome of task (true - positive , false - negative)
     */
    void notifyTaskResult(boolean resultFlag);
}
