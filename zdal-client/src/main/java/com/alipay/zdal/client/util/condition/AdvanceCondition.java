package com.alipay.zdal.client.util.condition;

import com.alipay.zdal.common.sqljep.function.Comparative;
import com.alipay.zdal.common.sqljep.function.ComparativeAND;
import com.alipay.zdal.common.sqljep.function.ComparativeBaseList;
import com.alipay.zdal.common.sqljep.function.ComparativeOR;

public class AdvanceCondition extends SimpleCondition{
	public static final int GT=Comparative.GreaterThan;
	public static final int LT=Comparative.LessThan;
	
	public static final int GTE=Comparative.GreaterThanOrEqual;
	public static final int LTE=Comparative.LessThanOrEqual;

	@Deprecated
	public static ComparativeAND getAnd(ComparativeBaseList l,Comparative c){
		if(l!=null&&l instanceof ComparativeAND){
			l.addComparative(c);
			return (ComparativeAND)l;
		}else{
			ComparativeAND a= new ComparativeAND();
			a.addComparative(c);
			return a;
		}
	}
	public static Comparative or(Comparative parent,Comparative target){
		if(parent == null){
			ComparativeOR or= new ComparativeOR();
			or.addComparative(target);
			return or;
		}else{
			if(parent instanceof ComparativeOR){
				((ComparativeOR) parent).addComparative(target);
				return parent;
			}else{
			ComparativeOR or = new ComparativeOR();
			or.addComparative(parent);
			or.addComparative(target);
			return or;
			}
		}
	}
	
	public static Comparative and(Comparative parent,Comparative target){
		if(parent == null){
			ComparativeAND and = new ComparativeAND();
			and.addComparative(target);
			return and;
		}else{
			if(parent instanceof ComparativeAND){
			
				ComparativeAND and = ((ComparativeAND) parent);
				if(and.getList().size() == 1){
					and.addComparative(target);
					return and;
				}else{
					ComparativeAND andNew = new ComparativeAND();
					andNew.addComparative(and);
					andNew.addComparative(target);
					return andNew;
				}
				
			}else{
				ComparativeAND and = new ComparativeAND();
				and.addComparative(parent);
				and.addComparative(target);
				return and;
			}
		}
	}
	@Deprecated
	public static ComparativeOR getOr(ComparativeBaseList l,Comparative c){
		if(l!=null&&l instanceof ComparativeOR){
			l.addComparative(c);
			return (ComparativeOR)l;
		}else{
			ComparativeOR a= new ComparativeOR();
			a.addComparative(c);
			return a;
		}
	}
	
}
