package rahulstech.javafx.example;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class UserService extends Service<UserService.Result> {

    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;

    public static final int ERROR_NO_USER = 10;
    public static final int ERROR_PASSWORD_MISMATCH = 11;
    public static final int ERROR_USERNAME = 12;
    public static final int SUCCESSFUL = 100;

    private final List<User> users = new ArrayList<>();

    private final Queue<Task<Result>> tasks = new ArrayBlockingQueue<>(10);

    public UserService() {
        addInitialUsers();
    }

    private void addInitialUsers() {
        for (int i = 1; i < 5; i++) {
            User user = new User(
                    UUID.randomUUID().toString(),
                    "user"+i,"pass"+i,
                    "GN"+i,"FN"+i);
            users.add(user);
        }
    }

    public void login(String username, String password) {
        tasks.add(new LogInTask(username, password));
        start();
    }

    public void signup(User user) {
        if (null == user) {
            throw new NullPointerException("user == null");
        }
        tasks.add(new SingUpTask(user));
        start();
    }

    @Override
    protected final Task<Result> createTask() {
        return tasks.poll();
    }

    private static void simulateDelay(long millis) throws InterruptedException{
        Thread.currentThread().sleep(millis);
    }

    public static class Result {
        private int taskCode;
        private int resultCode;
        private String message = null;
        private Object output = null;

        public int getTaskCode() {
            return taskCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public <T> T getOutput() {
            return (T) output;
        }
    }

    private class LogInTask extends Task<Result> {

        final String username;
        final String password;

        public LogInTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Result call() throws Exception {
            Result result = new Result();
            result.taskCode = LOGIN;

            String username = this.username;
            String password = this.password;
            User found = null;

            synchronized (users) {
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        found = user;
                        break;
                    }
                    simulateDelay(250);
                }
            }

            simulateDelay(500);

            if (null == found) {
                result.resultCode = ERROR_NO_USER;
                result.message = "No user found";
            }
            else {
                if (!found.getPassword().equals(password)) {
                    result.resultCode = ERROR_PASSWORD_MISMATCH;
                    result.message = "Wrong password";
                }
                else {
                    result.resultCode = SUCCESSFUL;
                    result.output = found;
                }
            }
            return result;
        }
    }

    private class SingUpTask extends Task<Result> {

        final User user;

        public SingUpTask(User user) {
            this.user = user;
        }

        @Override
        protected Result call() throws Exception {
            Result result = new Result();
            result.taskCode = SIGNUP;

            User newUser = this.user;
            boolean exists = false;

            synchronized (users) {
                for (User user : users) {
                    simulateDelay(250);
                    if (user.getUsername().equals(newUser.getUsername())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    newUser.setUserId(UUID.randomUUID().toString());
                    users.add(newUser);
                }
            }

            simulateDelay(500);

            if (exists) {
                result.resultCode = ERROR_USERNAME;
                result.message = "Username already used";
            }
            else {
                result.resultCode = SUCCESSFUL;
                result.output = newUser;
            }

            return result;
        }
    }


}
