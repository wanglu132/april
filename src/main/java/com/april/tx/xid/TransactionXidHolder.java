package com.april.tx.xid;

import com.april.util.PrimitiveUtil;

public class TransactionXidHolder {
	
	private ThreadLocal<TransactionXid> threadLocalTransactionXid = new ThreadLocal<TransactionXid>();

    public TransactionXid getCurrentTransactionXid() {
    	
    	TransactionXid xid = threadLocalTransactionXid.get();
    	if(xid == null) {
    		xid = new TransactionXid();
        	threadLocalTransactionXid.set(xid);
    	}
    	return xid;
    }
    
    public String getGlobalTransactionId() {
    	return PrimitiveUtil.bytes2hex(this.getCurrentTransactionXid().getGlobalTransactionId());
    }
    
    public String getBranchQualifier() {
    	return PrimitiveUtil.bytes2hex(this.getCurrentTransactionXid().getBranchQualifier());
    }
    
    public void removeCurrentTransactionXid() {
    	threadLocalTransactionXid.remove();
    }
    
}
