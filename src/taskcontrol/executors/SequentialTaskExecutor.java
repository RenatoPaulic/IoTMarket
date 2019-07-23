package taskcontrol.executors;


import program.Agent;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that execute tasks in sequential order
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class SequentialTaskExecutor implements TaskExecutor {

    private List<ITask> tasks;
    private int taskCounter;
    private ITask tmpTask;

    public SequentialTaskExecutor(){

        Agent.logger.info("Creating sequential task executor ");
        tasks = new ArrayList<>();
        taskCounter = 0;

    }


    @Override
    public void startTaskExecution(){
        executeTask();
    }

    @Override
    public void executeTask() {

        // if all tasks are executed
        if (taskCounter == tasks.size()) {

            Agent.logger.info("Sequential task executor - ended task execution");

            // end all subscribe tasks
            for(ITask task : tasks){

              if(task instanceof ISubscribeTask){
                  ((ISubscribeTask) task).endTask();
              }
            }

            // end program buyer
         //   Agent.end();


        // if there are more tasks to be executed
        } else{

            System.out.println("Executing " + (taskCounter + 1) + " of " + tasks.size() + " tasks");
            Agent.logger.info("Sequential task executor - executing " + (taskCounter + 1) + " of " + tasks.size() + " tasks");

            // execute next task
            tmpTask = tasks.get(taskCounter);
            tmpTask.execute();


        }
    }

    @Override
    public void addTask(ITask task){
        tasks.add(task);
    }

    @Override
    public void removeTask(ITask task){
        tasks.remove(task);
    }

    @Override
    public void notifyTaskResult(boolean resultFlag) {

        // if task execution was positive
        // execute next task
        if(resultFlag){

            System.out.println("Task " + (taskCounter+1) + " successfully done");
            System.out.println("Executing next task");
            Agent.logger.info("Sequential task executor: " + " Task " + (taskCounter+1) + " successfully done");
            Agent.logger.info("Sequential task executor: " + " Executing next task");

            taskCounter ++;
            executeTask();

        // if task execution was negative
        // stop task executing (taskCounter = task.size() - last task executed)
        }else{



            System.out.println("Task " + (taskCounter+1) + " didn't finish correctly or didn't bring positive result");
            System.out.println("Terminating task execution ... ");
            Agent.logger.info("Sequential task executor: " + " Task " + (taskCounter+1) + " didn't finish correctly or didn't bring positive result");
            Agent.logger.info("Terminating task execution .... ");

            taskCounter = tasks.size();
            executeTask();


        }
    }


}
