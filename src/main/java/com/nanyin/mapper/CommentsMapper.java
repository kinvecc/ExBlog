package com.nanyin.mapper;

import com.nanyin.model.Comments;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by NanYin on 2017-10-02 下午8:01.
 * 包名： com.nanyin.mapper
 * 类描述：
 */
@Mapper
public interface CommentsMapper {
    @Select("SELECT COUNT(*) " +
            "FROM social_blog.comments c ,social_blog.paper p " +
            "WHERE p.id = c.comments_paper AND p.id=#{title}")
    int findCommentCountByTitle(int title);

    @Select("SELECT COUNT(*) " +
            "FROM social_blog.comments c ,social_blog.paper p " +
            "WHERE p.id = c.comments_paper AND p.id=#{id}")
    int findCommentCountByid(int id);

    /**
     * 删除评论
     * @param id
     * @return
     */
    @Delete("DELETE FROM social_blog.comments WHERE id=#{id}")
    int deleteCommentById(int id);

    /**
     * 查询对应paper的所有comments
     * @param id
     * @return
     */
    @Select("SELECT c.* FROM social_blog.comments c " +
            "LEFT JOIN social_blog.paper p ON c.comments_paper = p.id " +
            "WHERE comments_paper=#{id}")
    List<Comments> findAllCommentsByPaperId(int id);

    @Insert("INSERT INTO social_blog.comments(comments_content,comments_time,comments_paper,comments_user)" +
            "VALUES(#{comments.comments_content},#{comments.comments_time},#{comments.comments_paper},#{comments.comments_user})")
    int insertComments(@Param("comments") Comments comments);

    /**
     * 首页评论控制 limit 5
     * @return
     */
    @Select("SELECT * FROM social_blog.comments ORDER BY comments_time LIMIT #{pageNum},#{limit}")
    List<Comments> findAllCommentsOrderByTime(@Param("pageNum") int pageNum,@Param("limit") int limit);

    /**
     * 根据user查询近期的所有评论
     * @param userId
     * @return
     */
    @Select("SELECT * " +
            "FROM social_blog.comments " +
            "WHERE comments_user = #{userId} ORDER BY comments_time DESC LIMIT #{pageNum},#{pageSize}")
    List<Comments> findCommentsByUserId(@Param("userId") int userId,
                                        @Param("pageNum") int pageNum,
                                        @Param("pageSize") int pageSize);
}
