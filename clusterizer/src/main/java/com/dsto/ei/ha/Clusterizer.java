package com.dsto.ei.ha;

import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.dsto.ei.ha.IClusterizer;

public class Clusterizer 
	extends ReceiverAdapter 
	implements IClusterizer 
{

	private final String clusterName ;
	private final int allowedLocalInstances ;
	private View view = null;
	
	private Clusterizer (ClusterBuilder cb) {
		this.clusterName = cb.clusterName ;
		this.allowedLocalInstances = cb.allowedLocalInstances ;
	}
	
	public void joinCluster() {
		// TODO Auto-generated method stub

	}

	public int numInstances() {
		// TODO Auto-generated method stub
		if( view != null ) {
			return view.size() ;
		}
		return 0;
	}

	public int numLocalInstances() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void viewAccepted(View new_view) {
	    System.out.println("** view: " + new_view);
	    this.view  = new_view;
	}

	public boolean isPrimary() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static class ClusterBuilder {
		private int allowedLocalInstances;
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
		
		public Clusterizer build() {
			return new Clusterizer( this);
		}
		
	}

}
