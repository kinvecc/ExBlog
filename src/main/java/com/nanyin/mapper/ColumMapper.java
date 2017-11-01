package com.nanyin.mapper;

import com.nanyin.model.Column;
import org.apache.ibatis.annotations.*;

import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.Set;

/**
 * Created by NanYin on 2017-10-02 下午1:15.
 * 包名： com.nanyin.mapper
 * 类描述：
 */
@Mapper
public interface ColumMapper {

    // 专题内文章由多到少排序
    @Select("SELECT c.title,c.C_create_time,c.image " +
            "FROM social_blog.`Column` c,social_blog.Column_paper cp " +
            "WHERE c.id = cp.Column_id " +
            "GROUP BY c.title,c.C_create_time,c.image " +
            "ORDER BY COUNT(cp.paper_id) DESC LIMIT 0,5")
    List<Column> findColumByPaperCount();

//   查询所有专题信息
    @Select("SELECT * FROM social_blog.`Column`")
    List<Column> findAllColumn();

    @Select("SELECT COUNT(*) FROM social_blog.paper p, social_blog.`Column` c ,social_blog.Column_paper cp ,social_blog.users u " +
            "WHERE cp.Column_id = c.id AND cp.paper_id = p.id AND u.id = p.author AND c.title = #{title} AND u.login_name=#{name}")
    int findCountByTitle(@Param("title") String title,@Param("name") String name);

    @Select("SELECT c.title " +
            "FROM social_blog.paper p, social_blog.`Column` c ,social_blog.Column_paper cp ,social_blog.users u , social_blog.tag t " +
            "WHERE cp.Column_id = c.id AND cp.paper_id = p.id AND u.id = p.author AND u.login_name=#{name}")
    Set<String> findCoumnByUser(String name);

    /**
     *  根据插入的column title 查询到对应的id
     * @param title column title
     * @return column id
     */
    @Select("SELECT id FROM social_blog.`Column` WHERE title=#{title}")
    int findColumnId(String title);

    /**
     * 专题和paper对应的表
     * @param columnId
     * @param paperId
     * @return
     */
    @Insert("INSERT INTO social_blog.Column_paper(Column_id,paper_id) VALUES(#{Column_id},#{paper_id})")
    int insertColumnPaper(@Param("Column_id") int columnId,@Param("paper_id") int paperId);

    /**
     * 通过paper的id查询所属的column
     * @param id paperid
     * @return column
     */
    @Select("SELECT c.* " +
            "FROM social_blog.`Column` c " +
            "LEFT JOIN social_blog.Column_paper cp ON c.id = cp.Column_id " +
            "LEFT JOIN social_blog.paper p ON cp.paper_id = p.id WHERE p.id = #{id}  ")
    Column findColumnByPaperId(int id);

    @Select("SELECT id FROM social_blog.`Column` WHERE title = #{title}")
    int findColumnIdByTitle(String title);

    @Update("UPDATE social_blog.Column_paper SET Column_id = #{column_id} WHERE paper_id = #{paper_id}")
    int updateColumnByPaperId(@Param("paper_id") int paperId,@Param("column_id") int columnId);

}