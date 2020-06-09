package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.AssertionConcern;

public class ConnectionSettings extends AssertionConcern {

    private String hostName;
    private String password;
    private int port;
    private String username;
    private String virtualHost;

    public static ConnectionSettings instance() {
        return new ConnectionSettings("localhost", -1, "/", null, null);
    }

    public static ConnectionSettings instance(
            final String hostName,
            final String virtualHost) {
        return new ConnectionSettings(hostName, -1, virtualHost, null, null);
    }

    public static ConnectionSettings instance(
            final String hostName,
            final int port,
            final String virtualHost,
            final String username,
            final String password) {
        return new ConnectionSettings(
                hostName, port, virtualHost, username, password);
    }

    protected ConnectionSettings(
            final String hostName,
            final int port,
            final String virtualHost,
            final String username,
            final String password) {

        this.setHostName(hostName);
        this.setPassword(password);
        this.setPort(port);
        this.setUsername(username);
        this.setVirtualHost(virtualHost);
    }

    protected String hostName() {
        return this.hostName;
    }

    private void setHostName(final String hostName) {
        this.assertArgumentNotEmpty(hostName, "Host name must be provided.");

        this.hostName = hostName;
    }

    protected String password() {
        return this.password;
    }

    private void setPassword(final String password) {
        this.password = password;
    }

    protected int port() {
        return this.port;
    }

    protected boolean hasPort() {
        return this.port() > 0;
    }

    private void setPort(final int port) {
        this.port = port;
    }

    protected boolean hasUserCredentials() {
        return this.username() != null && this.password() != null;
    }

    protected String username() {
        return this.username;
    }

    private void setUsername(final String username) {
        this.username = username;
    }

    protected String virtualHost() {
        return this.virtualHost;
    }

    private void setVirtualHost(final String virtualHost) {
        this.assertArgumentNotEmpty(virtualHost, "Virtual host must be provided.");

        this.virtualHost = virtualHost;
    }
}
