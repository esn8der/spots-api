package com.sioma.spotsapi.domain.ports.out;

public interface PasswordHasher {
    String hash(String rawPassword);
}
