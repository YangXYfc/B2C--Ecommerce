package com.team.ecommerce.common.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new TradeGlobalExceptionHandler())
                .build();
    }

    @Test
    void unfinishedFeatureReturnsStableApiError() throws Exception {
        mockMvc.perform(get("/test/not-implemented"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code").value("NOT_IMPLEMENTED"))
                .andExpect(jsonPath("$.message").value("功能待实现: cart.list"));
    }

    @Test
    void missingRequiredHeaderReturnsInvalidArgument() throws Exception {
        mockMvc.perform(get("/test/header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/test/not-implemented")
        void fail() {
            throw new FeatureNotImplementedException("cart.list");
        }

        @GetMapping("/test/header")
        void header(@org.springframework.web.bind.annotation.RequestHeader("X-Test") String value) {
        }
    }
}
