package com.sioma.spotsapi.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor, AsyncHandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        // Generar ID único para este request
        MDC.put(REQUEST_ID, UUID.randomUUID().toString().substring(0, 8));

        // Extraer userId (puede ser null si no hay autenticación aún)
        String userId = request.getHeader("X-User-Id");

        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        // Para requests autenticadas y no autenticados
        MDC.put(USER_ID, Objects.requireNonNullElse(userId, "anon"));

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        // Limpieza CRÍTICA: evitar memory leaks (ThreadLocal)
        log.debug("Finalizando request: {} con status {}", request.getRequestURI(), response.getStatus());
        MDC.clear();
    }

    @Override
    public void afterConcurrentHandlingStarted(@NonNull HttpServletRequest request,
                                               @NonNull HttpServletResponse response,
                                               @NonNull Object handler) {
        // Para requests asíncronos, limpiar MDC en el hilo original
        MDC.clear();
    }
}