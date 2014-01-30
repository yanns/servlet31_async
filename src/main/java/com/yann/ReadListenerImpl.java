package com.yann;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

public class ReadListenerImpl implements ReadListener {

    private final ServletInputStream input;
    private final AsyncContext asyncContext;
    private int read = 0;

    public ReadListenerImpl(ServletInputStream input, AsyncContext asyncContext) {
        this.input = input;
        this.asyncContext = asyncContext;
    }

    @Override
    public void onDataAvailable() throws IOException {
        System.out.println("ReadListenerImpl: onDataAvailable");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // simulate wait to check back pressure
                    Thread.sleep((int)(Math.random() * 200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int len = -1;
                byte b[] = new byte[1024];
                try {
                    while (input.isReady() && (len = input.read(b)) != -1) {
                        read += len;
                    }
                    System.out.println("read till now: " + read);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        })).start();
        System.out.println("onDataAvailable end");
    }

    public int getRead() {
        return read;
    }

    @Override
    public void onAllDataRead() throws IOException {
        System.out.println("onAllDataRead");
        asyncContext.complete();
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError");
        asyncContext.complete();
        t.printStackTrace();
    }
}
