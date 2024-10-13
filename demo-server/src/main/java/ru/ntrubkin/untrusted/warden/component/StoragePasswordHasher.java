package ru.ntrubkin.untrusted.warden.component;

public class StoragePasswordHasher extends BasePasswordHasher {
    public StoragePasswordHasher() {
        super("SHA-256");
    }
}
