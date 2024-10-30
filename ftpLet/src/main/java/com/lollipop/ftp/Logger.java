package com.lollipop.ftp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class Logger {

    public static int LOG_MAX_LENGTH = 512;
    private static LoggerImpl loggerImpl = null;

    private static final Delegate defaultDelegate = new Delegate(null, null, null);

    public static Delegate create() {
        return new Delegate(null, null, null);
    }

    public static Delegate create(Object object) {
        return new Delegate(null, object, null);
    }

    public static Delegate create(String tag) {
        return new Delegate(tag, null, null);
    }

    public static Delegate create(String tag, Object object) {
        return new Delegate(tag, object, null);
    }

    public static Delegate create(String tag, Object object, String prefix) {
        return new Delegate(tag, object, prefix);
    }

    public static void setLoggerImpl(LoggerImpl loggerImpl) {
        Logger.loggerImpl = loggerImpl;
    }

    public static void log(LoggerLevel level, String tag, String message) {
        if (loggerImpl != null) {
            loggerImpl.log(level, tag, message);
        }
    }

    public void d(String message) {
        defaultDelegate.d(message);
    }

    public void i(String message) {
        defaultDelegate.i(message);
    }

    public void w(String message) {
        defaultDelegate.w(message);
    }

    public void e(String message) {
        defaultDelegate.e(message);
    }

    public void e(String message, Throwable error) {
        defaultDelegate.e(message, error);
    }

    public static class Delegate {

        private final String tag;
        private final String logPrefix;

        public Delegate(String tag, Object object, String prefix) {
            if (tag == null || tag.isEmpty()) {
                this.tag = "LLog";
            } else {
                this.tag = tag;
            }
            if (object != null || prefix != null) {
                StringBuilder builder = new StringBuilder();
                if (object != null) {
                    builder.append(object.getClass().getSimpleName());
                    builder.append("@");
                    builder.append(System.identityHashCode(object));
                    builder.append(" -- ");
                }
                if (prefix != null && !prefix.isEmpty()) {
                    builder.append(prefix);
                    builder.append(" -- ");
                }
                builder.append("> ");
                logPrefix = builder.toString();
            } else {
                logPrefix = null;
            }
        }

        private String fillLogInfo(String message) {
            return logPrefix + message;
        }

        private void printLog(LoggerLevel level, String value) {
            Logger.log(level, tag, value);
        }

        private void splitLog(LoggerLevel level, String message) {
            int stepLength = LOG_MAX_LENGTH;
            String outMsg = fillLogInfo(message);
            int msgLength = outMsg.length();
            if (msgLength <= stepLength) {
                printLog(level, outMsg);
            } else {
                int start = 0;
                int end = stepLength;
                while (start < msgLength) {
                    printLog(level, outMsg.substring(start, end));
                    start += stepLength;
                    end += stepLength;
                    if (end > msgLength) {
                        end = msgLength;
                    }
                }
            }
        }

        public void d(String message) {
            splitLog(LoggerLevel.DEBUG, message);
        }

        public void i(String message) {
            splitLog(LoggerLevel.INFO, message);
        }

        public void w(String message) {
            splitLog(LoggerLevel.WARN, message);
        }

        public void e(String message) {
            splitLog(LoggerLevel.ERROR, message);
        }

        public void e(String message, Throwable error) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            printWriter.write(message);

            printWriter.write("  :  ");

            error.printStackTrace(printWriter);
            printWriter.flush();

            splitLog(LoggerLevel.ERROR, outputStream.toString());
        }

    }


    public interface LoggerImpl {
        void log(LoggerLevel level, String tag, String message);
    }

}
