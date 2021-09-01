package org.fibonacci.devopscenter.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author krame
 * @description：
 * @date ：Created in 2019-09-18 15:28
 */
@Component
public class GitlabConstants {

    @Value("${gitlab.commits}")
    public String gitlabCommits;


    @Value("${gitlab.projects}")
    public String gitlabProjects;
}
