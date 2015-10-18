package com.dsto.ei.ha;

public interface IClusterizer {

	public void joinCluster(String clusterName);
	public int numInstances() ;
	public int numLocalInstances() ;
	public boolean isPrimary() ;
}
