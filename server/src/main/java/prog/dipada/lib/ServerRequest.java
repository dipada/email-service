package prog.dipada.lib;

import java.io.Serializable;

/**
 * This class represent type of request sent to the server
 */
public enum ServerRequest implements Serializable {
    AUTH, SENDEMAIL, DELETEEMAIL, SENDALL
}
