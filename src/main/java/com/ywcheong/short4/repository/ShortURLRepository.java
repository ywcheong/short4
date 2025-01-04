package com.ywcheong.short4.repository;

public interface ShortURLRepository {
    boolean attemptReserve(String shortUrl);
}
