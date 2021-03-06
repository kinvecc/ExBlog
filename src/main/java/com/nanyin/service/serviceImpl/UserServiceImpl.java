package com.nanyin.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nanyin.config.AllParamOfUser;
import com.nanyin.config.Author;
import com.nanyin.config.common.Paging;
import com.nanyin.config.common.TimeUtil;
import com.nanyin.mapper.UserMapper;
import com.nanyin.model.Friend;
import com.nanyin.model.Role;
import com.nanyin.model.To.UserAndRoles;
import com.nanyin.model.UserDetail;
import com.nanyin.model.Users;
import com.nanyin.service.PermissionService;
import com.nanyin.service.RoleService;
import com.nanyin.service.UserDetailService;
import com.nanyin.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by NanYin on 2017-10-01 下午9:33.
 * 包名： com.nanyin.service.serviceImpl
 * 类描述：
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserDetailService userDetailService;

    @Autowired
    UserService userService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;

    private static final int BE_USEING = 1 ;

    private static final int OUT_OF_SERVICE = 0 ;

    @Override
    public Users findUsersByName(String name) {
        return userMapper.findUsersByName(name);
    }

    @Override
    public Users findUsersById(int id) {
        return userMapper.findUserById(id);
    }

    @Override
    public Users findUsersByName(String name, int page, int pageNum) {
        return userMapper.findUsersByNameLimit(name, page, pageNum);
    }

    @Override
    public int findAuthorByName(String name) {
        return userMapper.findAuthorByName(name);
    }

    @Override
    public String findUserNameById(int id) {
        return userMapper.findUserNameById(id);
    }

    @Override
    public AllParamOfUser getUserParam(String name) {
        AllParamOfUser allParamOfUser = new AllParamOfUser();

        Users users = userMapper.findUsersByName(name);

        UserDetail userDetail = userDetailService.findUserDetailByUserName(name);

        allParamOfUser.setUsers(users);
        allParamOfUser.setUserDetail(userDetail);

        return allParamOfUser;
    }

    @Override
    public int updateUserMes1(Users users, String name) {
        return userMapper.updateUserMes(users,name);
    }

    private UserDetail setDetail(int userId,
                                String position,
                                java.util.Date data,
                                String address,
                                String sketch){
        UserDetail u1 = new UserDetail();
        u1.setUser_id(userId);
        u1.setAddress(address);
        u1.setBirthday(data);
        u1.setPosition(position);
        u1.setSketch(sketch);
        return u1;
    }

    private Users setUserDetail(String imgMes,
                                String realName,
                                String email
                                ){
        Users users = new Users();
        users.setEmail(email);
        users.setHead(imgMes);
        users.setReal_name(realName);
        return users;
    }

    @Override
    public int updateUserMes(String imgMes,
                             String userName,
                             String realName,
                             String position,
                             String data,
                             String email,
                             String address,
                             String sketch) throws ParseException {
        SimpleDateFormat sdf= TimeUtil.formatToYMD();
        java.util.Date date=sdf.parse(data);
        UserDetail userDetail = userDetailService.findUserDetailByUserName(userName);
        int userId = userService.findAuthorByName(userName);
        if(userDetail == null){
            // 查不到userdetail 说明还没有信息 执行insert操作
            UserDetail u1 = setDetail(userId,position,date,address,sketch);
            userDetailService.insertUserDetailByUserId(u1);
        }else {
            //可以查询到userdetail的信息站执行update操作
            UserDetail u2 = setDetail(userId,position,date,address,sketch);
            userDetailService.updateUserDetailByUserId(u2);
        }

        Users users2 = setUserDetail(imgMes,realName,email);

        return userService.updateUserMes1(users2,userName);
    }

    @Override
    public int updateUserPass(String userName, String newPassWord, String oldPassWord) {
        return userMapper.updateUserPass(userName, newPassWord, oldPassWord);
    }

    @Override
    public Map<String,Object> userAndAuthor(String search, int pageNum) {
        int limit = Paging.LIMIT.getValue();
        Map<String,Object> map = Maps.newHashMap();
        List<Author> authorList = Lists.newLinkedList();

        List<Users> users = userMapper.findAllUsersLimit(search,(pageNum-1) * limit,limit);
        int total = userMapper.findAllUsers(search).size();
        for(int i = 0 ; i < users.size() ; i++) {

            StringBuffer roleString = new StringBuffer(" ") ;
            StringBuffer permissionString = new StringBuffer(" ");
            Author author = new Author();

            appendRoles(roleString,author,users,i);
            appendPermisson(permissionString,author,users,i);
            setAuthor(author,users,i);

            authorList.add(author);
        }
        map.put("data",authorList);
        map.put("count",total);
        map.put("code",0);
        map.put("mes","");
        return map;
    }

    /**
     *
     * @param roleString
     * @param author
     * @param users
     * @param i
     */
    private void appendRoles(StringBuffer roleString,Author author,List<Users> users,int i){
        Set<String> roles = roleService.findRoleByName(users.get(i).getLogin_name());
        for (String role: roles
                ) {
            roleString = roleString.append(" "+role);
        }
        if("".equals(roleString.toString().trim())){
            author.setRoleName("暂无,等待分配");
        }else {
            author.setRoleName(roleString.toString());
        }
    }

    /**
     *
     * @param permissionString
     * @param author
     * @param users
     * @param i
     */

    private void appendPermisson(StringBuffer permissionString,Author author,List<Users> users,int i){
        Set<String> permissions = permissionService.findPermissionByName(users.get(i).getLogin_name());
        for (String s: permissions
                ) {
            permissionString = permissionString.append(" "+s);
        }
        if("".equals(permissionString.toString().trim())){
            author.setPermissionName("暂无,等待分配");
        }else {
            author.setPermissionName(permissionString.toString());
        }
    }

    private void setAuthor(Author author,List<Users> users,int i){
        author.setId(users.get(i).getId());
        author.setLoginName(users.get(i).getLogin_name());
        author.setCreateTime(users.get(i).getCreate_time());
    }


    /***
     *
     * @author NanYin 
     * @date 17-11-29 下午2:28
     * @param [users, roleName]  
     * @return com.nanyin.model.To.UserAndRoles  
     */  
    private UserAndRoles setValue (Users users,String roleName){
        UserAndRoles userAndRoles = new UserAndRoles();
        userAndRoles.setUserId(users.getId());
        userAndRoles.setUserName(users.getLogin_name());
        userAndRoles.setCreateTIme(users.getCreate_time());
        userAndRoles.setEmail(users.getEmail());
        int statusNumber = users.getStatus();
        if(statusNumber == OUT_OF_SERVICE ){
            userAndRoles.setStatus("已停用");
        }
        if(statusNumber == BE_USEING)
        {
            userAndRoles.setStatus("正在使用");
        }
        userAndRoles.setRoleName(roleName);
        return userAndRoles;
    }
    /***
     *
     * @author NanYin 
     * @date 17-11-29 下午2:48  
     * @param [pageNum, userName]
     * @return com.github.pagehelper.Paging
     */  
    @Override
    public List<UserAndRoles> userAndRole(int pageNum) {
        // 固定 limit
        int limit = Paging.LIMIT.getValue() ;
        List<Users> users = findAllUsers((pageNum-1) * limit,limit);
        return listSwap(users);
    }

    private List<UserAndRoles> listSwap(List<Users> users){
        List<UserAndRoles> userAndRoles = Lists.newLinkedList();
        for (Users u: users
             ) {
            String userName = u.getLogin_name();
            Set<String> roleSet =  roleService.findRoleByName(userName);
            String roleName = null;
            Iterator iterator = roleSet.iterator();
            while(iterator.hasNext())
            {
                roleName = (String) iterator.next();
            }
            UserAndRoles userAndRole = setValue(u,roleName);
            userAndRoles.add(userAndRole);
        }
        return userAndRoles;
    }
    

    @Override
    public List<Users> findAllUsers(int page, int limit) {
        return userMapper.findAllUsersListLimit(page, limit);
    }

    @Override
    public List<Users> findAllUsers() {
        return userMapper.findAllUsersList();
    }

    /**
     * 确保只提取到非隐私信息
     * @param users
     * @return
     */
    private Users insurePrivacyMes(Users users){
        Users privacy = new Users();
        privacy.setId(users.getId());
        privacy.setLogin_name(users.getLogin_name());
        privacy.setEmail(users.getEmail());
        privacy.setHead(users.getHead());
        privacy.setStatus(users.getStatus());
        return privacy;
    }

    @Override
    public Users checkUserMes(int id) {
        return insurePrivacyMes(userMapper.findUserById(id));
    }

    @Override
    public int updateUserStatus(String review, String status, int userId) {
        int statusS = Integer.parseInt(status);
        return userMapper.updateUserStatus(review, statusS, userId);
    }


}
