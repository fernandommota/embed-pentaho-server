package br.com.bovbi.embed.logger;

import br.com.bovbi.embed.Utils;
import org.pentaho.platform.api.engine.ILogger;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Acredito nao ser a melhor pratica
 * Porem enquanto nao conseguimos utilizar da classe do logger do pentaho (Nao sei qual classe que é ... )
 */
public class EmbedLogger implements ILogger {
    private final static String LOGGER = "{0} {1} [{2}] {3} - {4}";
    private final Class<?> clasz;

    private int loggingLevel = TRACE;

    private EmbedLogger(Class<?> clasz) {
        this.clasz = clasz;
    }

    public static EmbedLogger Get(Class<?> clasz) {
        return new EmbedLogger(clasz);
    }

    public int getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(int loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public void trace(String message) {
        print("trace", message);
    }

    public void debug(String message) {
        print("debug", message);
    }

    public void info(String message) {
        print("info", message);
    }

    public void warn(String message) {
        print("warn", message);
    }

    public void error(String message) {
        print("error", message);
    }

    public void fatal(String message) {
        print("fatal", message);
    }

    public void trace(String message, Throwable error) {
        print("trace", message, error);
    }

    public void debug(String message, Throwable error) {
        print("debug", message, error);
    }

    public void info(String message, Throwable error) {
        print("info", message, error);
    }

    public void warn(String message, Throwable error) {
        print("warn", message, error);
    }

    public void error(String message, Throwable error) {
        print("error", message, error);
    }

    public void fatal(String message, Throwable error) {
        print("fatal", message, error);
    }

    private void print(String t, String mensagem) {
        System.out.println(MessageFormat.format(
                LOGGER,
                Utils.formatDate(new Date()),
                t.toUpperCase(),
                Thread.currentThread().getName(),
                clasz.getName(),
                mensagem
        ));
    }

    private void print(String t, String mensagem, Throwable throwable) {
        System.out.println(MessageFormat.format(
                LOGGER,
                Utils.formatDate(new Date()),
                t.toUpperCase(),
                Thread.currentThread().getName(),
                clasz.getName(),
                mensagem + "\n" + throwable.getMessage()
        ));
    }
}
