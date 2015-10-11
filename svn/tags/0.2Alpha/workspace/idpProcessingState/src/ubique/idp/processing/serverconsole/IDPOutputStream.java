package ubique.idp.processing.serverconsole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Decorator for console output operations. 
 * @author NatfullinA
 *
 */
public class IDPOutputStream extends OutputStream {

	OutputStream s;
	ByteArrayOutputStream p;
	Queue queue;
	
	//PipedOutputStream n;
	//PipedInputStream n1;
	//InputStreamReader n2;
	//BufferedReader n3;
	

	public IDPOutputStream(OutputStream s) throws Throwable {
		this.s = s;
		p = new ByteArrayOutputStream(); 
		
		/*
		n = new PipedOutputStream();
		n1 = new PipedInputStream(n);
		n2 = new InputStreamReader(n1);
		n3 = new BufferedReader(n2);
		*/
		
		queue = Queue.getQueue();
	}

	public void write(int i) throws IOException {
		s.write(i);
		//n.write(i);
		p.write(i);

		if (i == '\n')
		{
			queue.push(p.toString());
			p.reset();
			
			//queue.push(n3.readLine() + "\n");
		}
	}

	//public void write(byte[] b) throws IOException {
	//}

}
