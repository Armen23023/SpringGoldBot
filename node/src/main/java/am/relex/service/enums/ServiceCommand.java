package am.relex.service.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    PUT("/put"),
    GET("/get")
    ;


    public boolean equals(String cmd){
        return  this.toString().equals(cmd);
    }

    private final String value;

    ServiceCommand(String cmd) {
        this.value = cmd;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String v){
        for(ServiceCommand c: ServiceCommand.values()){
            if (c.value.equals(v)){
                return c;
            }
        }
        return null;
    }
}
