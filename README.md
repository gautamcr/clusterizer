# clusterizer
Provide basic clustering capability to Java programs

Enterprise applications that need to be on 24x7 are challenged by code overhead needed to maintain high availability. If a process dies someone or some program needs to be aware of it and re-start it. If a machine goes down the process has to be restarted on another machine. These challenges lead to single-point-of-failure and missed SLAs because something was down and it took time to bring it back up.

Clusterizer aims to provide a simple clustering capability to Java programs. The idea is multiple copies of the program are spread out to run on multiple machines and join a common cluster - one of them becomes the primary instance and does real work while the rest go on standby. If the primary goes down for whatever reason one of the standbys becomes the primary.

This project uses JGroups internally to create and manage clusters.

Example code:

		try {
			// Build the Clusterizer and join the cluster
			Clusterizer c = new Clusterizer.ClusterBuilder()
					.withClusterName("testCluster")
					.withAllowedLocalInstances(1)
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
	
