package prog.dipada.lib;

import java.io.Serializable;

public enum ServerResponse implements Serializable {
    OK, EMAILSENT, USERNOTEXIST, EMAILDELETED, ERRDELETINGEM, ERRCONNECTION
}
