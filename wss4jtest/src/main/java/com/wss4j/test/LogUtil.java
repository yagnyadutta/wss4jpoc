package com.wss4j.test;

import java.text.MessageFormat;
import org.apache.commons.logging.Log;

public class LogUtil {
    /**
     * logs message at debug level.
     *
     * @param log  Log object used to log message
     * @param msg  message to be logged
     * @param args parameters to be substituted
     */
    public static void debug(Log log, String msg, String... args)
    {
        if (log.isDebugEnabled())
        {
            log.debug(MessageFormat.format(msg, args));
        }
    }

    /**
     * logs message at error level.
     *
     * @param log  Log object used to log message
     * @param msg  message to be logged
     * @param args parameters to be substituted
     */
    public static void error(Log log, String msg, String... args)
    {
        if (log.isErrorEnabled())
        {
            log.error(MessageFormat.format(msg, args));
        }
    }

    /**
     * logs message at info level.
     *
     * @param log  Log object used to log message
     * @param msg  message to be logged
     * @param args parameters to be substituted
     */
    public static void info(Log log, String msg, Object... args)
    {
        if (log.isInfoEnabled())
        {
            log.info(MessageFormat.format(msg, args));
        }
    }

    /**
     * logs message at warn level.
     *
     * @param log  Log object used to log message
     * @param msg  message to be logged
     * @param args parameters to be substituted
     */
    public static void warn(Log log, String msg, Object... args)
    {
        if (log.isWarnEnabled())
        {
            log.warn(MessageFormat.format(msg, args));
        }
    }
}
