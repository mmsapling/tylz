package com.tylz.aelos.bean;

import java.io.Serializable;

/**
 * @项目名: Aelos1
 * @包名: com.gxy.bean
 * @类名: Action
 * @创建者: 陈选文
 * @创建时间: 2016-6-24 下午4:09:27
 * @描述: TODO
 * 
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class Status implements Serializable
{
	public int		id;
	public int		actionId;
	public int[]    arr = new int[16];
	public boolean	isShow = false;
	public int 		progress = 15;
}
