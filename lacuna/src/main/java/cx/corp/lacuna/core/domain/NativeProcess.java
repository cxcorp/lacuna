package cx.corp.lacuna.core.domain;

public interface NativeProcess {
    String UNKNOWN_OWNER = "";
    String UNKNOWN_DESCRIPTION = "";

    int getPid();

    void setPid(int pid);

    String getDescription();

    void setDescription(String desc);

    String getOwner();

    void setOwner(String owner);
}
