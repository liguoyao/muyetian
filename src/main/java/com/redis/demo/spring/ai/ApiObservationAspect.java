package com.redis.demo.spring.ai;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiObservationAspect {

    private final ObservationRegistry registry;

    public ApiObservationAspect(ObservationRegistry registry) {
        this.registry = registry;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        String name = pjp.getSignature().getDeclaringTypeName()
                + "." + pjp.getSignature().getName();
        return Observation.createNotStarted(name, registry)
                .observeChecked(() -> pjp.proceed());
    }
}
