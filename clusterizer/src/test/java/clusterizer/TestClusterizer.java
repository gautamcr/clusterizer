package clusterizer;

import static org.junit.Assert.*;

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
			Clusterizer c = new Clusterizer.ClusterBuilder()
					.withClusterName("testCluster")
					.withAllowedLocalInstances(3)
					.withAllowedTotalInstances(4)
					.build() ;
			while( !c.isPrimary()) {
				System.out.println("Not primary, sleeping 10s");
				Thread.sleep(5*1000);
			}
			System.out.println("Primary! Off to work!");
			eventLoop() ;
			c.close(); ; 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch( Exception e) {	
			e.printStackTrace();
		}
		return "thread done";
	}

}
