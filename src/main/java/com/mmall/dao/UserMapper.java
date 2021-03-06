package com.mmall.dao;

import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checekUserName(String username);

    int checekEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUserName(@Param("username") String username);

    int checkAnswer(@Param("username") String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    int checkPassword(@Param("password")String password,@Param("id")int id);

    int checkEmailByUid(@Param("email")String email,@Param("id")int id);

}