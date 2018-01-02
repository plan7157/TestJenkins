package test.jenkins;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestServer {
    public static void main(String[] args) {
        BlockingQueue<AsyncResult<String>> q = new ArrayBlockingQueue<>(1);
        Vertx.vertx().deployVerticle(new MainVerticle(), q::offer);
        AsyncResult<String> result;
        try {
            result = q.take();
            if (result.failed()) {
                throw new RuntimeException(result.cause());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
