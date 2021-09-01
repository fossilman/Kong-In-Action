package org.fibonacci.framework.logcontrol;


import java.lang.annotation.*;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HttpLogControlConfig {


    /**
     * items will be excluded from all, if not specify, all will be printed
     * justIncludes will be ineffective
     * @return
     */
    HttpLogItem[] excludesFromAll() default {};

    /**
     * only print specifiled items, if not sepcify, all will be printed
     * will be effective only when excludesFromAll is not explicitly specify
     *
     * @return
     */
    HttpLogItem[] justIncludes() default {};

    /* 0%-100%, just need provide 0 or 100 or 95, default all log records is printed */
    int sampleRate() default 100;



}
