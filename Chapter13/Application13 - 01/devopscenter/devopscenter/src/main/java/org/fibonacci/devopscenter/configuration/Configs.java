package org.fibonacci.devopscenter.configuration;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author krame
 * @date 2019-12-13
 */
@Data
@Component
public class Configs {

    private Integer deployCheckHealthTimes = 6;
}
