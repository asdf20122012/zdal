package com.alipay.zdal.common.sqljep.function;


/**
 * AND节点
 * 在实际的SQL中，实际上是类似
 * [Comparative]              [comparative]
 * 			\                  /
 * 			  \				  /
 *             [ComparativeAnd]
 *             
 * 类似这样的节点出现
 * 
 * @author shenxun
 *
 */
public class ComparativeAND extends ComparativeBaseList{
	
	public ComparativeAND(int function, Comparable<?> value) {
		super(function, value);
	}
	
	public ComparativeAND(){
	}
	
	public ComparativeAND(Comparative item){
		super(item);
	}
	


	@Override
	protected String getRelation() {
		return " and ";
	}

}
