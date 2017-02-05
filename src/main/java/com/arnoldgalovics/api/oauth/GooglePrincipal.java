package com.arnoldgalovics.api.oauth;

import java.math.BigInteger;

public class GooglePrincipal {
    private final BigInteger id;

    public GooglePrincipal(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GooglePrincipal that = (GooglePrincipal) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GooglePrincipal{" +
                "id=" + id +
                '}';
    }
}
