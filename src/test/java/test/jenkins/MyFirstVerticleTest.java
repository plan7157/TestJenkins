package test.jenkins;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {
    private Vertx vertx;
    private int port;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", port)
                );

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(MainVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) throws JsonProcessingException {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        final Async async = context.async();

        HttpClient httpClient = Vertx.vertx().createHttpClient();

        httpClient
                .post(port, "localhost", "")
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    response.handler(body -> {
                        context.assertEquals(body.toString(), "Hello Vert.x!");
                        async.complete();
                    });
                })
                .end();

    }
}