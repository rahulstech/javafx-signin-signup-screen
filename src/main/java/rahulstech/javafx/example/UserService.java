/**
 * Copyright 2021 rahulstch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rahulstech.javafx.example;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService extends Service<UserService.Result> {

    public static final int ERROR_NO_USER = 10;
    public static final int ERROR_PASSWORD_MISMATCH = 11;
    public static final int ERROR_USERNAME = 12;
    public static final int SUCCESSFUL = 100;

    private static List<User> users = new ArrayList<>();
    static {
        for (int i = 1; i < 5; i++) {
            User user = new User(
                    UUID.randomUUID().toString(),
                    "iamuser"+i,"pass"+i,
                    "GN"+i,"FN"+i);
            users.add(user);
        }
    }

    private Task<Result> task = null;

    public UserService() { }

    public void login(String username, String password) {
        this.task = new LogInTask(username, password);
        start();
    }

    public void signup(User user) {
        this.task = new SingUpTask(user);
        start();
    }

    @Override
    protected final Task<Result> createTask() {
        return task;
    }

    private static void simulateDelay(long millis) throws InterruptedException{
        Thread.currentThread().sleep(millis);
    }

    public static class Result {
        private int resultCode;
        private String message = null;
        private Object output = null;

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
