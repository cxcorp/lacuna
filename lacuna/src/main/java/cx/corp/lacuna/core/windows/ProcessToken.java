package cx.corp.lacuna.core.windows;

interface ProcessToken extends AutoCloseable {
    int getToken();

    @Override
    void close();
}
