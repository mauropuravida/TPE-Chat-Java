import edu.isistan.server.Server;
import edu.isistan.client.Client;

public class Main {

	public static void main(String[] args) {
		
		new Thread(()-> {
			Server.main(args);
		}).start();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(()-> {
			String[] u1 = new String[2];
			u1[0] = "localhost";
			u1[1] = "Mauro";
			Client.main(u1);
		}).start();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(()-> {
			String[] u2 = new String[2];
			u2[0] = "localhost";
			u2[1] = "Lean";
			Client.main(u2);
		}).start();

	}

}
