package com.san30.enhancer;

import java.lang.reflect.Method;

import javassist.CtMethod;

/**
 * Hello world!
 * 
 */
public class App {

	public static void main(String[] args) throws SecurityException, Exception {
		new Test().doOut();
		ClassEnhancedGenerator.enhancedMethod(bsh.Token.class,
			new Method[]{bsh.Token.class.getDeclaredMethod("toString")}, InjectType.BEFORE, new ClassInjectProvider() {

				public String injectCode(CtMethod ctMethod) throws Exception {
					return "System.out.println(\"hello world\");";
				}

			});
		ClassEnhancedGenerator.enhancedMethod(com.san30.enhancer.Test.class,
				new Method[]{com.san30.enhancer.Test.class.getDeclaredMethod("doOut")}, InjectType.BEFORE, new ClassInjectProvider() {

					public String injectCode(CtMethod ctMethod) throws Exception {
						return "System.out.println(\"hello world\");";
					}

				});
		new Test().doOut();
	}

}
