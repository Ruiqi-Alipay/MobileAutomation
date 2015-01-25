/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * @author qiyi.wxc
 * @version $Id: ShellExecute.java, v 0.1 2014年9月16日 下午8:37:16 qiyi.wxc Exp $
 */
public class ShellExecute {

    private boolean mComplete = false;

    public interface SyncRunnable {
        public void run();

    }

    /**
     * run in thread sync for some block operations
     * 
     * @param runner
     */
    public void runInThreadSync(final SyncRunnable runner) {
        new Thread(new Runnable() {
            public void run() {
                runner.run();
                synchronized (this) {
                    mComplete = true;
                }
            }
        }).start();

        synchronized (this) {
            while (!mComplete) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * execute shell command on device
     * 
     * @param command
     *            e.g. "ls -l"
     * @param directory
     *            the directory where the command is executed. e.g. "/sdcard/"
     * @return std of the command
     */
    public CommandResult execute(String command) {
        CommandResult cr = new CommandResult();
        BufferedReader in = null;
        try {
            Process process = Runtime.getRuntime().exec("adb shell " + command + "\n");

            //            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            //            os.writeBytes(command + "\n");
            //            os.flush();

            //            cr.ret = process.waitFor();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                cr.console.strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return cr;
    }

    public interface CallBack<T> {
        T runInTimeout() throws InterruptedException;
    }

    private final static long INTERVAL = 50;

    /**
     * perform a function in timeout; return function's return value if function
     * is over in timeout, or else return null
     * 
     * @param <T>
     * @param callBack
     * @param timeout
     * @return null means reach timeout;
     */
    public static <T> T doInTimeout(CallBack<T> callBack, long timeout) {
        final CallBack<T> fCallBack = callBack;
        ExecutorService exs = Executors.newCachedThreadPool();

        Future<T> future = exs.submit(new Callable<T>() {
            public T call() throws Exception {
                try {
                    return fCallBack.runInTimeout();
                } catch (InterruptedException e) {
                    System.out.println("Timeout: Exiting by Exception");
                }

                return null;
            }
        });

        long end = System.currentTimeMillis() + timeout;

        while (true) {
            if (null == future) {
                return null;
            }

            if (System.currentTimeMillis() > end) {
                future.cancel(true);
                return null;
            }

            try {
                if (future.isDone()) {
                    return future.get();
                }
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public CommandResult execute(final String command, long timeout) {
        CommandResult ret = (CommandResult) doInTimeout(new CallBack<CommandResult>() {
            public CommandResult runInTimeout() throws InterruptedException {
                return execute(command);
            }
        }, timeout);
        return ret;
    }
}
