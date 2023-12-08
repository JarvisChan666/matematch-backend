package com.jvc.matematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jvc.matematch.model.domain.User;
import com.jvc.matematch.model.domain.Team;
import com.jvc.matematch.model.dto.TeamQuery;
import com.jvc.matematch.model.request.TeamJoinRequest;
import com.jvc.matematch.model.request.TeamUpdateRequest;
import com.jvc.matematch.model.vo.TeamUserVO;

import java.util.List;

/**
* @author JarvisChan
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-12-07 15:25:24
*/
/**
 * @author shayu
 * @description 针对表【team(队伍)】的数据库操作Service
 * @createDate 2023-01-30 15:38:39
 */
public interface TeamService extends IService<Team> {

    /**
     *   添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest team, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}