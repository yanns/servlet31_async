package com.yann;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

public class ReadListenerImpl implements ReadListener {

    private final ServletInputStream input;
    private final AsyncContext asyncContext;
    private volatile int read = 0;

    public ReadListenerImpl(ServletInputStream input, AsyncContext asyncContext) {
        this.input = input;
        this.asyncContext = asyncContext;
    }

    private String threadId() {
        return "[Thread " + Thread.currentThread().getId() + "] ";
    }

    @Override
    public void onDataAvailable() throws IOException {
        synchronized (input) {
            System.out.println(threadId() + "ReadListenerImpl: onDataAvailable");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (input) {
                        int len = -1;
                        int localRead = 0;
                        byte b[] = new byte[1024];
                        try {
                            while (input.isReady() && (len = input.read(b)) != -1) {
                                localRead += len;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            // simulate wait to check back pressure
                            Thread.sleep((int) (Math.random() * 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        read += localRead;
                        System.out.println(threadId() + "read till now: " + read);
                    }
                }
            })).start();
            System.out.println(threadId() + "onDataAvailable end");
        }
    }

    public int getRead() {
        return read;
    }

    @Override
    public void onAllDataRead() throws IOException {
        synchronized (input) {
            System.out.println(threadId() + "onAllDataRead");
            asyncContext.complete();
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError");
        asyncContext.complete();
        t.printStackTrace();
    }
}
