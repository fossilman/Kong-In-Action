package model.bo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author krame
 * @description：版本对象
 * @date ：Created in 2019-09-17 10:58
 */
@Setter
@Getter
public class VersionBo {

    private int oldVersionRebootNum;
    private int newVersionRebootNum;

    private List<ServerBo> oldRebootLists;
    private List<ServerBo> newRebootLists;
    private List<ServerBo> rebootLists;
    private String oldReboots;
    private String newReboots;
}
