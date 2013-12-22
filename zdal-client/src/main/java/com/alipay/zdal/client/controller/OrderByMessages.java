package com.alipay.zdal.client.controller;

import java.util.List;

import com.alipay.zdal.parser.visitor.OrderByEle;



public interface OrderByMessages {

	public abstract List<OrderByEle> getOrderbyList();

}