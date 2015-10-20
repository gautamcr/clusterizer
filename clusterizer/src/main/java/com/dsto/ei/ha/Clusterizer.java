package com.dsto.ei.ha;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.UUID;

import com.dsto.ei.ha.IClusterizer;

public class Clusterizer 
	extends ReceiverAdapter 
	implements IClusterizer 
{

	private final String clusterName ;
	private View view = null;
	private String channelName ;
	private JChannel channel;
	private String hostName ;
	
	private Clusterizer (ClusterBuilder cb) {
		this.clusterName = cb.clusterName ;
		InetAddress my_addr;
		try {
			my_addr = InetAddress.getLocalHost();
			hostName = my_addr.getHostName() ;
			this.channelName = hostName + "." + 
					(new java.util.Random()).nextInt(10000) ;
			this.channel = new JChannel("udp.xml") ;
			channel.setName(channelName);
			channel.setReceiver(this);
			channel.connect(clusterName);	
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public int numLocalInstances() {
		java.util.List<Address> members = channel.getView().getMembers() ;
		int instances = 0 ;

		java.util.Iterator<Address> it = members.iterator() ;
		while (it.hasNext() ) {
			Address addr = it.next() ;
			UUID uuid = (UUID) addr ;
			if( uuid.toString().startsWith(hostName) ) {
				instances++ ;
			}
		}
		return instances;
	}
	
	public int numInstances() {
		java.util.List<Address> members = channel.getView().getMembers() ;
		return members.size();
	}

	public String getName() {
		return channelName;
	}
	
	@Override
	public void viewAccepted(View new_view) {
	    System.out.println("** view: " + new_view);
	    this.view  = new_view;
	}

	public boolean isPrimary() {
		String creator = view.getCreator().toString() ;
		if( creator.equals(channelName) ) {
			return true ;
		}
		return false ;
	}
	
	public void exitCluster() {
		channel.close();
	}

	public static class ClusterBuilder {
		private int allowedLocalInstances = 1;
		private int allowedTotalInstances = 1 ;
		private String clusterName ;
		private boolean withRandomBounce = false;
		
		public ClusterBuilder() {
		}
		
		public ClusterBuilder withClusterName( String clusterName ) {
			this.clusterName = clusterName ;
			return this ;
		}
		
		public ClusterBuilder withAllowedLocalInstances( int n) {
			this.allowedLocalInstances = n ;
			return this ;
		}
		
		public ClusterBuilder withAllowedTotalInstances( int n) {
			this.allowedTotalInstances = n ;
			return this;
		}
		
		public ClusterBuilder withRandomBounce( boolean b) {
			this.withRandomBounce = b ;
			return this ;
		}
		
		public Clusterizer build() throws Exception {
			Clusterizer c = new Clusterizer(this) ;
			if( c.numLocalInstances() > this.allowedLocalInstances) {
				throw new Exception("numLocalInstances exceeds allowedLocalInstances: " 
						+ c.numLocalInstances() + "/" + this.allowedLocalInstances) ;
			}
			
			if( c.numInstances() > this.allowedTotalInstances) {
				throw new Exception("allowedTotalInstances exceeds numInstances: " 
						+ c.numInstances() + "/" + this.allowedTotalInstances) ;
			}
			return c;
		}
		
	}

	

	

}
