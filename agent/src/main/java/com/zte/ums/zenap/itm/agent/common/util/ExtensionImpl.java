package com.zte.ums.zenap.itm.agent.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target({ ElementType.TYPE}) 
public @interface ExtensionImpl {

	String entensionId();
    String[] keys() default {};
    boolean isSingleton() default  false;
}
