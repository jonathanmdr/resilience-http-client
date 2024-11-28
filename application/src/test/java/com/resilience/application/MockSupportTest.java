package com.resilience.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public abstract class MockSupportTest {

    protected abstract List<Object> mocks();

    @BeforeEach
    void cleanup() {
        if (mocks() != null && !mocks().isEmpty()) {
            Mockito.reset(mocks().toArray());
        }
    }

}
