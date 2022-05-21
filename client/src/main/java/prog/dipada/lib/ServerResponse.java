package prog.dipada.lib;

import java.io.Serializable;

/**
 * This class represent type of response received by the server
 */
public enum ServerResponse implements Serializable {
    OK, EMAILSENT, USERNOTEXIST, EMAILDELETED, ERRDELETINGEM, ERRCONNECTION
}
