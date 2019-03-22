package org.jiuwo.ratel.exception;

import org.jiuwo.ratel.util.JsonUtil;

/**
 * @author Steven Han
 */
public class RatelException extends RuntimeException {

    private static final long serialVersionUID = -4077570204600607577L;

    public RatelException() {
    }

    public RatelException(String message) {
        super(message);
    }

    public RatelException(String message, Object obj) {
        super(String.format("%s\n%s", message, JsonUtil.serialize(obj)));
    }

    public RatelException(String message, Throwable cause) {
        super(message, cause);
    }

    public RatelException(Throwable cause) {
        super(cause);
    }
}
