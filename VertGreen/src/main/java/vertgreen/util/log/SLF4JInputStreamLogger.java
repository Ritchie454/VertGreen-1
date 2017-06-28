package vertgreen.util.log;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SLF4JInputStreamLogger extends Thread {

    final Logger log;
    final BufferedReader br;

    public SLF4JInputStreamLogger(Logger log, InputStream is) {
        super(SLF4JInputStreamLogger.class.getSimpleName());
        this.log = log;
        this.br = new BufferedReader(new InputStreamReader(is));
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String ln = br.readLine();
                if (ln == null) {
                    return;
                }
                log.info(ln);
            }
        } catch (IOException ignored) {
            //The stream has ended
        }
    }

}
