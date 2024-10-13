package ru.ntrubkin.untrusted.warden.component;

public class ServicePasswordHasher extends BasePasswordHasher {
    public ServicePasswordHasher() {
        super("MD5");
    }
}
