package ubique.idp.processing.serverconsole;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Decorator for console output operations.
 * 
 * @author NatfullinA, talanovm
 */
public class IDPOutputStream extends OutputStream {

	OutputStream s;
	StringBuilder p;
	Queue queue;
	private static Logger log = Logger.getLogger(IDPOutputStream.class);

	public IDPOutputStream(OutputStream s) throws Throwable {
		this.s = s;
		p = new StringBuilder();
		queue = Queue.getQueue();
	}

	public void write(int i) throws IOException {
		p.append((char) i);
		if (i == '\n') {
			String t = p.toString();
			queue.push(t);
			log.info(t);
			p = new StringBuilder();
		}
	}

}
