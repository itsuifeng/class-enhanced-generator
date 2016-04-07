package com.san30.enhancer;

import java.lang.reflect.Method;
import java.net.URI;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * Desc:TODO
 * 
 * @author zhangwei<wei.zw@corp.netease.com>
 * @since 2016年1月14日 下午9:19:34
 * @version v 0.1
 */
public final class ClassEnhancedGenerator {
	
	private ClassEnhancedGenerator() {
	}

	/**
	 * 类方法增强<BR>
	 *
	 * 对指定类的方法进行代码增强(将指定的原方法名改为$enhanced,同时复制原方法名进行代码注入)
	 * 
	 * @param className
	 *            待增强的类名
	 * @param methodName
	 *            待增强的方法名
	 * @param provider
	 *            {@link ClassInjectProvider}实现类
	 * @throws Exception
	 */
	public static void enhancedMethod(Class<?> cls, Method[] methods, InjectType injectType, ClassInjectProvider provider)
			throws Exception {
		CtClass ctClass = ClassPool.getDefault().get(cls.getName());
		for (int i = 0; i < methods.length; i++) {
			injectCodeForMethod(ctClass, methods[i].getName(), injectType, provider);
		}
		String resource = cls.getName().replace(".", "/") + ".class";
		URI uri = ClassLoader.getSystemClassLoader().getResource(resource).toURI();
		String classFilePath = uri.getRawPath().substring(0, uri.getRawPath().length() - resource.length());
		ctClass.writeFile(classFilePath);
	}

	/***
	 * 注入增强代码
	 * 
	 * @param ctClass
	 * @param methodName
	 * @param injectType
	 * @param provider
	 * @throws Exception
	 * @author zhangwei<wei.zw@corp.netease.com>
	 */
	private static void injectCodeForMethod(CtClass ctClass, String methodName, InjectType injectType,
			ClassInjectProvider provider) throws Exception {
		CtMethod oldMethod = ctClass.getDeclaredMethod(methodName);
		// 修改原有的方法名称为"方法名$enhanced"，如果已存在该方法则返回
		String originalMethod = methodName + "$enhanced";
		CtMethod[] methods = ctClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			CtMethod method = methods[i];
			if (method.getName().equals(originalMethod)) {
				return;
			}
		}
		oldMethod.setName(originalMethod);
		// 增加代码,复制原来的方法名作为增强的新方法,同时调用原有方法即"方法名$enhanced"
		CtMethod enhancedMethod = CtNewMethod.copy(oldMethod, methodName, ctClass, null);
		// 对复制的方法注入代码
		StringBuffer methodBody = new StringBuffer();
		methodBody.append("{");
		switch (injectType) {
		case BEFORE:
			methodBody.append(provider.injectCode(enhancedMethod) == null ? "" : provider.injectCode(enhancedMethod));
			methodBody.append("return " + originalMethod + "($$); ");
			break;
		case AFTER:
			methodBody.append("try{");
			methodBody.append("return " + originalMethod + "($$); ");
			methodBody.append("}finally{");
			methodBody.append(provider.injectCode(enhancedMethod));
			methodBody.append("}");
			break;
		default:
			String injectCode = provider.injectCode(enhancedMethod);
			methodBody.append(injectCode);
		}
		methodBody.append("}");
		enhancedMethod.setBody(methodBody.toString());
		ctClass.addMethod(enhancedMethod);
	}
}
