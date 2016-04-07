package com.san30.enhancer;

import javassist.CtMethod;

/**
 * Desc:TODO
 * 
 * @author zhangwei<wei.zw@corp.netease.com>
 * @since 2016年1月14日 下午9:21:51
 * @version v 0.1
 */
public interface ClassInjectProvider {

	/**
	 * 对指定的方法注入代码
	 *
	 * @param ctMethod
	 *            CtMethod
	 * @return
	 */
	public String injectCode(final CtMethod ctMethod) throws Exception;

}
