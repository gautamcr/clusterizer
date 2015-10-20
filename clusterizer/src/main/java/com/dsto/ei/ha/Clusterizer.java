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
	private int numLocalInstances ;
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
			numLocalInstances = getNumLocalInstances(channel) ;	
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private int getNumLocalInstances(JChannel channel2) {
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
		if( view != null ) {
			return view.size() ;
		}
		return 0;
	}

	public int numLocalInstances() {
		return numLocalInstances;
	}
	
	@Override
	public void viewAccepted(View new_view) {
	    System.out.println("** view: " + new_view);
	    this.view  = new_view;
	}

	public boolean isPrimary() {
		String creator = view.getCreator().toString() ;
		if( creator.equals(channelName) ) {
			System.out.println( channelName + ": creator=true" ) ;
			return true ;
		}
		else {
			System.out.println( channelName + ": creator=false" ) ;
		}
		return false ;
	}
	
	public void close() {
		channel.close();
	}

	public static class ClusterBuilder {
		private int allowedLocalInstances = 1;
		private int allowedTotalInstances = 1 ;
		private String clusterName ;
		
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
