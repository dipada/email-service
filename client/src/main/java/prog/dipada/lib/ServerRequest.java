package prog.dipada.lib;

import java.io.Serializable;

public enum ServerRequest implements Serializable {
    AUTH, SENDEMAIL, DELETEEMAIL, SENDALL
}
