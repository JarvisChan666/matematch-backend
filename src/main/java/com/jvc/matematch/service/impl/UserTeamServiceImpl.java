package com.jvc.matematch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jvc.matematch.model.domain.UserTeam;
import com.jvc.matematch.mapper.UserTeamMapper;
import com.jvc.matematch.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
 * @author JarvisChan
 * @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
 * @createDate 2023-12-07 15:25:12
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
        implements UserTeamService {

}




