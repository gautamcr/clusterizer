package clusterizer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.dsto.ei.ha.Clusterizer;

public class TestClusterizer implements Callable<String> {

	private final ExecutorService espool = Executors.newFixedThreadPool(10); 
	
	@Test
	public void test() {
		try {
			final Future<String> firstInstance = joinCluster() ;
			final Future<String> secondInstance = joinCluster() ;	
			final Future<String> thirdInstance = joinCluster() ;	

			System.out.println(firstInstance.get()) ;
			System.out.println(secondInstance.get()) ;
			System.out.println(thirdInstance.get()) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Future<String> joinCluster() {
		return espool.submit(this) ;
	}

	private void eventLoop() {
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String call() throws Exception {	
		
		try {
			// Build the Clusterizer and join the cluster
			Clusterizer c = new Clusterizer.ClusterBuilder()
					.withClusterName("testCluster")
					.withAllowedLocalInstances(3)
					.withAllowedTotalInstances(4)
					.withRandomBounce(false)
					.build() ;
			
			// Wait till I become the primary
			while( !c.isPrimary()) {
				System.out.println( c.getName() + ": Not primary, sleeping 10s");
				Thread.sleep(5*1000);
			}
			
			// I am now the primary, do some useful work!
			System.out.println(c.getName() + ": Primary! Off to work!");
			eventLoop() ; // this could potentially be a 24x7x365 job
			
			// Exit the cluster if done.
			c.exitCluster();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch( Exception e) {	
			e.printStackTrace();
		}
		return "thread done";
	}

}
