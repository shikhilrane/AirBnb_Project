package com.shikhilrane.project.airBnbApp.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;        // Applies this advice to all controller responses
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        List<String> allowedRoutes = List.of("/v3/api-docs", "/actuator");  // Routes excluded from response wrapping

        boolean isAllowed = allowedRoutes
                .stream()
                .anyMatch(route -> request.getURI().getPath().contains(route)); // Checks if current request is excluded

        if (body instanceof APIResponse<?> || isAllowed){
            return body;                                // Returns original response if already wrapped or excluded
        }

        return new APIResponse<>(body);                 // Wraps response inside APIResponse
    }
}

/*
    GlobalResponseHandler

        Purpose : Provides a consistent response structure for all API responses across the application.

        Responsibilities :
            - Intercept controller responses
            - Wrap responses inside APIResponse
            - Maintain a standardized API format
            - Exclude Swagger and Actuator endpoints

        How It Works :

            Controller Response
                    ↓
            GlobalResponseHandler
                    ↓
            APIResponse Wrapper
                    ↓
            Client Response

        Example :

            Controller Returns :

                {
                    "id": 1,
                    "name": "Hotel Lotus"
                }

            Actual Response Sent :

                {
                    "success": true,
                    "data": {
                        "id": 1,
                        "name": "Hotel Lotus"
                    }
                }

        Excluded Routes :

            - /v3/api-docs
            - /actuator

        Why Excluded ?

            - Swagger/OpenAPI requires original response format.
            - Actuator endpoints should not be modified.

        Benefits :
            - Consistent API responses
            - Cleaner controllers
            - Easier frontend integration
            - Centralized response formatting

        Flow :

            Client Request
                    ↓
            Controller
                    ↓
            Service
                    ↓
            Response Generated
                    ↓
            GlobalResponseHandler
                    ↓
            APIResponse Wrapper
                    ↓
            Client

        Note :
            - If response is already an APIResponse,
              it will not be wrapped again.
            - Swagger and Actuator endpoints are ignored.

        This class acts as the global response formatting layer
        of the application.
*/