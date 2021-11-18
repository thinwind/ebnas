package io.github.thinwind.clusterhouse.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
*
* 用于标注Controller方法不要包装成CommonResult
*
* @author ShangYh <shangyehua@ebchinatech.com>
* @since 2020-09-01 15:10
*
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoNotWrap {
    
}