package com.example;

import org.hawkular.apm.client.opentracing.APMTracer;

import io.opentracing.NoopTracerFactory;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {
    private Tracer tracer = NoopTracerFactory.create();

    @Override
    public void start() {
        boolean tracerEnabled = Boolean.parseBoolean(System.getenv("TRACER_ENABLED"));
        if (tracerEnabled) {
            tracer = new APMTracer();
        }
        vertx.createHttpServer()
                .requestHandler((req) -> {
                    Span span = tracer.buildSpan("hello-world-request").start();
                    span.setTag("enabled", tracerEnabled);
                    req.response().end(String.format("Hello World! Are we tracing this request? %s", System.getenv("TRACER_ENABLED")));
                    span.finish();
                })
                .listen(8080);
    }
}